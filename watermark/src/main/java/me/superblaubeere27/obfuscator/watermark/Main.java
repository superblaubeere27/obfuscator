package me.superblaubeere27.obfuscator.watermark;

import com.google.common.io.Files;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String args[]) {
        OptionParser parser = new OptionParser();

        AbstractOptionSpec<Void> helpOption = parser.accepts("help").forHelp();
        ArgumentAcceptingOptionSpec<String> extract = parser.accepts("extract").withOptionalArg().describedAs("Extracts the watermark");

        ArgumentAcceptingOptionSpec<File> generateConfig = parser.accepts("generateConfig").withOptionalArg().ofType(File.class).describedAs("Generates a new random configuration");
        ArgumentAcceptingOptionSpec<File> inputOption = parser.accepts("input").requiredUnless(generateConfig).withOptionalArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<File> outputOption = parser.accepts("output").availableUnless(extract).requiredUnless(generateConfig).withOptionalArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<File> configOption = parser.accepts("config").requiredUnless(generateConfig).withOptionalArg().ofType(File.class);
        ArgumentAcceptingOptionSpec<File> watermarkOption = parser.accepts("watermark").availableUnless(outputOption).requiredUnless(generateConfig).withOptionalArg().ofType(File.class);

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
            }

            if (optionSet.has(outputOption)) {

            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getLocalizedMessage());
            System.err.println("Try --help for help.");
        }
    }

}
