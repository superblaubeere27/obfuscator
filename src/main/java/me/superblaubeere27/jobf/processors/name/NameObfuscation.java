package me.superblaubeere27.jobf.processors.name;

import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.utils.ClassTree;
import me.superblaubeere27.jobf.utils.NameUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class NameObfuscation implements INameObfuscationProcessor {

    @Override
    public void transformPost(JObfImpl inst, HashMap<String, ClassNode> nodes) {
        final List<ClassNode> classNodes = new ArrayList<>(JObfImpl.classes.values());

        final Map<String, ClassNode> updatedClasses = new HashMap<>();
        final CustomRemapper remapper = new CustomRemapper();


        for (final ClassNode classNode : classNodes) {
            if (!inst.script.remapClass(classNode)) continue;

            String mappedName;

            do {
                mappedName = NameUtils.generateClassName();
            } while (!remapper.map(classNode.name, mappedName));

            Set<String> allClasses = new HashSet<>();
            ClassTree tree = inst.getClassTree(classNode.name);
            Set<String> tried = new HashSet<>();
            LinkedList<String> toTry = new LinkedList<>();
            toTry.add(tree.thisClass);

            while (!toTry.isEmpty()) {
                String t = toTry.poll();
                if (tried.add(t) && !t.equals("java/lang/Object")) {
                    ClassTree ct = inst.getClassTree(t);
                    allClasses.add(t);
                    allClasses.addAll(ct.parentClasses);
                    allClasses.addAll(ct.subClasses);
                    toTry.addAll(ct.parentClasses);
                    toTry.addAll(ct.subClasses);
                }
            }
            for (FieldNode fieldNode : classNode.fields) {
                List<String> references = new ArrayList<>();
                for (String possibleClass : allClasses) {
                    ClassNode otherNode = inst.assureLoaded(possibleClass);
                    boolean found = false;
                    for (FieldNode otherField : otherNode.fields) {
                        if (otherField.name.equals(fieldNode.name) && otherField.desc.equals(fieldNode.desc)) {
                            found = true;
                        }
                    }
                    if (!found) {
                        references.add(possibleClass);
                    }
                }
                if (!remapper.fieldMappingExists(classNode.name, fieldNode.name, fieldNode.desc)) {
                    while (true) {
//                        String newName = "Field" + integer.getAndIncrement();
                        String newName = NameUtils.generateFieldName(classNode.name);
                        if (remapper.mapFieldName(classNode.name, fieldNode.name, fieldNode.desc, newName, false)) {
                            for (String s : references) {
                                remapper.mapFieldName(s, fieldNode.name, fieldNode.desc, newName, true);
                            }
                            break;
                        }
                    }
                }
            }
            while (!toTry.isEmpty()) {
                String t = toTry.poll();
                if (tried.add(t) && !t.equals("java/lang/Object")) {
                    ClassNode cn = inst.assureLoaded(t);
                    ClassTree ct = inst.getClassTree(t);
                    allClasses.add(t);
                    allClasses.addAll(ct.parentClasses);
                    toTry.addAll(ct.parentClasses);
                    allClasses.addAll(ct.subClasses);
                    toTry.addAll(ct.subClasses);
                }
            }
            allClasses.remove(tree.thisClass);

            for (MethodNode methodNode : new ArrayList<>(classNode.methods)) {
                if (methodNode.name.startsWith("<"))
                    continue;
                if (methodNode.name.equals("main"))
                    continue;
                if ((methodNode.access & Opcodes.ACC_ABSTRACT) != 0)
                    continue;
                if ((methodNode.access & Opcodes.ACC_NATIVE) != 0)
                    continue;
                if ((classNode.access & Opcodes.ACC_INTERFACE) != 0)
                    continue;

                final Map<Map.Entry<ClassNode, MethodNode>, Boolean> allMethodNodes = new HashMap<>();
                final Type methodType = Type.getReturnType(methodNode.desc);
                final AtomicBoolean isLibrary = new AtomicBoolean(false);
                if (methodType.getSort() != Type.OBJECT && methodType.getSort() != Type.ARRAY) {
                    if (methodType.getSort() == Type.METHOD) {
                        throw new IllegalArgumentException("Did not expect method");
                    }
                    allClasses.stream().map(inst::assureLoaded).forEach(node -> {
                        boolean foundSimilar = false;
                        boolean equals = false;
                        MethodNode equalsMethod = null;
                        for (MethodNode method : node.methods) {
                            Type thisType = Type.getMethodType(methodNode.desc);
                            Type otherType = Type.getMethodType(method.desc);
                            if (methodNode.name.equals(method.name) && Arrays.equals(thisType.getArgumentTypes(), otherType.getArgumentTypes())) {
                                foundSimilar = true;
                                if (thisType.getReturnType().getSort() == otherType.getReturnType().getSort()) {
                                    equals = true;
                                    equalsMethod = method;
                                }
                            }
                        }
                        if (foundSimilar) {
                            if (equals) {
                                allMethodNodes.put(new AbstractMap.SimpleEntry<>(node, equalsMethod), true);
                            }
                        } else {
                            allMethodNodes.put(new AbstractMap.SimpleEntry<>(node, methodNode), false);
                        }
                    });
                } else if (methodType.getSort() == Type.ARRAY) {
                    Type elementType = methodType.getElementType();
                    if (elementType.getSort() == Type.OBJECT) {
                        String parent = elementType.getInternalName();
                        allClasses.stream().map(name -> inst.assureLoaded(name)).forEach(node -> {
                            boolean foundSimilar = false;
                            boolean equals = false;
                            MethodNode equalsMethod = null;
                            for (MethodNode method : node.methods) {
                                Type thisType = Type.getMethodType(methodNode.desc);
                                Type otherType = Type.getMethodType(method.desc);
                                if (methodNode.name.equals(method.name) && Arrays.equals(thisType.getArgumentTypes(), otherType.getArgumentTypes())) {
                                    if (otherType.getReturnType().getSort() == Type.OBJECT) {
                                        foundSimilar = true;
                                        String child = otherType.getReturnType().getInternalName();
                                        inst.assureLoaded(parent);
                                        inst.assureLoaded(child);
                                        if (inst.isSubclass(parent, child) || inst.isSubclass(child, parent)) {
                                            equals = true;
                                            equalsMethod = method;
                                        }
                                    }
                                }
                            }
                            if (foundSimilar) {
                                if (equals) {
                                    allMethodNodes.put(new AbstractMap.SimpleEntry<>(node, equalsMethod), true);
                                }
                            } else {
                                allMethodNodes.put(new AbstractMap.SimpleEntry<>(node, methodNode), false);
                            }
                        });
                    } else {
                        allClasses.stream().map(name -> inst.assureLoaded(name)).forEach(node -> {
                            boolean foundSimilar = false;
                            boolean equals = false;
                            MethodNode equalsMethod = null;
                            for (MethodNode method : node.methods) {
                                Type thisType = Type.getMethodType(methodNode.desc);
                                Type otherType = Type.getMethodType(method.desc);
                                if (methodNode.name.equals(method.name) && Arrays.equals(thisType.getArgumentTypes(), otherType.getArgumentTypes())) {
                                    foundSimilar = true;
                                    if (thisType.getReturnType().getSort() == otherType.getReturnType().getSort()) {
                                        equals = true;
                                        equalsMethod = method;
                                    }
                                }
                            }
                            if (foundSimilar) {
                                if (equals) {
                                    allMethodNodes.put(new AbstractMap.SimpleEntry<>(node, equalsMethod), true);
                                }
                            } else {
                                allMethodNodes.put(new AbstractMap.SimpleEntry<>(node, methodNode), false);
                            }
                        });
                    }
                } else if (methodType.getSort() == Type.OBJECT) {
                    String parent = methodType.getInternalName();
                    allClasses.stream().map(name -> inst.assureLoaded(name)).forEach(node -> {
                        boolean foundSimilar = false;
                        boolean equals = false;
                        MethodNode equalsMethod = null;
                        for (MethodNode method : node.methods) {
                            Type thisType = Type.getMethodType(methodNode.desc);
                            Type otherType = Type.getMethodType(method.desc);
                            if (methodNode.name.equals(method.name) && Arrays.equals(thisType.getArgumentTypes(), otherType.getArgumentTypes())) {
                                if (otherType.getReturnType().getSort() == Type.OBJECT) {
                                    foundSimilar = true;
                                    String child = otherType.getReturnType().getInternalName();
                                    inst.assureLoaded(parent);
                                    inst.assureLoaded(child);
                                    if (inst.isSubclass(parent, child) || inst.isSubclass(child, parent)) {
                                        equals = true;
                                        equalsMethod = method;
                                    }
                                }
                            }
                        }
                        if (foundSimilar) {
                            if (equals) {
                                allMethodNodes.put(new AbstractMap.SimpleEntry<>(node, equalsMethod), true);
                            } else {
                                allMethodNodes.put(new AbstractMap.SimpleEntry<>(node, methodNode), false);
                            }
                        } else {
                            allMethodNodes.put(new AbstractMap.SimpleEntry<>(node, methodNode), false);
                        }
                    });
                }

                allMethodNodes.forEach((key, value) -> {
                    if (inst.isLibrary(key.getKey()) && value) {
                        isLibrary.set(true);
                    }
                });

                if (!isLibrary.get()) {
                    if (!remapper.methodMappingExists(classNode.name, methodNode.name, methodNode.desc)) {
                        while (true) {
                            String name = NameUtils.generateMethodName(classNode, methodNode.desc);

                            if (remapper.mapMethodName(classNode.name, methodNode.name, methodNode.desc, name, false)) {
                                allMethodNodes.keySet().forEach(ent -> remapper.mapMethodName(ent.getKey().name, ent.getValue().name, ent.getValue().desc, name, true));
                                break;
                            }
                        }
                    }
                }
            }
        }

        for (final ClassNode classNode : classNodes) {
            JObfImpl.classes.remove(classNode.name + ".class");

            ClassNode newNode = new ClassNode();
            ClassRemapper classRemapper = new ClassRemapper(newNode, remapper);
            classNode.accept(classRemapper);

//            if (!classNode.name.equals(newNode.name))
//                Fume.fume.obfuscator.classTransforms.put(classNode.name, newNode.name);

            updatedClasses.put(newNode.name + ".class", newNode);
        }

        updatedClasses.forEach((s, classNode) -> {
            JObfImpl.classes.put(s, classNode);
        });
    }

//    @Override
//    public void processEntry(Callback<String> entryNameCallback, Callback<byte[]> data) {
//        final byte[] entryData = data.getObject();
//        final String entryName = entryNameCallback.getObject();
//
//        if(entryName.equals("META-INF/MANIFEST.MF")) {
//            final String s = new String(entryData);
//            final StringBuilder stringBuilder = new StringBuilder();
//
//            for(final String line : s.split("\n")) {
//                if(line.startsWith("Main-Class: ")) {
//                    final String oldClass = line.replace("Main-Class: ", "").replace("\r", "").replace('.', '/');
//
//                    if(Fume.fume.obfuscator.classTransforms.containsKey(oldClass))
//                        stringBuilder.append("Main-Class: ").append(Fume.fume.obfuscator.classTransforms.get(oldClass.replace('.', '/')).replace('/', '.')).append("\n");
//                    else
//                        stringBuilder.append(line);
//                    continue;
//                }
//
//                stringBuilder.append(line).append("\n");
//            }
//
//            try{
//                data.setObject(stringBuilder.toString().getBytes("UTF-8"));
//            }catch(UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

//    @Override
//    public void processClass(final ClassNode classNode) {
//        for(final MethodNode method : classNode.methods)
//            if(localVariables && method.localVariables != null && !method.localVariables.isEmpty())
//                method.localVariables.forEach(localVariableNode -> localVariableNode.name = NameUtils.generateLocalVariableName(classNode.name, method.name));
//    }
}