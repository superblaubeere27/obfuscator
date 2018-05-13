package me.superblaubeere27.jobf;

import com.google.common.io.ByteStreams;
import me.superblaubeere27.jobf.processors.*;
import me.superblaubeere27.jobf.processors.packager.Packager;
import me.superblaubeere27.jobf.util.Util;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class JObfImpl {
    final static Logger log = Logger.getLogger("Obfuscator");
    public static List<IClassProcessor> processors = new ArrayList<>();
    private boolean packagerEnabled = false;
    private boolean hwid;
    private byte[] hwidBytes;
    private Packager packager;
    private String packagerMainClass;
    private boolean workDone = false;

//    public static void process(String inFile, String outFile, String logFile) {
//        JObfImpl inst = new JObfImpl();
//
//        inst.processJar(inFile, outFile, 0);
//
//        log.fine("Processed " + inFile);
//    }

    public JObfImpl() {
//        processors.add(new FlowObfuscator(this));
//        processors.add(new StaticInitializionProcessor(this));
//        processors.add(new NumberObfuscationProcessor(this));
//        processors.add(new StringEncryptionProcessor(this));
//        processors.add(new FlowStringProcessor(this));
//        processors.add(new SBProcessor(this));
//        processors.add(new LineNumberRemover(this));
//        processors.add(new ShuffleMembersProcessor(this));
    }

    public static void processConsole(String inFile, String outFile, String logFile, int mode, boolean packager, boolean hwid, byte[] hwidBytes, String packagerMainClass) {
        JObfImpl inst = new JObfImpl();

        inst.addProcessors();

        inst.packagerEnabled = packager;
        inst.hwid = hwid;
        inst.hwidBytes = hwidBytes;
        inst.packager = new Packager();
        inst.packagerMainClass = packagerMainClass;

        inst.processJar(inFile, outFile, mode);

        log.fine("Processed " + inFile);
    }

    public static MethodNode getMethod(ClassNode cls, String name, String desc) {
        for (MethodNode method : cls.methods) {
            if (method.name.equals(name) && method.desc.equals(desc))
                return method;
        }
        return null;
    }

    public void addProcessor(IClassProcessor processor) {
        processors.add(processor);
    }

    public void addProcessors() {
        processors.add(new StaticInitializionProcessor(this));
        processors.add(new ReferenceProxy(this));
        processors.add(new StringEncryptionProcessor(this));
        processors.add(new NumberObfuscationProcessor(this));
        processors.add(new FlowObfuscator(this));
        processors.add(new SBProcessor(this));
        processors.add(new LineNumberRemover(this));
        processors.add(new ShuffleMembersProcessor(this));
        //processors.add(new InvokeDynamic(this));
    }

    public void processJar(String inFile, String outFile, int mode) {
        if (packagerEnabled) {
            packager = new Packager();

            if (!hwid) {
                packager.init();
            } else {
                packager.initHWID(hwidBytes);
            }
        }
        ZipInputStream inJar = null;
        ZipOutputStream outJar = null;

        try {
            try {
                inJar = new ZipInputStream(new BufferedInputStream(new FileInputStream(inFile)));
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException("Could not open input file: " + e.getMessage());
            }

            try {
                OutputStream out = (outFile == null ? new ByteArrayOutputStream() : new FileOutputStream(outFile));
                outJar = new ZipOutputStream(new BufferedOutputStream(out));
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException("Could not open output file: " + e.getMessage());
            }

            while (true) {
                ZipEntry entry = inJar.getNextEntry();

                if (entry == null) {
                    break;
                }

                if (entry.isDirectory()) {
                    outJar.putNextEntry(entry);
                    continue;
                }

                byte[] data = new byte[4096];
                ByteArrayOutputStream entryBuffer = new ByteArrayOutputStream();

                int len;
                do {
                    len = inJar.read(data);
                    if (len > 0) {
                        entryBuffer.write(data, 0, len);
                    }
                } while (len != -1);

                byte[] entryData = entryBuffer.toByteArray();

                String entryName = entry.getName();

                if (entryName.endsWith(".class")) {
                    try {
                        JObfImpl.log.log(Level.FINE, String.format("Processing %s", entryName));
                        entryData = this.processClass(entryData, outFile == null, mode);
                        JObfImpl.log.log(Level.FINE, String.format("Processed %s (+%.2f KB)", entryName, (entryData.length - entryBuffer.size()) / 1024.0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (packagerEnabled) {
                        entryName = packager.encryptName(entryName.replace(".class", ""));
                        entryData = packager.encryptClass(entryData);
                    }

//                    JObfImpl.log.log(Level.FINE, "Processed " + entryBuffer.size() + " -> " + entryData.length);
                } else {
                    if (entryName.equals("META-INF/MANIFEST.MF")) {
                        if (packagerEnabled) {
                            entryData = Util.replaceMainClass(new String(entryData), packager.getDecryptorClassName()).getBytes("UTF-8");
                        }
                        JObfImpl.log.log(Level.FINE, "Processed MANIFEST.MF");
                    }
                    JObfImpl.log.log(Level.FINE, "Copying " + entryName);
                }

                ZipEntry newEntry = new ZipEntry(entryName);
                outJar.putNextEntry(newEntry);
                outJar.write(entryData);
            }

            // Add Out Util class:
            String[] extras = {
//                    Type.getInternalName(Util.class) + ".class",
//                    Type.getInternalName(Util.class) + "$1.class",
//                    Type.getInternalName(Util.Indexed.class) + ".class"
            };
            for (String name : extras) {
                ZipEntry newEntry = new ZipEntry(name);
                outJar.putNextEntry(newEntry);
                outJar.write(ByteStreams.toByteArray(JObfImpl.class.getClassLoader().getResourceAsStream(name)));
            }
            if (packagerEnabled) {
                byte[] decryptorData = packager.generateEncryptionClass(packagerMainClass, mode);
                outJar.putNextEntry(new ZipEntry(packager.getDecryptorClassName() + ".class"));
                outJar.write(decryptorData);
                outJar.closeEntry();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outJar != null) {
                try {
                    outJar.flush();
                    outJar.close();
                    System.out.println(">>> Processing completed +" + (new File(inFile).length() / ((float) new File(outFile).length()) * 100.0f) + "%");
                } catch (Exception e) {
                    // ignore
                }
            }

            if (inJar != null) {
                try {
                    inJar.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    public void setWorkDone() {
        workDone = true;
    }

    public byte[] processClass(byte[] cls, boolean readOnly, int mode) {
        workDone = false;

        ClassReader cr = new ClassReader(cls);
        ClassNode cn = new ClassNode();

        ClassVisitor ca = cn;

        //ca = new LineInjectorAdaptor(ASM4, cn);

        cr.accept(ca, 0);

        for (IClassProcessor proc : processors)
            proc.process(cn, mode);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(writer);

        return workDone ? writer.toByteArray() : cls;
    }

    public void loadConfig(Configuration config) {
        FlowObfuscator flowObfuscator = new FlowObfuscator(this);
        LineNumberRemover numberRemover = new LineNumberRemover(this);
        NumberObfuscationProcessor numberProcessor = new NumberObfuscationProcessor(this);
        SBProcessor sbProcessor = new SBProcessor(this);
        ShuffleMembersProcessor shuffleMembersProcessor = new ShuffleMembersProcessor(this);
        StaticInitializionProcessor staticInitializionProcessor = new StaticInitializionProcessor(this);
        StringEncryptionProcessor stringEncryptionProcessor = new StringEncryptionProcessor(this);
        ReferenceProxy referenceProxy = new ReferenceProxy(this);

        if (config.isStaticInitializionProtectorEnabled) addProcessor(staticInitializionProcessor);
        if (config.isReferenceProxyEnabled) addProcessor(referenceProxy);
        if (config.isStringEncryptionEnabled) addProcessor(stringEncryptionProcessor);
        if (config.isNumberObfuscatorEnabled) addProcessor(numberProcessor);
        if (config.isFlowObfuscatorEnabled) addProcessor(flowObfuscator);
        if (config.isInformationRemoverEnabled) addProcessor(numberRemover);
        if (config.isHiderEnabled) addProcessor(sbProcessor);
        if (config.isShuffleMembersEnabled) addProcessor(shuffleMembersProcessor);
    }
}
