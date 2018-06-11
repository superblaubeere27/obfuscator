package me.superblaubeere27.jobf;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import me.superblaubeere27.hwid.HWID;
import me.superblaubeere27.jobf.ui.GUI;
import me.superblaubeere27.jobf.util.script.JObfScript;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class JObf {
    private static final String VERSION = "Obfuscator v" + (JObf.class.getPackage().getImplementationVersion() == null ? "0.0" : JObf.class.getPackage().getImplementationVersion()) + " by superblaubeere27";
    private final static Logger log = Logger.getLogger("Obfuscator");
    private static GUI gui;

    public static void main(String[] args) throws Exception {
//        System.out.println(Util.modifierToString(0x9));


        OptionParser parser = new OptionParser();
        parser.accepts("help").forHelp();
        parser.accepts("version").forHelp();
        parser.accepts("jarIn").withOptionalArg().required();
        parser.accepts("jarOut").withRequiredArg();
        parser.accepts("package").withOptionalArg().describedAs("Encrypts all classes");
        parser.accepts("packagerMainClass").requiredIf("package").availableIf("package").withOptionalArg();
        parser.accepts("mode").withOptionalArg().describedAs("0 = Normal, 1 = Aggressive (Might not work)").ofType(Integer.class).defaultsTo(0);
        parser.accepts("cp").withOptionalArg().describedAs("ClassPath; Only for name obfuscation").ofType(File.class);
        parser.accepts("scriptFile").withOptionalArg().describedAs("[Not documented] JS script file").ofType(File.class);
        parser.accepts("nameobf").withOptionalArg().describedAs("!!! DEPRECATED !!!").ofType(boolean.class);
        parser.accepts("hwid").withOptionalArg().describedAs("Enabled HWID protection").ofType(String.class);
        parser.accepts("log").withRequiredArg();


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
            String log = (String) options.valueOf("log");
            int mode = (int) options.valueOf("mode");

            JObf.log.setUseParentHandlers(false);
            JObf.log.setLevel(Level.ALL);

            if (log != null) {
                FileHandler filehandler = new FileHandler(log);
                filehandler.setFormatter(new Formatter() {
                    @Override
                    public synchronized String format(LogRecord record) {
                        StringBuffer sb = new StringBuffer();
                        String message = this.formatMessage(record);
                        sb.append(record.getLevel().getName());
                        sb.append(": ");
                        sb.append(message);
                        sb.append("\n");
                        if (record.getThrown() != null) {
                            try {
                                StringWriter sw = new StringWriter();
                                PrintWriter pw = new PrintWriter(sw);
                                record.getThrown().printStackTrace(pw);
                                pw.close();
                                sb.append(sw.toString());
                            } catch (Exception ex) {
                            }
                        }
                        return sb.toString();
                    }

                });
                JObf.log.addHandler(filehandler);
            }

            JObf.log.addHandler(new Handler() {
                @Override
                public void publish(LogRecord record) {
//                    if (record.getLevel().intValue() < Level.INFO.intValue()) return;

                    if (gui != null)
                        gui.logArea.append(String.format(record.getMessage(), record.getParameters()) + "\n");

                    System.out.println(String.format(record.getMessage(), record.getParameters()));
                }

                @Override
                public void flush() {
                }

                @Override
                public void close() throws SecurityException {
                }
            });

            log(JObf.VERSION);
            log("Input:          " + jarIn);
            log("Output:         " + jarOut);
            log("Log:            " + log);

            List<File> fileList = new ArrayList<>();

            if (options.has("cp")) {
                for (Object cp : options.valuesOf("cp")) {
                    File file = (File) cp;

                    if (file.isDirectory()) {
                        Files.walk(file.toPath()).filter(p -> {
                            String path = p.toString().toLowerCase();

                            return path.endsWith(".jar") || path.endsWith(".zip");
                        }).forEach(p -> {
                            fileList.add(p.toFile());
                        });
                    } else if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
                        fileList.add(file);
                    }
                }
            }

//            for (File file : fileList) {
//                System.out.println("ClassPath: " + file.getAbsolutePath());
//            }
            String scriptContent = "";

            if (options.has("scriptFile")) {
                scriptContent = new String(Files.readAllBytes(((File) options.valueOf("scriptFile")).toPath()), "UTF-8");
            }

            JObfScript script = new JObfScript(scriptContent);

            try {
                JObfImpl.processConsole(jarIn, jarOut, fileList, log, mode, options.has("package"), options.has("nameobf"), options.has("hwid"), options.hasArgument("hwid") ? HWID.hexStringToByteArray((String) options.valueOf("hwid")) : HWID.generateHWID(), options.has("package") ? String.valueOf(options.valueOf("packagerMainClass")) : "", script);
            } catch (Exception e) {
                System.err.println("ERROR: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        } catch (OptionException e) {
//            try {
//                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }

//            gui = new GUI();
//            e.printStackTrace();
//            parser.printHelpOn(System.out);
            System.err.println("ERROR: " + e.getMessage() + " (try --help)");

//            e.printStackTrace();
        }
    }

    private static void log(String line) {
        log.info(line);
    }
}
