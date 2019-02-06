/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27.jobf;

import com.google.common.io.ByteStreams;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import me.superblaubeere27.jobf.processors.packager.Packager;
import me.superblaubeere27.jobf.ui.GUI;
import me.superblaubeere27.jobf.util.values.ConfigManager;
import me.superblaubeere27.jobf.util.values.Configuration;
import me.superblaubeere27.jobf.utils.ConsoleUtils;
import me.superblaubeere27.jobf.utils.Templates;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class JObf {
    public static final String SHORT_VERSION = (JObf.class.getPackage().getImplementationVersion() == null ? "DEV" : "v" + JObf.class.getPackage().getImplementationVersion()) + " by superblaubeere27";
    public static final String VERSION = "obfuscator " + (JObf.class.getPackage().getImplementationVersion() == null ? "DEV" : "v" + JObf.class.getPackage().getImplementationVersion()) + " by superblaubeere27";
    public final static Logger log = Logger.getLogger("obfuscator");
    private static GUI gui;

    public static void main(String[] args) throws Exception {
        Class.forName(JObfImpl.class.getCanonicalName());
        JObf.log.setUseParentHandlers(false);
        JObf.log.setLevel(Level.ALL);
        JObf.log.setFilter(record -> true);


        JObf.log.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                synchronized (log) {
                    //                    if (record.getLevel().intValue() < Level.INFO.intValue()) return;
                    if (record.getMessage() == null)
                        return;
//                System.out.println(record.getMessage() + "/" + record.getParameters());
                    if (gui != null) {
                        try {
                            gui.logArea.append(String.format(record.getMessage(), record.getParameters()) + "\n");
                        } catch (Exception e) {
                            gui.logArea.append(record.getMessage() + "\n");
                        }
                        gui.scrollDown();
//                    System.out.println("lloool");
                    }

                    try {
                        System.out.println(String.format(record.getMessage(), record.getParameters()));
                    } catch (Exception e) {
                        System.out.println(record.getMessage());
                    }
                }
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        });


        OptionParser parser = new OptionParser();
        parser.accepts("help").forHelp();
        parser.accepts("version").forHelp();
        parser.accepts("config").withOptionalArg().ofType(File.class);
        parser.accepts("jarIn").withRequiredArg().required();
        parser.accepts("jarOut").withRequiredArg();
        parser.accepts("cp").withOptionalArg().describedAs("ClassPath").ofType(File.class);
        parser.accepts("scriptFile").withOptionalArg().describedAs("[Not documented] JS script file").ofType(File.class);
        parser.accepts("threads").withOptionalArg().ofType(Integer.class).defaultsTo(Runtime.getRuntime().availableProcessors()).describedAs("Thread count; Please don't use more threads than you have cores. It might hang up your system");


        try {
            OptionSet options = parser.parse(args);
            if (options.has("help")) {
                System.out.println(VERSION);
                parser.printHelpOn(System.out);
                return;
            } else if (options.has("version")) {
                System.out.println(VERSION);
                return;
            }

            String jarIn = (String) options.valueOf("jarIn");
            String jarOut = (String) options.valueOf("jarOut");


            log("        _      __                     _             \n" +
                    "       | |    / _|                   | |            \n" +
                    "   ___ | |__ | |_ _   _ ___  ___ __ _| |_ ___  _ __ \n" +
                    "  / _ \\| '_ \\|  _| | | / __|/ __/ _` | __/ _ \\| '__|\n" +
                    " | (_) | |_) | | | |_| \\__ \\ (_| (_| | || (_) | |   \n" +
                    "  \\___/|_.__/|_|  \\__,_|___/\\___\\__,_|\\__\\___/|_|   \n" +
                    "   " + SHORT_VERSION);
            log("");

            log(ConsoleUtils.formatBox("Configuration", false, Arrays.asList(
                    "Input:      " + jarIn,
                    "Output:     " + jarOut,
                    "Config:     " + options.valueOf("config")
            )));



            List<String> libraries = new ArrayList<>();

            if (options.has("cp")) {
                for (Object cp : options.valuesOf("cp")) {
                    libraries.add(cp.toString());
                }
            }

            String scriptContent = "";

            if (options.has("scriptFile")) {
                scriptContent = new String(Files.readAllBytes(((File) options.valueOf("scriptFile")).toPath()), StandardCharsets.UTF_8);
            }

            JObfImpl impl = new JObfImpl();

            Configuration config = new Configuration(jarIn, jarOut, scriptContent, libraries);

            if (options.has("config")) {
                File configFile = (File) options.valueOf("config");

                if (!configFile.exists()) {
                    System.err.println("Config file doesn't exist");
                    return;
                }

                config = ConfigManager.loadConfig(new String(ByteStreams.toByteArray(new FileInputStream(configFile)), StandardCharsets.UTF_8));
            }

            config.setInput(jarIn);
            config.setOutput(jarOut);

            if (!scriptContent.isEmpty()) config.setScript(scriptContent);


            int threads = (Integer) options.valueOf("threads");

            if (threads > Runtime.getRuntime().availableProcessors()) {
                log.warning("");
                log.warning(ConsoleUtils.formatBox("WARNING", true, Arrays.asList(
                        "You selected more threads than your cpu has cores.",
                        "",
                        "I would strongly advise against it because",
                        "it WILL make the obfuscation slower and also",
                        "might hang up your system. " + threads + " threads > " + Runtime.getRuntime().availableProcessors() + " cores",
                        "",
                        "The program will resume in 10s. Please think about your decision"
                )));
                Thread.sleep(10000);
            }

            impl.setThreadCount(threads);

            try {
                impl.processJar(config);
            } catch (Exception e) {
                System.err.println("ERROR: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        } catch (OptionException e) {
            System.err.println("ERROR: " + e.getMessage() + " (Tip: try --help and even if you specified a config you have to specify an input and output jar)");
            e.printStackTrace();

            if (GraphicsEnvironment.isHeadless()) {
                return;
            }

            System.out.println("Starting in GUI Mode");

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            Templates.loadTemplates();

            Packager.INSTANCE.isEnabled();
            gui = new GUI();
//            e.printStackTrace();
//            parser.printHelpOn(System.out);

//            e.printStackTrace();
        }
    }

    private static void log(String line) {
        log.info(line);
    }

    public static void report(String s) {

    }
}
