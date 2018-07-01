package me.superblaubeere27.jobf;

import com.google.common.io.ByteStreams;
import me.superblaubeere27.jobf.processors.*;
import me.superblaubeere27.jobf.processors.name.INameObfuscationProcessor;
import me.superblaubeere27.jobf.processors.name.NameObfuscation;
import me.superblaubeere27.jobf.processors.packager.Packager;
import me.superblaubeere27.jobf.util.Util;
import me.superblaubeere27.jobf.util.script.JObfScript;
import me.superblaubeere27.jobf.utils.ClassTree;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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
    public static HashMap<String, ClassNode> classes = new HashMap<>();
    public static HashMap<String, byte[]> files = new HashMap<>();
    public boolean mainClassChanged;
    public JObfScript script;
    private List<INameObfuscationProcessor> nameObfuscationProcessors = new ArrayList<>();
    private String mainClass;
    private Map<String, ClassNode> classpath = new HashMap<>();
    private Map<String, ClassTree> hierachy = new HashMap<>();
    private Set<ClassNode> libraryClassnodes = new HashSet<>();
    private List<File> libraryFiles;
    private boolean nameobf;
    public int computeMode;
    private boolean invokeDynamic;

//    public static void process(String inFile, String outFile, String logFile) {
//        JObfImpl inst = new JObfImpl();
//
//        inst.processJar(inFile, outFile, 0);
//
//        log.fine("Processed " + inFile);
//    }


    public JObfImpl() {
//        processors.add(new StaticInitializionProcessor(this));
////        processors.add(new ReferenceProxy(this));
//        processors.add(new FlowObfuscator(this));
//        processors.add(new NumberObfuscationProcessor(this));
//        processors.add(new StringEncryptionProcessor(this));
//        processors.add(new FlowStringProcessor(this));
//        processors.add(new SBProcessor(this));
//        processors.add(new LineNumberRemover(this));
//        processors.add(new ShuffleMembersProcessor(this));
    }

    public static void processConsole(String inFile, String outFile, List<File> fileList, String logFile, int mode, boolean packager, boolean nameobf, boolean hwid, boolean invokeDynamic, byte[] hwidBytes, String packagerMainClass, JObfScript script) {
        JObfImpl inst = new JObfImpl();


        inst.packagerEnabled = packager;
        inst.hwid = hwid;
        inst.invokeDynamic = invokeDynamic;
        inst.hwidBytes = hwidBytes;
        inst.packager = new Packager();
        inst.packagerMainClass = packagerMainClass;
        inst.libraryFiles = fileList;
        inst.script = script;
        inst.nameobf = nameobf;

        inst.addProcessors();


        try {
            System.out.println("Loading classpath...");
            inst.loadClasspath();
            System.out.println("Loaded");
        } catch (IOException e) {
            e.printStackTrace();
        }

        inst.processJar(inFile, outFile, mode);

        log.fine("Processed " + inFile);
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
//        System.out.println(mainClass);
//        new Exception().printStackTrace(System.out);
        this.mainClass = mainClass;
    }

    public static MethodNode getMethod(ClassNode cls, String name, String desc) {
        for (MethodNode method : cls.methods) {
            if (method.name.equals(name) && method.desc.equals(desc))
                return method;
        }
        return null;
    }

    public ClassTree getClassTree(String classNode) {
        ClassTree tree = hierachy.get(classNode);
        if (tree == null) {
            loadHierachyAll(assureLoaded(classNode));
            return getClassTree(classNode);
        }
        return tree;
    }

    private void loadClasspath() throws IOException {
        if (libraryFiles != null) {
            int i = 0;
            for (File file : libraryFiles) {
                if (file.isFile()) {
                    System.out.println("Loading " + file.getAbsolutePath() + " (" + (i++ * 100 / libraryFiles.size()) + "%)");
                    classpath.putAll(loadClasspathFile(file, true));
                }
// else {
//                    File[] files = file.listFiles(child -> child.getName().endsWith(".jar"));
//                    if (files != null) {
//                        for (File child : files) {
//                            classpath.putAll(loadClasspathFile(child, true));
//                        }
//                    }
//                }
            }
        }
//        if (configuration.getLibraries() != null) {
//            for (File file : configuration.getLibraries()) {
//                if (file.isFile()) {
//                    libraries.putAll(loadClasspathFile(file, false));
//                } else {
//                    File[] files = file.listFiles(child -> child.getName().endsWith(".jar"));
//                    if (files != null) {
//                        for (File child : files) {
//                            libraries.putAll(loadClasspathFile(child, false));
//                        }
//                    }
//                }
//            }
//        }
//        classpath.putAll(libraries);
        libraryClassnodes.addAll(classpath.values());
    }

    private Map<String, ClassNode> loadClasspathFile(File file, boolean skipCode) throws IOException {
        Map<String, ClassNode> map = new HashMap<>();

        ZipFile zipIn = new ZipFile(file);
        Enumeration<? extends ZipEntry> entries = zipIn.entries();
        while (entries.hasMoreElements()) {
            ZipEntry ent = entries.nextElement();
            if (ent.getName().endsWith(".class")) {
                ClassReader reader = new ClassReader(zipIn.getInputStream(ent));
                ClassNode node = new ClassNode();
                reader.accept(node, (skipCode ? 0 : 0) | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                map.put(node.name, node);

//                setConstantPool(node, new ConstantPool(reader));
            }
        }
        zipIn.close();

        return map;
    }

    public void loadHierachyAll(ClassNode classNode) {
        Set<String> processed = new HashSet<>();
        LinkedList<ClassNode> toLoad = new LinkedList<>();
        toLoad.add(classNode);
        while (!toLoad.isEmpty()) {
            for (ClassNode toProcess : loadHierachy(toLoad.poll())) {
                if (processed.add(toProcess.name)) {
                    toLoad.add(toProcess);
                }
            }
        }
    }

    public void resetHierachy() {
        this.hierachy.clear();
    }

    private ClassTree getOrCreateClassTree(String name) {
        return this.hierachy.computeIfAbsent(name, ClassTree::new);
    }

    public ClassNode assureLoaded(String ref) {
        ClassNode clazz = classpath.get(ref);
        if (clazz == null) {
            throw new IllegalStateException("Class not in path: " + ref);
        }
        return clazz;
    }

    public ClassNode assureLoadedElseRemove(String referencer, String ref) {
        ClassNode clazz = classpath.get(ref);
        if (clazz == null) {
            classes.remove(referencer);
            classpath.remove(referencer);
            return null;
        }
        return clazz;
    }

    public void loadHierachy() {
        Set<String> processed = new HashSet<>();
        LinkedList<ClassNode> toLoad = new LinkedList<>();
        toLoad.addAll(classes.values());
        while (!toLoad.isEmpty()) {
            for (ClassNode toProcess : loadHierachy(toLoad.poll())) {
                if (processed.add(toProcess.name)) {
                    toLoad.add(toProcess);
                }
            }
        }
    }

    public List<ClassNode> loadHierachy(ClassNode specificNode) {
        if (specificNode.name.equals("java/lang/Object")) {
            return Collections.emptyList();
        }
        if ((specificNode.access & Opcodes.ACC_INTERFACE) != 0) {
            getOrCreateClassTree(specificNode.name).parentClasses.add("java/lang/Object");
            return Collections.emptyList();
        }
        List<ClassNode> toProcess = new ArrayList<>();

        ClassTree thisTree = getOrCreateClassTree(specificNode.name);
        ClassNode superClass;

//        if (DELETE_USELESS_CLASSES) {
//            superClass = assureLoadedElseRemove(specificNode.name, specificNode.superName);
//            if (superClass == null)
//                //It got removed
//                return toProcess;
//        } else
        superClass = assureLoaded(specificNode.superName);

        if (superClass == null) {
            throw new IllegalArgumentException("Could not load " + specificNode.name);
        }
        ClassTree superTree = getOrCreateClassTree(superClass.name);
        superTree.subClasses.add(specificNode.name);
        thisTree.parentClasses.add(superClass.name);
        toProcess.add(superClass);

        for (String interfaceReference : specificNode.interfaces) {
            ClassNode interfaceNode;
//            if (DELETE_USELESS_CLASSES) {
//                interfaceNode = assureLoadedElseRemove(specificNode.name, interfaceReference);
//                if (interfaceNode == null)
//                    //It got removed
//                    return toProcess;
//            } else
            interfaceNode = assureLoaded(interfaceReference);
            if (interfaceNode == null) {
                throw new IllegalArgumentException("Could not load " + interfaceReference);
            }
            ClassTree interfaceTree = getOrCreateClassTree(interfaceReference);
            interfaceTree.subClasses.add(specificNode.name);
            thisTree.parentClasses.add(interfaceReference);
            toProcess.add(interfaceNode);
        }
        return toProcess;
    }

    public boolean isLibrary(ClassNode classNode) {
        return libraryClassnodes.contains(classNode);
    }

    public boolean isSubclass(String possibleParent, String possibleChild) {
        if (possibleParent.equals(possibleChild)) {
            return true;
        }
        loadHierachyAll(assureLoaded(possibleParent));
        loadHierachyAll(assureLoaded(possibleChild));
        ClassTree parentTree = hierachy.get(possibleParent);
        if (parentTree != null && hierachy.get(possibleChild) != null) {
            List<String> layer = new ArrayList<>();
            layer.add(possibleParent);
            layer.addAll(parentTree.subClasses);
            while (!layer.isEmpty()) {
                if (layer.contains(possibleChild)) {
                    return true;
                }
                List<String> clone = new ArrayList<>(layer);
                layer.clear();
                for (String r : clone) {
                    ClassTree tree = hierachy.get(r);
                    if (tree != null)
                        layer.addAll(tree.subClasses);
                }
            }
        }
        return false;
    }

    public void addProcessor(IClassProcessor processor) {
        processors.add(processor);
    }

    public void addProcessors() {
        processors.add(new StaticInitializionProcessor(this));

        if (hwid) {
            processors.add(new HWIDProtection(this, hwidBytes));
        }
        if (invokeDynamic) {
            processors.add(new InvokeDynamic(this));
        }
        processors.add(new StringEncryptionProcessor(this));
        processors.add(new NumberObfuscationProcessor(this));
        processors.add(new FlowObfuscator(this));
        processors.add(new SBProcessor(this));
        processors.add(new LineNumberRemover(this));
        processors.add(new ShuffleMembersProcessor(this));


        nameObfuscationProcessors.add(new NameObfuscation());
//        processors.add(new CrasherProcessor(this));
//        processors.add(new ReferenceProxy(this));
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
            setMainClass(null);

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
                    ClassReader cr = new ClassReader(entryData);
                    ClassNode cn = new ClassNode();


                    //ca = new LineInjectorAdaptor(ASM4, cn);

                    cr.accept(cn, 0);
                    classes.put(entryName, cn);

                } else {
                    if (entryName.equals("META-INF/MANIFEST.MF")) {
                        setMainClass(Util.getMainClass(new String(entryData, "UTF-8")));
                        System.out.println(mainClass);
                    }

                    files.put(entryName, entryData);
                }
            }

            for (Map.Entry<String, ClassNode> stringClassNodeEntry : classes.entrySet()) {
                classpath.put(stringClassNodeEntry.getKey().replace(".class", ""), stringClassNodeEntry.getValue());
            }
            libraryClassnodes.addAll(classes.values());

            if (nameobf) {
                for (INameObfuscationProcessor nameObfuscationProcessor : nameObfuscationProcessors) {
                    nameObfuscationProcessor.transformPost(this, classes);
                }
            }

            int processed = 0;

            for (Map.Entry<String, ClassNode> stringClassNodeEntry : classes.entrySet()) {
                String entryName = stringClassNodeEntry.getKey();
                byte[] entryData;
                ClassNode cn = stringClassNodeEntry.getValue();

                try {
                    try {
                        computeMode = ClassWriter.COMPUTE_MAXS;

                        JObfImpl.log.log(Level.FINE, String.format("(%s/%s), Processing %s", processed, classes.size(), entryName));


                        if (script == null || script.isObfuscatorEnabled(cn.name)) {
                            for (IClassProcessor proc : processors)
                                proc.process(cn, mode);
                        }


                        ClassWriter writer = new ClassWriter(computeMode
//                            | ClassWriter.COMPUTE_FRAMES
                        );
                        cn.accept(writer);

                        entryData = writer.toByteArray();
                    } catch (Exception e) {
                        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS
//                            | ClassWriter.COMPUTE_FRAMES
                        );
                        cn.accept(writer);

                        entryData = writer.toByteArray();
                        e.printStackTrace();
                    }
                    try {
                        if (packagerEnabled) {
                            entryName = packager.encryptName(entryName.replace(".class", ""));
                            entryData = packager.encryptClass(entryData);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    ZipEntry newEntry = new ZipEntry(entryName);
                    outJar.putNextEntry(newEntry);
                    outJar.write(entryData);
//                    JObfImpl.log.log(Level.FINE, String.format("Processed %s (+%.2f KB)", entryName, (entryData.length - entryBuffer.size()) / 1024.0));
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                JObfImpl.log.log(Level.FINE, "Processed " + entryBuffer.size() + " -> " + entryData.length);

                processed++;
            }
            for (Map.Entry<String, byte[]> stringEntry : files.entrySet()) {
                String entryName = stringEntry.getKey();
                byte[] entryData = stringEntry.getValue();

                if (entryName.equals("META-INF/MANIFEST.MF")) {
                    if (packagerEnabled) {
                        entryData = Util.replaceMainClass(new String(entryData, "UTF-8"), packager.getDecryptorClassName()).getBytes("UTF-8");
                    } else if (mainClassChanged) {
                        entryData = Util.replaceMainClass(new String(entryData, "UTF-8"), mainClass).getBytes("UTF-8");
                        JObfImpl.log.log(Level.FINE, "Replaced Main-Class with " + mainClass);
                    }

                    JObfImpl.log.log(Level.FINE, "Processed MANIFEST.MF");
                }
                JObfImpl.log.log(Level.FINE, "Copying " + entryName);

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
                System.out.println("Packaging...");
                byte[] decryptorData = packager.generateEncryptionClass(packagerMainClass, mode);
                outJar.putNextEntry(new ZipEntry(packager.getDecryptorClassName() + ".class"));
                outJar.write(decryptorData);
                outJar.closeEntry();
                System.out.println("Packaging finished.");
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

        Analyzer analyzer = new Analyzer(new BasicInterpreter());

        for (MethodNode method : cn.methods) {
            System.err.println("Verifing " + cn.name + "/" + method.name);
            try {
                analyzer.analyze(cn.name, method);
            } catch (AnalyzerException e) {
                e.printStackTrace();
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS
//                | ClassWriter.COMPUTE_FRAMES
        );
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
