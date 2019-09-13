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
import me.superblaubeere27.jobf.utils.ConsoleUtils;
import me.superblaubeere27.jobf.utils.Templates;
import me.superblaubeere27.jobf.utils.Utils;
import me.superblaubeere27.jobf.utils.VersionComparator;
import me.superblaubeere27.jobf.utils.values.ConfigManager;
import me.superblaubeere27.jobf.utils.values.Configuration;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
    public static boolean VERBOSE = false;
    //#if buildType=="gui"
    private static GUI gui;
    //#endif

    public static void main(String[] args) throws Exception {
        if (JObf.class.getPackage().getImplementationVersion() == null) {
            VERBOSE = true;
        }


        Class.forName(JObfImpl.class.getCanonicalName());
        JObf.log.setUseParentHandlers(false);
        JObf.log.setLevel(Level.ALL);
        JObf.log.setFilter(record -> true);


        JObf.log.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (!VERBOSE && record.getLevel().intValue() < Level.CONFIG.intValue())
                    return;

                synchronized (log) {
                    //                    if (record.getLevel().intValue() < Level.INFO.intValue()) return;
                    if (record.getMessage() == null)
                        return;
//                System.out.println(record.getMessage() + "/" + record.getParameters());
                    //#if buildType=="gui"
                    if (gui != null) {
                        try {
                            gui.logArea.append(String.format(record.getMessage(), record.getParameters()) + "\n");
                        } catch (Exception e) {
                            gui.logArea.append(record.getMessage() + "\n");
                        }
                        gui.scrollDown();
//                    System.out.println("lloool");
                    }
                    //#endif

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

        String version = checkForUpdate();

        OptionParser parser = new OptionParser();
        parser.accepts("jarIn").withRequiredArg().required();
        parser.accepts("jarOut").withRequiredArg();
        parser.accepts("config").withOptionalArg().ofType(File.class);
        parser.accepts("cp").withOptionalArg().describedAs("ClassPath").ofType(File.class);
        parser.accepts("scriptFile").withOptionalArg().describedAs("[Not documented] JS script file").ofType(File.class);
        parser.accepts("threads").withOptionalArg().ofType(Integer.class).defaultsTo(Runtime.getRuntime().availableProcessors()).describedAs("Thread count; Please don't use more threads than you have cores. It might hang up your system");
        parser.accepts("verbose").withOptionalArg();
        parser.accepts("help").forHelp();
        parser.accepts("version").forHelp();

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

            if (options.has("verbose")) {
                VERBOSE = true;
            }

            String jarIn = (String) options.valueOf("jarIn");
            String jarOut = (String) options.valueOf("jarOut");
            File configPath = options.has("config") ? (File) options.valueOf("config") : null;

            String scriptContent = "";

            if (options.has("scriptFile")) {
                scriptContent = new String(Files.readAllBytes(((File) options.valueOf("scriptFile")).toPath()), StandardCharsets.UTF_8);
            }

            boolean outdated = version != null;
            boolean embedded = false;
            int threads = Math.max(1, (Integer) options.valueOf("threads"));

            List<String> libraries = new ArrayList<>();

            if (options.has("cp")) {
                for (Object cp : options.valuesOf("cp")) {
                    libraries.add(cp.toString());
                }
            }

            runObfuscator(jarIn, jarOut, configPath, libraries, outdated, embedded, version, scriptContent, threads);
        } catch (OptionException e) {
            System.err.println("ERROR: " + e.getMessage() + " (Tip: try --help and even if you specified a config you have to specify an input and output jar)");
            e.printStackTrace();

            if (GraphicsEnvironment.isHeadless()) {
                return;
            }

            if (
                //#if buildType=="gui"
                    false &&
                            //#endif
                            true
            ) {
                log.severe("");
                log.severe("This build is a headless build, so GUI is not available");
                return;
            }

            //#if buildType=="gui"
            System.out.println("Starting in GUI Mode");

            try {
                if (Utils.isWindows()) {
                    UIManager.setLookAndFeel("com.bulenkov.darcula.DarculaLaf");
                } else {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
            } catch (Exception e1) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e2) {
                    e1.printStackTrace();
                }
            }

            Templates.loadTemplates();

            Packager.INSTANCE.isEnabled();

            gui = new GUI(version);
            //#endif
//            e.printStackTrace();
//            parser.printHelpOn(System.out);

//            e.printStackTrace();
        }
    }

    public static boolean runEmbedded(String jarIn, String jarOut, File configPath, List<String> libraries, String scriptContent) throws IOException, InterruptedException {
        return runObfuscator(jarIn, jarOut, configPath, libraries, false, true, null, scriptContent, Runtime.getRuntime().availableProcessors());
    }

    private static boolean runObfuscator(String jarIn, String jarOut, File configPath, List<String> libraries, boolean outdated, boolean embedded, String version, String scriptContent, int threads) throws IOException, InterruptedException {
        if (outdated) {
            log(ConsoleUtils.formatBox("Update available", true, Arrays.asList(
                    "An update is available: v" + version,
                    "(Current version: " + SHORT_VERSION + ")",
                    "The latest version can be downloaded at",
                    "https://github.com/superblaubeere27/obfuscator/releases/latest"
            )));
        }

        log("        _      __                     _             \n" +
                "       | |    / _|                   | |            \n" +
                "   ___ | |__ | |_ _   _ ___  ___ __ _| |_ ___  _ __ \n" +
                "  / _ \\| '_ \\|  _| | | / __|/ __/ _` | __/ _ \\| '__|\n" +
                " | (_) | |_) | | | |_| \\__ \\ (_| (_| | || (_) | |   \n" +
                "  \\___/|_.__/|_|  \\__,_|___/\\___\\__,_|\\__\\___/|_|   \n" +
                "   " + SHORT_VERSION + (embedded ? " (EMBEDDED)" : outdated ? " (OUTDATED)" : " (LATEST)"));
        log("");

        log("");


        log(ConsoleUtils.formatBox("Configuration", false, Arrays.asList(
                "Input:      " + jarIn,
                "Output:     " + jarOut,
                "Config:     " + (configPath != null ? configPath.getPath() : "")
        )));

        Configuration config = new Configuration(jarIn, jarOut, scriptContent, libraries);

        if (configPath != null) {
            if (!configPath.exists()) {
                System.err.println("Config file doesn't exist");
                return false;
            }

            config = ConfigManager.loadConfig(new String(ByteStreams.toByteArray(new FileInputStream(configPath)), StandardCharsets.UTF_8));
        } else {
            log.warning("");
            log.warning(ConsoleUtils.formatBox("WARNING", true, Arrays.asList(
                    "You didn't specify a configuration, so the ",
                    "obfuscator is using the default configuration.",
                    " ",
                    "This might cause the output jar to be invalid.",
                    "If you want to create a config, please start the",
                    "obfuscator in GUI Mode (run it without cli args).",
                    "",
                    "The program will resume in 2 sec"
            )));
            log.warning("");
            Thread.sleep(2000);
        }

        config.setInput(jarIn);
        config.setOutput(jarOut);

        config.getLibraries().addAll(libraries);

        if (!scriptContent.isEmpty()) config.setScript(scriptContent);

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

        JObfImpl.INSTANCE.setThreadCount(threads);

        try {
            JObfImpl.INSTANCE.processJar(config);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Checks if a new version is available
     *
     * @return If the current version is up to date it will return null. If the version is outdated it will return the name of the latest version
     */
    private static String checkForUpdate() {
        try {
            String version = JObf.class.getPackage().getImplementationVersion();

            // If the ImplementationVersion is null, the build wasn't built by maven.
            if (version == null) return null;


            InputStream inputStream = new URL("https://raw.githubusercontent.com/superblaubeere27/obfuscator/master/version").openStream();


            String latestVersion = new String(ByteStreams.toByteArray(inputStream), StandardCharsets.UTF_8);

            VersionComparator comparator = new VersionComparator();

            if (comparator.compare(version, latestVersion) < 0) {
                return latestVersion;
            }
        } catch (Exception e) {
            log.warning("Update check failed: " + e.getMessage());
        }
        return null;
    }

    private static void log(String line) {
        log.info(line);
    }

    public static void report(String s) {

    }
}
