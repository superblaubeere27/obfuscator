/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27.jobf.processors.name;

import me.superblaubeere27.jobf.JObf;
import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.utils.ClassTree;
import me.superblaubeere27.jobf.utils.NameUtils;
import me.superblaubeere27.jobf.utils.Utils;
import me.superblaubeere27.jobf.utils.values.BooleanValue;
import me.superblaubeere27.jobf.utils.values.DeprecationLevel;
import me.superblaubeere27.jobf.utils.values.EnabledValue;
import me.superblaubeere27.jobf.utils.values.StringValue;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class NameObfuscation implements INameObfuscationProcessor {
    private static final String PROCESSOR_NAME = "NameObfuscation";
    private static final Random random = new Random();
    private final EnabledValue enabled = new EnabledValue(PROCESSOR_NAME, DeprecationLevel.OK, false);
    private final StringValue excludedClasses = new StringValue(PROCESSOR_NAME, "Excluded classes", null, DeprecationLevel.GOOD, "me.name.Class\nme.name.*\nio.netty.**", 5);
    private final StringValue excludedMethods = new StringValue(PROCESSOR_NAME, "Excluded methods", null, DeprecationLevel.GOOD, "me.name.Class.method\nme.name.Class**\nme.name.Class.*", 5);
    private final StringValue excludedFields = new StringValue(PROCESSOR_NAME, "Excluded fields", null, DeprecationLevel.GOOD, "me.name.Class.field\nme.name.Class.*\nme.name.**", 5);
    private final BooleanValue shouldPackage = new BooleanValue(PROCESSOR_NAME, "Package", DeprecationLevel.OK, false);
    private final StringValue newPackage = new StringValue(PROCESSOR_NAME, "New Packages", null, DeprecationLevel.GOOD, "", 5);
    private final BooleanValue acceptMissingLibraries = new BooleanValue(PROCESSOR_NAME, "Accept Missing Libraries", DeprecationLevel.GOOD, false);
    private List<String> packageNames;
    private final List<Pattern> excludedClassesPatterns = new ArrayList<>();
    private final List<Pattern> excludedMethodsPatterns = new ArrayList<>();
    private final List<Pattern> excludedFieldsPatterns = new ArrayList<>();

    public void setupPackages() {
        if (shouldPackage.getObject()) {
            String[] newPackages = newPackage.getObject().split("\n");
            packageNames = Arrays.asList(newPackages);
        }
    }

    public String getPackageName() {
        if (shouldPackage.getObject()) {
            if (packageNames == null) setupPackages();

            String retVal;
            if (packageNames.size() == 1 && packageNames.get(0).equalsIgnoreCase("common")) {
                retVal = CommonPackageTrees.getRandomPackage();
            } else {
                retVal = packageNames.get(random.nextInt(packageNames.size()));
            }

            if (retVal.startsWith("/"))
                retVal = retVal.substring(1);
            if (!retVal.endsWith("/"))
                retVal = retVal + "/";

            return retVal;
        }
        return "";
    }

    private void putMapping(HashMap<String, String> mappings, String str, String str1) {
        mappings.put(str, str1);
    }

    @Override
    public void transformPost(JObfImpl inst, HashMap<String, ClassNode> nodes) {
        if (!enabled.getObject()) return;

        try {
            HashMap<String, String> mappings = new HashMap<>();

            List<ClassWrapper> classWrappers = new ArrayList<>();

            for (String s : excludedClasses.getObject().split("\n")) {
                excludedClassesPatterns.add(compileExcludePattern(s));
            }
            for (String s : excludedMethods.getObject().split("\n")) {
                excludedMethodsPatterns.add(compileExcludePattern(s));
            }
            for (String s : excludedFields.getObject().split("\n")) {
                excludedFieldsPatterns.add(compileExcludePattern(s));
            }

            JObf.log.info("Building Hierarchy...");

            for (ClassNode value : nodes.values()) {
                ClassWrapper cw = new ClassWrapper(value, false, new byte[0]);

                classWrappers.add(cw);

                JObfImpl.INSTANCE.buildHierarchy(cw, null, acceptMissingLibraries.getObject());
            }

            JObf.log.info("... Finished building hierarchy");

            long current = System.currentTimeMillis();
            JObf.log.info("Generating mappings...");

            NameUtils.setup();

            AtomicInteger classCounter = new AtomicInteger();

            classWrappers.forEach(classWrapper -> {
                boolean excluded = this.isClassExcluded(classWrapper);
                AtomicBoolean builtHierarchy = new AtomicBoolean(false);

                for (MethodWrapper method : classWrapper.methods) {
                    if ((Modifier.isPrivate(method.methodNode.access) || Modifier.isProtected(method.methodNode.access)) && excluded)
                        continue;

                    method.methodNode.access &= ~Opcodes.ACC_PRIVATE;
                    method.methodNode.access &= ~Opcodes.ACC_PROTECTED;
                    method.methodNode.access |= Opcodes.ACC_PUBLIC;
                }
                for (FieldWrapper field : classWrapper.fields) {
                    if ((Modifier.isPrivate(field.fieldNode.access) || Modifier.isProtected(field.fieldNode.access)) && excluded)
                        continue;

                    field.fieldNode.access &= ~Opcodes.ACC_PRIVATE;
                    field.fieldNode.access &= ~Opcodes.ACC_PROTECTED;
                    field.fieldNode.access |= Opcodes.ACC_PUBLIC;
                }

                AtomicBoolean nativeMethodsFound = new AtomicBoolean(false);

                classWrapper.methods.stream().filter(methodWrapper ->
                        !methodWrapper.methodNode.name.equals("main") && !methodWrapper.methodNode.name.equals("premain")
                                && !methodWrapper.methodNode.name.startsWith("<")).forEach(methodWrapper -> {

                    if (Modifier.isNative(methodWrapper.methodNode.access)) {
                        nativeMethodsFound.set(true);
                    }

                    try {
                        if (!isMethodExcluded(classWrapper.originalName, methodWrapper) && canRenameMethodTree(mappings, new HashSet<>(), methodWrapper, classWrapper.originalName)) {
                            this.renameMethodTree(mappings, new HashSet<>(), methodWrapper, classWrapper.originalName, NameUtils.generateMethodName(classWrapper.originalName, methodWrapper.originalDescription));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                classWrapper.fields.forEach(fieldWrapper -> {
                    if (!isFieldExcluded(classWrapper.originalName, fieldWrapper) && canRenameFieldTree(mappings, new HashSet<>(), fieldWrapper, classWrapper.originalName)) {
                        this.renameFieldTree(new HashSet<>(), fieldWrapper, classWrapper.originalName, NameUtils.generateFieldName(classWrapper.originalName), mappings);
                    }
                });

                if (!excluded && nativeMethodsFound.get()) {
                    JObf.log.info("Automatically excluded " + classWrapper.originalName + " because it has native methods in it.");
                }

                if (excluded || nativeMethodsFound.get()) return;

                classWrapper.classNode.access &= ~Opcodes.ACC_PRIVATE;
                classWrapper.classNode.access &= ~Opcodes.ACC_PROTECTED;
                classWrapper.classNode.access |= Opcodes.ACC_PUBLIC;

                putMapping(mappings, classWrapper.originalName, getPackageName() + NameUtils.generateClassName());
                classCounter.incrementAndGet();
            });

//        try {
//            FileOutputStream outStream = new FileOutputStream("mappings.txt");
//            PrintStream printStream = new PrintStream(outStream);
//
//            for (Map.Entry<String, String> stringStringEntry : mappings.entrySet()) {
//                printStream.println(stringStringEntry.getKey() + " -> " + stringStringEntry.getValue());
//            }
//
//            outStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


            JObf.log.info(String.format("... Finished generating mappings (%s)", Utils.formatTime(System.currentTimeMillis() - current)));
            JObf.log.info("Applying mappings...");

            current = System.currentTimeMillis();

            Remapper simpleRemapper = new MemberRemapper(mappings);

            for (ClassWrapper classWrapper : classWrappers) {
                ClassNode classNode = classWrapper.classNode;

                ClassNode copy = new ClassNode();
                classNode.accept(new ClassRemapper(copy, simpleRemapper));
                for (int i = 0; i < copy.methods.size(); i++) {
                    classWrapper.methods.get(i).methodNode = copy.methods.get(i);

                    /*for (AbstractInsnNode insn : methodNode.instructions.toArray()) { // TODO: Fix lambdas + interface
                        if (insn instanceof InvokeDynamicInsnNode) {
                            InvokeDynamicInsnNode indy = (InvokeDynamicInsnNode) insn;
                            if (indy.bsm.getOwner().equals("java/lang/invoke/LambdaMetafactory")) {
                                Handle handle = (Handle) indy.bsmArgs[1];
                                String newName = mappings.get(handle.getOwner() + '.' + handle.getName() + handle.getDesc());
                                if (newName != null) {
                                    indy.name = newName;
                                    indy.bsm = new Handle(handle.getTag(), handle.getOwner(), newName, handle.getDesc(), false);
                                }
                            }
                        }
                    }*/
                }

                if (copy.fields != null) {
                    for (int i = 0; i < copy.fields.size(); i++) {
                        classWrapper.fields.get(i).fieldNode = copy.fields.get(i);
                    }
                }

                classWrapper.classNode = copy;
                JObfImpl.classes.remove(classWrapper.originalName + ".class");
                JObfImpl.classes.put(classWrapper.classNode.name + ".class", classWrapper.classNode);
                //            JObfImpl.INSTANCE.getClassPath().put();
                //            this.getClasses().put(classWrapper.classNode.name, classWrapper);

                ClassWriter writer = new ClassWriter(0);

                classWrapper.classNode.accept(writer);

                classWrapper.originalClass = writer.toByteArray();

                JObfImpl.INSTANCE.getClassPath().put(classWrapper.classNode.name, classWrapper);
            }

            JObf.log.info(String.format("... Finished applying mappings (%s)", Utils.formatTime(System.currentTimeMillis() - current)));
        } finally {
            excludedClassesPatterns.clear();
            excludedMethodsPatterns.clear();
            excludedFieldsPatterns.clear();
        }

    }

    private Pattern compileExcludePattern(String s) {
        StringBuilder sb = new StringBuilder();
        // s.replace('.', '/').replace("**", ".*").replace("*", "[^/]*")

        char[] chars = s.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if (c == '*') {
                if (chars.length - 1 != i && chars[i + 1] == '*') {
                    sb.append(".*");
                    i++;
                } else {
                    sb.append("[^/]*");
                }
            } else if (c == '.') {
                sb.append('/');
            } else {
                sb.append(c);
            }
        }

        return Pattern.compile(sb.toString());
    }

    private boolean isClassExcluded(ClassWrapper classWrapper) {
        String str = classWrapper.classNode.name;

        for (Pattern excludedMethodsPattern : excludedClassesPatterns) {
            if (excludedMethodsPattern.matcher(str).matches()) {
                JObf.log.log(Level.FINEST, "Class '" + classWrapper.classNode.name + "' was excluded from name obfuscation by regex '" + excludedMethodsPattern.pattern() + "'");
                return true;
            }
        }

        return false;
    }

    private boolean isMethodExcluded(String owner, MethodWrapper methodWrapper) {
        String str = owner + '.' + methodWrapper.originalName;

        for (Pattern excludedMethodsPattern : excludedMethodsPatterns) {
            if (excludedMethodsPattern.matcher(str).matches()) {
                return true;
            }
        }

        return false;
    }

    private boolean isFieldExcluded(String owner, FieldWrapper methodWrapper) {
        String str = owner + '.' + methodWrapper.originalName;

        for (Pattern excludedMethodsPattern : excludedFieldsPatterns) {
            if (excludedMethodsPattern.matcher(str).matches()) {
                return true;
            }
        }

        return false;
    }

    private boolean canRenameMethodTree(HashMap<String, String> mappings, HashSet<ClassTree> visited, MethodWrapper methodWrapper, String owner) {
        ClassTree tree = JObfImpl.INSTANCE.getTree(owner);

        if (tree == null)
            return false;

        if (!visited.contains(tree)) {
            visited.add(tree);

            if (tree.missingSuperClass) {
                return false;
            }
            if (Modifier.isNative(methodWrapper.methodNode.access)) {
                return false;
            }

            if (mappings.containsKey(owner + '.' + methodWrapper.originalName + methodWrapper.originalDescription)) {
                return true;
            }
            if (!methodWrapper.owner.originalName.equals(owner) && tree.classWrapper.libraryNode) {
                for (MethodNode mn : tree.classWrapper.classNode.methods) {
                    if (mn.name.equals(methodWrapper.originalName)
                            && mn.desc.equals(methodWrapper.originalDescription)) {
                        return false;
                    }
                }
            }
            for (String parent : tree.parentClasses) {
                if (parent != null && !canRenameMethodTree(mappings, visited, methodWrapper, parent)) {
                    return false;
                }
            }
            for (String sub : tree.subClasses) {
                if (sub != null && !canRenameMethodTree(mappings, visited, methodWrapper, sub)) {
                    return false;
                }
            }
        }

        return true;
    }

    private void renameMethodTree(HashMap<String, String> mappings, HashSet<ClassTree> visited, MethodWrapper MethodWrapper, String className,
                                  String newName) {
        ClassTree tree = JObfImpl.INSTANCE.getTree(className);

        if (!tree.classWrapper.libraryNode && !visited.contains(tree)) {
            putMapping(mappings, className + '.' + MethodWrapper.originalName + MethodWrapper.originalDescription, newName);
            visited.add(tree);
            for (String parentClass : tree.parentClasses) {
                this.renameMethodTree(mappings, visited, MethodWrapper, parentClass, newName);
            }
            for (String subClass : tree.subClasses) {
                this.renameMethodTree(mappings, visited, MethodWrapper, subClass, newName);
            }
        }
    }

    private boolean canRenameFieldTree(HashMap<String, String> mappings, HashSet<ClassTree> visited, FieldWrapper fieldWrapper, String owner) {
        ClassTree tree = JObfImpl.INSTANCE.getTree(owner);

        if (tree == null)
            return false;

        if (!visited.contains(tree)) {
            visited.add(tree);

            if (tree.missingSuperClass) {
                return false;
            }

            if (mappings.containsKey(owner + '.' + fieldWrapper.originalName + '.' + fieldWrapper.originalDescription))
                return true;
            if (!fieldWrapper.owner.originalName.equals(owner) && tree.classWrapper.libraryNode) {
                for (FieldNode fn : tree.classWrapper.classNode.fields) {
                    if (fieldWrapper.originalName.equals(fn.name) && fieldWrapper.originalDescription.equals(fn.desc)) {
                        return false;
                    }
                }
            }
            for (String parent : tree.parentClasses) {
                if (parent != null && !canRenameFieldTree(mappings, visited, fieldWrapper, parent)) {
                    return false;
                }
            }
            for (String sub : tree.subClasses) {
                if (sub != null && !canRenameFieldTree(mappings, visited, fieldWrapper, sub)) {
                    return false;
                }
            }
        }

        return true;
    }

    private void renameFieldTree(HashSet<ClassTree> visited, FieldWrapper fieldWrapper, String owner, String newName, HashMap<String, String> mappings) {
        ClassTree tree = JObfImpl.INSTANCE.getTree(owner);

        if (!tree.classWrapper.libraryNode && !visited.contains(tree)) {
            putMapping(mappings, owner + '.' + fieldWrapper.originalName + '.' + fieldWrapper.originalDescription, newName);
            visited.add(tree);
            for (String parentClass : tree.parentClasses) {
                this.renameFieldTree(visited, fieldWrapper, parentClass, newName, mappings);
            }
            for (String subClass : tree.subClasses) {
                this.renameFieldTree(visited, fieldWrapper, subClass, newName, mappings);
            }
        }
    }

//    @Override
//    public void processClass(final ClassNode classNode) {
//        for(final MethodNode method : classNode.methods)
//            if(localVariables && method.localVariables != null && !method.localVariables.isEmpty())
//                method.localVariables.forEach(localVariableNode -> localVariableNode.name = NameUtils.generateLocalVariableName(classNode.name, method.name));
//    }
}
