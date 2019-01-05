/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27.obfuscator.watermark;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Main {

    public static void main(String args[]) {
        OptionParser parser = new OptionParser();

        AbstractOptionSpec<Void> helpOption = parser.accepts("help").forHelp();
        ArgumentAcceptingOptionSpec<String> extract = parser.accepts("extract").withOptionalArg().describedAs("Extracts the watermark");

        ArgumentAcceptingOptionSpec<File> generateConfig = parser.accepts("generateConfig").withOptionalArg().ofType(File.class).describedAs("Generates a new random configuration");
        ArgumentAcceptingOptionSpec<File> inputOption = parser.accepts("input").requiredUnless(generateConfig).withOptionalArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<File> outputOption = parser.accepts("output").availableUnless(extract).requiredUnless(extract).withOptionalArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<File> configOption = parser.accepts("config").requiredUnless(generateConfig).withOptionalArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<String> watermarkOption = parser.accepts("watermark").availableIf(outputOption).requiredIf(outputOption).withOptionalArg().ofType(String.class);

        try {
            OptionSet optionSet = parser.parse(args);

            if (optionSet.has(helpOption)) {
                try {
                    parser.printHelpOn(System.err);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            if (optionSet.has(generateConfig)) {
                try {
                    File file = generateConfig.value(optionSet);
                    Files.write(Config.generateConfig().toJsonObject().toString().getBytes(StandardCharsets.UTF_8), file);
                    System.out.println("Config was written to " + file);
                } catch (IOException e) {
                    System.out.println("Error while generating config:");
                    e.printStackTrace();
                }
            } else {
                try {
                    File configFile = configOption.value(optionSet);

                    if (!configFile.exists()) {
                        System.out.println("Config file doesn't exist");
                        return;
                    }
                    Config config = Config.fromJson(new JsonParser().parse(new InputStreamReader(new FileInputStream(configFile))).getAsJsonObject());

                    if (optionSet.has(outputOption)) {
                        File input = inputOption.value(optionSet);
                        File output = outputOption.value(optionSet);
                        String watermark = watermarkOption.value(optionSet);

                        if (!input.exists()) {
                            System.err.println("The input file does not exist");
                            return;
                        }

                        if (!output.exists() && !output.createNewFile()) {
                            System.err.println("Failed to create input file");
                            return;
                        }

                        ZipFile zipFile = new ZipFile(input);
                        ZipOutputStream outStream = new ZipOutputStream(new FileOutputStream(output));

                        Enumeration<? extends ZipEntry> entries = zipFile.entries();

                        while (entries.hasMoreElements()) {
                            ZipEntry zipEntry = entries.nextElement();

                            byte[] entryData = ByteStreams.toByteArray(zipFile.getInputStream(zipEntry));

                            if (zipEntry.getName().endsWith(".class")) {
                                try {
                                    ClassReader reader = new ClassReader(entryData);

                                    ClassWriter writer = new ClassWriter(0);
                                    reader.accept(writer, 0);

                                    writer.newUTF8(config.getMagicBytes() + Encryption.encrypt(watermark, config.getKey()));

                                    entryData = writer.toByteArray();
                                    System.out.println(watermark + " -> " + zipEntry.getName());
                                } catch (ClassTooLargeException | MethodTooLargeException e) {
                                    System.err.println("ERROR in " + zipEntry.getName());
                                    e.printStackTrace();
                                }
                            }

                            outStream.putNextEntry(new ZipEntry(zipEntry.getName()));
                            outStream.write(entryData);
                            outStream.closeEntry();
                        }
                        outStream.close();
                    } else if (optionSet.has(extract)) {
                        File input = inputOption.value(optionSet);

                        if (!input.exists()) {
                            System.err.println("The input file does not exist");
                            return;
                        }

                        ZipFile zipFile = new ZipFile(input);
                        Enumeration<? extends ZipEntry> entries = zipFile.entries();

                        while (entries.hasMoreElements()) {
                            ZipEntry zipEntry = entries.nextElement();

                            byte[] entryData = ByteStreams.toByteArray(zipFile.getInputStream(zipEntry));

                            if (zipEntry.getName().endsWith(".class")) {
                                try {
                                    ClassReader reader = new ClassReader(entryData);

                                    ClassVisitor clazzNode = new ClassNode();
                                    reader.accept(clazzNode, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);

                                    // By ItzSomebody (partially)

                                    char[] chars = new char[reader.getMaxStringLength()];

                                    for (int i = 0; i < reader.getItemCount(); i++) {
                                        int getItem = reader.getItem(i);
                                        String UTF = reader.readUTF8(getItem, chars);

                                        if (UTF != null && UTF.startsWith(config.getMagicBytes())) {
                                            if (UTF.length() > 6) {
                                                String watermark = Encryption.decrypt(UTF.substring(config.getMagicBytes().length()), config.getKey());
                                                System.out.println("Found watermark in " + zipEntry.getName() + ": \"" + watermark + "\"");
                                            }
                                        }
                                    }
                                } catch (ClassTooLargeException | MethodTooLargeException e) {
                                    System.err.println("ERROR in " + zipEntry.getName());
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (JsonIOException | JsonSyntaxException | IOException e) {
                    System.err.println("ERROR: ");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getLocalizedMessage());
            System.err.println("Try --help for help.");
        }
    }

}
