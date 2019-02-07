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
import me.superblaubeere27.jobf.utils.values.DeprecationLevel;
import me.superblaubeere27.jobf.utils.values.EnabledValue;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NameObfuscation implements INameObfuscationProcessor {
    private static String PROCESSOR_NAME = "NameObfuscation";

    private EnabledValue enabled = new EnabledValue(PROCESSOR_NAME, DeprecationLevel.OK, false);

    private String repackageName = "obfuscator";
    private boolean repackage = false;

    private void putMapping(HashMap<String, String> mappings, String str, String str1) {
        mappings.put(str, str1);
    }

    @Override
    public void transformPost(JObfImpl inst, HashMap<String, ClassNode> nodes) {
        if (!enabled.getObject()) return;
        HashMap<String, String> mappings = new HashMap<>();

        mappings.clear();

        List<ClassWrapper> classWrappers = new ArrayList<>();


        System.out.println("Building Hierarchy");

        for (ClassNode value : nodes.values()) {
            ClassWrapper cw = new ClassWrapper(value, false, new byte[0]);

            classWrappers.add(cw);

            JObfImpl.INSTANCE.buildHierarchy(cw, null);
        }
        System.out.println("Finished building hierarchy");

        long current = System.currentTimeMillis();
        JObf.log.info("Generating mappings...");

        NameUtils.setup("", "", "", true);

        AtomicInteger classCounter = new AtomicInteger();

        classWrappers.forEach(classWrapper -> {
            boolean excluded = this.excluded(classWrapper);

            for (MethodWrapper method : classWrapper.methods) {
                method.methodNode.access &= ~Opcodes.ACC_PRIVATE;
                method.methodNode.access &= ~Opcodes.ACC_PROTECTED;
                method.methodNode.access |= Opcodes.ACC_PUBLIC;
            }
            for (FieldWrapper fieldWrapper : classWrapper.fields) {
                fieldWrapper.fieldNode.access &= ~Opcodes.ACC_PRIVATE;
                fieldWrapper.fieldNode.access &= ~Opcodes.ACC_PROTECTED;
                fieldWrapper.fieldNode.access |= Opcodes.ACC_PUBLIC;
            }
            if (excluded) return;

            classWrapper.methods.stream().filter(methodWrapper -> !Modifier.isNative(methodWrapper.methodNode.access)
                    && !methodWrapper.methodNode.name.equals("main") && !methodWrapper.methodNode.name.equals("premain")
                    && !methodWrapper.methodNode.name.startsWith("<")).forEach(methodWrapper -> {
//                        if (!excluded) {

//                        }
                if (canRenameMethodTree(mappings, new HashSet<>(), methodWrapper, classWrapper.originalName)) {
                    this.renameMethodTree(mappings, new HashSet<>(), methodWrapper, classWrapper.originalName, NameUtils.generateMethodName(classWrapper.originalName, methodWrapper.originalDescription));
                }
            });

            classWrapper.fields.forEach(fieldWrapper -> {
//                if (!excluded) {
//                }
                if (canRenameFieldTree(mappings, new HashSet<>(), fieldWrapper, classWrapper.originalName)) {
                    this.renameFieldTree(new HashSet<>(), fieldWrapper, classWrapper.originalName, NameUtils.generateFieldName(classWrapper.originalName), mappings);
                }
            });
            classWrapper.classNode.access &= ~Opcodes.ACC_PRIVATE;
            classWrapper.classNode.access &= ~Opcodes.ACC_PROTECTED;
            classWrapper.classNode.access |= Opcodes.ACC_PUBLIC;

            putMapping(mappings, classWrapper.originalName, (repackage)
                    ? repackageName + '/' + NameUtils.generateClassName() : NameUtils.generateClassName());
            classCounter.incrementAndGet();
        });

        try {
            FileOutputStream outStream = new FileOutputStream("mappings.txt");
            PrintStream printStream = new PrintStream(outStream);

            for (Map.Entry<String, String> stringStringEntry : mappings.entrySet()) {
                printStream.println(stringStringEntry.getKey() + " -> " + stringStringEntry.getValue());
            }

            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        JObf.log.info(String.format("Finished generating mappings (%dms)", (System.currentTimeMillis() - current)));
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
            JObfImpl.INSTANCE.getClassPath().put(classWrapper.classNode.name, classWrapper);
        }
        JObf.log.info(String.format("Finished applying mappings (%dms)", (System.currentTimeMillis() - current)));
    }

    private boolean excluded(ClassWrapper classWrapper) {
        boolean b = !JObfImpl.INSTANCE.script.remapClass(classWrapper.classNode);
        System.out.println(classWrapper.originalName + "/ " + b);
        return b;
    }

    private boolean excluded(String s) {
        // TODO Check excluded
        return false;
    }


    private boolean canRenameMethodTree(HashMap<String, String> mappings, HashSet<ClassTree> visited, MethodWrapper methodWrapper, String owner) {
        ClassTree tree = JObfImpl.INSTANCE.getTree(owner);
        if (!visited.contains(tree)) {
            visited.add(tree);
            if (excluded(owner + '.' + methodWrapper.originalName + methodWrapper.originalDescription)) {
                return false;
            }
            if (mappings.containsKey(owner + '.' + methodWrapper.originalName + methodWrapper.originalDescription)) {
                return true;
            }
            if (!methodWrapper.owner.originalName.equals(owner) && tree.classWrapper.libraryNode) {
                for (MethodNode mn : tree.classWrapper.classNode.methods) {
                    if (mn.name.equals(methodWrapper.originalName)
                            & mn.desc.equals(methodWrapper.originalDescription)) {
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
        if (!visited.contains(tree)) {
            visited.add(tree);
            if (excluded(owner + '.' + fieldWrapper.originalName + '.' + fieldWrapper.originalDescription)) {
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