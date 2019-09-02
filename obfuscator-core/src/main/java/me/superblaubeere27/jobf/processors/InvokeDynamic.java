/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27.jobf.processors;

import me.superblaubeere27.annotations.ObfuscationTransformer;
import me.superblaubeere27.jobf.IClassTransformer;
import me.superblaubeere27.jobf.JObf;
import me.superblaubeere27.jobf.ProcessorCallback;
import me.superblaubeere27.jobf.utils.NameUtils;
import me.superblaubeere27.jobf.utils.NodeUtils;
import me.superblaubeere27.jobf.utils.Utils;
import me.superblaubeere27.jobf.utils.values.DeprecationLevel;
import me.superblaubeere27.jobf.utils.values.EnabledValue;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.objectweb.asm.Opcodes.*;

public class InvokeDynamic implements IClassTransformer {
    private static final String PROCESSOR_NAME = "InvokeDynamic";
    private static Random random = new Random();
    private EnabledValue enabled = new EnabledValue(PROCESSOR_NAME, "Hides method calls", DeprecationLevel.OK, false);


    private static MethodNode bootstrap(FieldNode arrayField, FieldNode typeField, ClassNode node) {
        String className = node.name;

        String referenceFieldName = arrayField.name;
        String referenceFieldType = arrayField.desc;

        String typeFieldName = typeField.name;
        String typeFieldType = typeField.desc;

        MethodNode mv;
        {
            mv = new MethodNode(Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC, NameUtils.generateMethodName(node.name, "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"), "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", null, new String[]{"java/lang/NoSuchMethodException", "java/lang/IllegalAccessException"});
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
            mv.visitLabel(l0);
            mv.visitLineNumber(16, l0);
            mv.visitFieldInsn(GETSTATIC, className, referenceFieldName, referenceFieldType);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
            mv.visitInsn(AALOAD);
            mv.visitLdcInsn(":");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "split", "(Ljava/lang/String;)[Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 3);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitLineNumber(17, l3);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ICONST_0);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            mv.visitVarInsn(ASTORE, 4);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitLineNumber(18, l4);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ASTORE, 5);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitLineNumber(19, l5);
            mv.visitInsn(ACONST_NULL);
            mv.visitVarInsn(ASTORE, 6);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitLineNumber(21, l6);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ICONST_3);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
            mv.visitVarInsn(ISTORE, 7);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitLineNumber(23, l7);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitInsn(ICONST_2);
            Label l8 = new Label();
            mv.visitJumpInsn(IF_ICMPGT, l8);
            Label l9 = new Label();
            mv.visitLabel(l9);
            mv.visitLineNumber(24, l9);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitLdcInsn(Type.getType("L" + className + ";"));
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType", "fromMethodDescriptorString", "(Ljava/lang/String;Ljava/lang/ClassLoader;)Ljava/lang/invoke/MethodType;", false);
            mv.visitVarInsn(ASTORE, 8);
            Label l10 = new Label();
            mv.visitLabel(l10);
            mv.visitLineNumber(26, l10);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitInsn(ICONST_2);
            Label l11 = new Label();
            mv.visitJumpInsn(IF_ICMPNE, l11);
            Label l12 = new Label();
            mv.visitLabel(l12);
            mv.visitLineNumber(27, l12);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findVirtual", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 6);
            Label l13 = new Label();
            mv.visitJumpInsn(GOTO, l13);
            mv.visitLabel(l11);
            mv.visitLineNumber(29, l11);
            mv.visitFrame(Opcodes.F_FULL, 9, new Object[]{"java/lang/invoke/MethodHandles$Lookup", "java/lang/String", "java/lang/invoke/MethodType", "[Ljava/lang/String;", "java/lang/Class", "java/lang/String", "java/lang/invoke/MethodHandle", Opcodes.INTEGER, "java/lang/invoke/MethodType"}, 0, new Object[]{});
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findStatic", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 6);
            mv.visitLabel(l13);
            mv.visitLineNumber(31, l13);
            mv.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
            Label l14 = new Label();
            mv.visitJumpInsn(GOTO, l14);
            mv.visitLabel(l8);
            mv.visitLineNumber(32, l8);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitFieldInsn(GETSTATIC, className, typeFieldName, typeFieldType);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ASTORE, 8);
            Label l15 = new Label();
            mv.visitLabel(l15);
            mv.visitLineNumber(34, l15);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitInsn(ICONST_3);
            Label l16 = new Label();
            mv.visitJumpInsn(IF_ICMPNE, l16);
            Label l17 = new Label();
            mv.visitLabel(l17);
            mv.visitLineNumber(35, l17);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findGetter", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 6);
            mv.visitJumpInsn(GOTO, l14);
            mv.visitLabel(l16);
            mv.visitLineNumber(36, l16);
            mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/lang/Class"}, 0, null);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitInsn(ICONST_4);
            Label l18 = new Label();
            mv.visitJumpInsn(IF_ICMPNE, l18);
            Label l19 = new Label();
            mv.visitLabel(l19);
            mv.visitLineNumber(37, l19);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findStaticGetter", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 6);
            mv.visitJumpInsn(GOTO, l14);
            mv.visitLabel(l18);
            mv.visitLineNumber(38, l18);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitInsn(ICONST_5);
            Label l20 = new Label();
            mv.visitJumpInsn(IF_ICMPNE, l20);
            Label l21 = new Label();
            mv.visitLabel(l21);
            mv.visitLineNumber(39, l21);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findSetter", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 6);
            mv.visitJumpInsn(GOTO, l14);
            mv.visitLabel(l20);
            mv.visitLineNumber(41, l20);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findStaticSetter", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 6);
            mv.visitLabel(l14);
            mv.visitLineNumber(45, l14);
            mv.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
            mv.visitTypeInsn(NEW, "java/lang/invoke/ConstantCallSite");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 6);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/invoke/ConstantCallSite", "<init>", "(Ljava/lang/invoke/MethodHandle;)V", false);
            mv.visitLabel(l1);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l2);
            mv.visitLineNumber(46, l2);
            mv.visitFrame(Opcodes.F_FULL, 3, new Object[]{"java/lang/invoke/MethodHandles$Lookup", "java/lang/String", "java/lang/invoke/MethodType"}, 1, new Object[]{"java/lang/Exception"});
            mv.visitVarInsn(ASTORE, 3);
            Label l22 = new Label();
            mv.visitLabel(l22);
            mv.visitLineNumber(47, l22);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false);
            Label l23 = new Label();
            mv.visitLabel(l23);
            mv.visitLineNumber(48, l23);
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            Label l24 = new Label();
            mv.visitLabel(l24);
            mv.visitLocalVariable("methodDesc", "Ljava/lang/invoke/MethodType;", null, l10, l13, 8);
            mv.visitLocalVariable("typeLookup", "Ljava/lang/Class;", null, l15, l14, 8);
            mv.visitLocalVariable("split", "[Ljava/lang/String;", null, l3, l2, 3);
            mv.visitLocalVariable("classIn", "Ljava/lang/Class;", "Ljava/lang/Class<*>;", l4, l2, 4);
            mv.visitLocalVariable("name", "Ljava/lang/String;", null, l5, l2, 5);
            mv.visitLocalVariable("methodHandle", "Ljava/lang/invoke/MethodHandle;", null, l6, l2, 6);
            mv.visitLocalVariable("length", "I", null, l7, l2, 7);
            mv.visitLocalVariable("ex", "Ljava/lang/Exception;", null, l22, l24, 3);
            mv.visitLocalVariable("lookup", "Ljava/lang/invoke/MethodHandles$Lookup;", null, l0, l24, 0);
            mv.visitLocalVariable("s", "Ljava/lang/String;", null, l0, l24, 1);
            mv.visitLocalVariable("methodType", "Ljava/lang/invoke/MethodType;", null, l0, l24, 2);
            mv.visitMaxs(4, 9);
            mv.visitEnd();

//            mv = new MethodNode(Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC, NameUtils.generateMethodName(node.name, "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"), "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", null, new String[]{"java/lang/NoSuchMethodException", "java/lang/IllegalAccessException"});
//            mv.visitCode();
//            Label l0 = new Label();
//            Label l1 = new Label();
//            Label l2 = new Label();
//            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
//            mv.visitLabel(l0);
//            mv.visitLineNumber(21, l0);
//            mv.visitFieldInsn(Opcodes.GETSTATIC, node.name, arrayField.name, arrayField.desc);
//            mv.visitVarInsn(Opcodes.ALOAD, 1);
//            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
//            mv.visitInsn(Opcodes.AALOAD);
//            mv.visitLdcInsn(":");
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "split", "(Ljava/lang/String;)[Ljava/lang/String;", false);
//            mv.visitVarInsn(Opcodes.ASTORE, 3);
//            Label l3 = new Label();
//            mv.visitLabel(l3);
//            mv.visitLineNumber(22, l3);
//            mv.visitVarInsn(Opcodes.ALOAD, 3);
//            mv.visitInsn(Opcodes.ICONST_0);
//            mv.visitInsn(Opcodes.AALOAD);
//            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
//            mv.visitVarInsn(Opcodes.ASTORE, 4);
//            Label l4 = new Label();
//            mv.visitLabel(l4);
//            mv.visitLineNumber(23, l4);
//            mv.visitVarInsn(Opcodes.ALOAD, 3);
//            mv.visitInsn(Opcodes.ICONST_1);
//            mv.visitInsn(Opcodes.AALOAD);
//            mv.visitVarInsn(Opcodes.ASTORE, 5);
//            Label l5 = new Label();
//            mv.visitLabel(l5);
//            mv.visitLineNumber(24, l5);
//            mv.visitVarInsn(Opcodes.ALOAD, 3);
//            mv.visitInsn(Opcodes.ICONST_2);
//            mv.visitInsn(Opcodes.AALOAD);
//            mv.visitLdcInsn(Type.getType("L" + node.name + ";"));
//            Label l6 = new Label();
//            mv.visitLabel(l6);
//            mv.visitLineNumber(25, l6);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;", false);
//            Label l7 = new Label();
//            mv.visitLabel(l7);
//            mv.visitLineNumber(24, l7);
//            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/invoke/MethodType", "fromMethodDescriptorString", "(Ljava/lang/String;Ljava/lang/ClassLoader;)Ljava/lang/invoke/MethodType;", false);
//            mv.visitVarInsn(Opcodes.ASTORE, 6);
//            Label l8 = new Label();
//            mv.visitLabel(l8);
//            mv.visitLineNumber(26, l8);
//            mv.visitVarInsn(Opcodes.ALOAD, 3);
//            mv.visitInsn(Opcodes.ICONST_3);
//            mv.visitInsn(Opcodes.AALOAD);
//            mv.visitVarInsn(Opcodes.ASTORE, 7);
//            Label l9 = new Label();
//            mv.visitLabel(l9);
//            mv.visitLineNumber(27, l9);
//            mv.visitInsn(Opcodes.ACONST_NULL);
//            mv.visitVarInsn(Opcodes.ASTORE, 8);
//            Label l10 = new Label();
//            mv.visitLabel(l10);
//            mv.visitLineNumber(33, l10);
//            mv.visitVarInsn(Opcodes.ALOAD, 7);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
//            mv.visitInsn(Opcodes.ICONST_1);
//            Label l11 = new Label();
//            mv.visitJumpInsn(Opcodes.IF_ICMPLE, l11);
//            Label l12 = new Label();
//            mv.visitLabel(l12);
//            mv.visitLineNumber(34, l12);
//            mv.visitVarInsn(Opcodes.ALOAD, 0);
//            mv.visitVarInsn(Opcodes.ALOAD, 4);
//            mv.visitVarInsn(Opcodes.ALOAD, 5);
//            mv.visitVarInsn(Opcodes.ALOAD, 6);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findVirtual", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
////            mv.visitVarInsn(Opcodes.ALOAD, 2);
////            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "asType", "(Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
//            mv.visitVarInsn(Opcodes.ASTORE, 8);
//            Label l13 = new Label();
//            mv.visitJumpInsn(Opcodes.GOTO, l13);
//            mv.visitLabel(l11);
//            mv.visitLineNumber(36, l11);
//            mv.visitFrame(Opcodes.F_FULL, 9, new Object[]{"java/lang/invoke/MethodHandles$Lookup", "java/lang/String", "java/lang/invoke/MethodType", "[Ljava/lang/String;", "java/lang/Class", "java/lang/String", "java/lang/invoke/MethodType", "java/lang/String", "java/lang/invoke/MethodHandle"}, 0, new Object[]{});
//            mv.visitVarInsn(Opcodes.ALOAD, 0);
//            mv.visitVarInsn(Opcodes.ALOAD, 4);
//            mv.visitVarInsn(Opcodes.ALOAD, 5);
//            mv.visitVarInsn(Opcodes.ALOAD, 6);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findStatic", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
//            mv.visitVarInsn(Opcodes.ASTORE, 8);
//            mv.visitLabel(l13);
//            mv.visitLineNumber(38, l13);
//            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
//            mv.visitTypeInsn(Opcodes.NEW, "java/lang/invoke/ConstantCallSite");
//            mv.visitInsn(Opcodes.DUP);
//            mv.visitVarInsn(Opcodes.ALOAD, 8);
//            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/invoke/ConstantCallSite", "<init>", "(Ljava/lang/invoke/MethodHandle;)V", false);
//            mv.visitLabel(l1);
//            mv.visitInsn(Opcodes.ARETURN);
//            mv.visitLabel(l2);
//            mv.visitLineNumber(39, l2);
//            mv.visitFrame(Opcodes.F_FULL, 3, new Object[]{"java/lang/invoke/MethodHandles$Lookup", "java/lang/String", "java/lang/invoke/MethodType"}, 1, new Object[]{"java/lang/Exception"});
//            mv.visitVarInsn(Opcodes.ASTORE, 3);
//            Label l14 = new Label();
//            mv.visitLabel(l14);
//            mv.visitLineNumber(40, l14);
//            mv.visitVarInsn(Opcodes.ALOAD, 3);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false);
//            Label l15 = new Label();
//            mv.visitLabel(l15);
//            mv.visitLineNumber(41, l15);
//            mv.visitInsn(Opcodes.ACONST_NULL);
//            mv.visitInsn(Opcodes.ARETURN);
//            mv.visitMaxs(4, 9);
//            mv.visitEnd();
        }
        return mv;
    }

    @Override
    public void process(ProcessorCallback callback, ClassNode classNode) {
        if (!enabled.getObject()) return;

        if (!NodeUtils.isClassValid(classNode)) {
            return;
        }
        if (classNode.version == Opcodes.V1_1 || classNode.version < Opcodes.V1_4) {
            JObf.log.warning("!!! WARNING !!! " + classNode.name + "'s lang level is too low (VERSION < V1_4)");
            return;
        }

        FieldNode arrayField = new FieldNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, NameUtils.generateFieldName(classNode), "[Ljava/lang/String;", null, null);
        FieldNode typeArrayField = new FieldNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, NameUtils.generateFieldName(classNode), "[Ljava/lang/Class;", null, null);


        MethodNode bootstrap = bootstrap(arrayField, typeArrayField, classNode);
        Handle bootstrapMethod = new Handle(H_INVOKESTATIC, classNode.name, bootstrap.name,
                MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class,
                        MethodType.class).toMethodDescriptorString(), false);

        int count = 0;
        int indexCount = 0;

        int typeCount = 0;

        HashMap<String, Integer> map = new HashMap<>();
        HashMap<Type, Integer> typeMap = new HashMap<>();

        for (MethodNode method : classNode.methods) {
            if (!NodeUtils.isMethodValid(method)) {
                continue;
            }

            for (AbstractInsnNode abstractInsnNode : method.instructions.toArray()) {
                if (abstractInsnNode instanceof MethodInsnNode) {
                    MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;

                    if (methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL || methodInsnNode.getOpcode() == Opcodes.INVOKEINTERFACE) {
                        String name = methodInsnNode.owner.replace('/', '.') + ":" + methodInsnNode.name + ":" + methodInsnNode.desc + ":" + NameUtils.generateSpaceString(2);
                        int index;

                        if (map.containsKey(name)) {
                            index = map.get(name);
                        } else {
                            index = indexCount++;
                            map.put(name, index);
                        }

                        method.instructions.insert(methodInsnNode, new InvokeDynamicInsnNode(Integer.toString(index), (methodInsnNode.owner.startsWith("[") ? "(" : "(L") + methodInsnNode.owner + (methodInsnNode.owner.endsWith(";") ? "" : ";") + methodInsnNode.desc.substring(1), bootstrapMethod));
                        method.instructions.remove(methodInsnNode);
                        count++;
                    }
                    if (methodInsnNode.getOpcode() == Opcodes.INVOKESTATIC) {
                        String name = methodInsnNode.owner.replace('/', '.') + ":" + methodInsnNode.name + ":" + methodInsnNode.desc + ":" + NameUtils.generateSpaceString(1);
                        int index;

                        if (map.containsKey(name)) {
                            index = map.get(name);
                        } else {
                            index = indexCount++;
                            map.put(name, index);
                        }
                        method.instructions.insert(methodInsnNode, new InvokeDynamicInsnNode(Integer.toString(index), methodInsnNode.desc, bootstrapMethod));
                        method.instructions.remove(methodInsnNode);
                        count++;
                    }
                }
                if (abstractInsnNode instanceof FieldInsnNode) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) abstractInsnNode;

                    Type fieldType = Type.getType(fieldInsnNode.desc);
                    int typeIndex;

                    if (typeMap.containsKey(fieldType)) {
                        typeIndex = typeMap.get(fieldType);
                    } else {
                        typeIndex = typeCount++;
                        typeMap.put(fieldType, typeIndex);
                    }


                    if (fieldInsnNode.getOpcode() == Opcodes.GETFIELD) {
                        String name = fieldInsnNode.owner.replace('/', '.') + ":" + fieldInsnNode.name + ":" + typeIndex + ":" + NameUtils.generateSpaceString(3);
                        int index;

                        if (map.containsKey(name)) {
                            index = map.get(name);
                        } else {
                            index = indexCount++;
                            map.put(name, index);
                        }

                        method.instructions.insert(fieldInsnNode, new InvokeDynamicInsnNode(Integer.toString(index), "(L" + fieldInsnNode.owner + ";)" + fieldInsnNode.desc, bootstrapMethod));
                        method.instructions.remove(fieldInsnNode);
                        count++;
                    } else if (fieldInsnNode.getOpcode() == Opcodes.GETSTATIC) {
                        String name = fieldInsnNode.owner.replace('/', '.') + ":" + fieldInsnNode.name + ":" + typeIndex + ":" + NameUtils.generateSpaceString(4);
                        int index;

                        if (map.containsKey(name)) {
                            index = map.get(name);
                        } else {
                            index = indexCount++;
                            map.put(name, index);
                        }

                        method.instructions.insert(fieldInsnNode, new InvokeDynamicInsnNode(Integer.toString(index), "()" + fieldInsnNode.desc, bootstrapMethod));
                        method.instructions.remove(fieldInsnNode);
                        count++;
                    }
                    ClassNode owner = Utils.lookupClass(fieldInsnNode.owner);
                    FieldNode field = null;

                    if (owner != null) field = Utils.getField(owner, fieldInsnNode.name);

                    if (field == null) {
                        JObf.log.warning("Field " + fieldInsnNode.owner + "." + fieldInsnNode.name + " wasn't found. Please add it as library");
                        continue;
                    }
                    if (Modifier.isFinal(field.access)) {
                        continue;
                    }

                    if (fieldInsnNode.getOpcode() == Opcodes.PUTFIELD) {
                        String name = fieldInsnNode.owner.replace('/', '.') + ":" + fieldInsnNode.name + ":" + typeIndex + ":" + NameUtils.generateSpaceString(5);
                        int index;

                        if (map.containsKey(name)) {
                            index = map.get(name);
                        } else {
                            index = indexCount++;
                            map.put(name, index);
                        }

                        method.instructions.insert(fieldInsnNode, new InvokeDynamicInsnNode(Integer.toString(index), "(L" + fieldInsnNode.owner + ";" + fieldInsnNode.desc + ")V", bootstrapMethod));
                        method.instructions.remove(fieldInsnNode);
                        count++;
                    } else if (fieldInsnNode.getOpcode() == Opcodes.PUTSTATIC) {
                        String name = fieldInsnNode.owner.replace('/', '.') + ":" + fieldInsnNode.name + ":" + typeIndex + ":" + NameUtils.generateSpaceString(6);
                        int index;

                        if (map.containsKey(name)) {
                            index = map.get(name);
                        } else {
                            index = indexCount++;
                            map.put(name, index);
                        }

                        method.instructions.insert(fieldInsnNode, new InvokeDynamicInsnNode(Integer.toString(index), "(" + fieldInsnNode.desc + ")V", bootstrapMethod));
                        method.instructions.remove(fieldInsnNode);
                        count++;
                    }
                }
            }
        }
//        System.out.println(count);


        if (count > 0) {
            if (classNode.version < Opcodes.V1_7) {
                callback.setForceComputeFrames();
            }
            classNode.version = Math.max(Opcodes.V1_7, classNode.version);

            MethodNode generatorMethod = new MethodNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, NameUtils.generateMethodName(classNode, "()V"), "()V", null, new String[0]);
            InsnList generatorMethodNodes = new InsnList();

            {
                List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());

                Collections.shuffle(list);

                generatorMethodNodes.add(NodeUtils.generateIntPush(list.size()));
                generatorMethodNodes.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/String"));
                generatorMethodNodes.add(new FieldInsnNode(Opcodes.PUTSTATIC, classNode.name, arrayField.name, arrayField.desc));

                for (Map.Entry<String, Integer> integerStringEntry : list) {
                    generatorMethodNodes.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, arrayField.name, arrayField.desc));
                    generatorMethodNodes.add(NodeUtils.generateIntPush(integerStringEntry.getValue()));
                    generatorMethodNodes.add(new LdcInsnNode(integerStringEntry.getKey()));
                    generatorMethodNodes.add(new InsnNode(Opcodes.AASTORE));
                }
            }
            {
                List<Map.Entry<Type, Integer>> list = new ArrayList<>(typeMap.entrySet());

                Collections.shuffle(list);

                generatorMethodNodes.add(NodeUtils.generateIntPush(list.size()));
                generatorMethodNodes.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/Class"));
                generatorMethodNodes.add(new FieldInsnNode(Opcodes.PUTSTATIC, classNode.name, typeArrayField.name, typeArrayField.desc));

                for (Map.Entry<Type, Integer> integerStringEntry : list) {
                    generatorMethodNodes.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, typeArrayField.name, typeArrayField.desc));
                    generatorMethodNodes.add(NodeUtils.generateIntPush(integerStringEntry.getValue()));

                    if (integerStringEntry.getKey().getSort() == Type.ARRAY || integerStringEntry.getKey().getSort() == Type.OBJECT) {
                        generatorMethodNodes.add(new LdcInsnNode(integerStringEntry.getKey()));
                    } else {
                        generatorMethodNodes.add(NodeUtils.getTypeNode(integerStringEntry.getKey()));
                    }
                    generatorMethodNodes.add(new InsnNode(Opcodes.AASTORE));
                }
            }

            generatorMethodNodes.add(new InsnNode(Opcodes.RETURN));

            generatorMethod.instructions = generatorMethodNodes;

            MethodNode clInit = NodeUtils.getMethod(classNode, "<clinit>");

            if (clInit == null) {
                clInit = new MethodNode(Opcodes.ACC_STATIC, "<clinit>", "()V", null, new String[0]);
                classNode.methods.add(clInit);
            }
            if (clInit.instructions == null)
                clInit.instructions = new InsnList();

            if (clInit.instructions.getFirst() == null) {
                clInit.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, classNode.name, generatorMethod.name, generatorMethod.desc, false));
                clInit.instructions.add(new InsnNode(Opcodes.RETURN));
            } else {
                clInit.instructions.insertBefore(clInit.instructions.getFirst(), new MethodInsnNode(Opcodes.INVOKESTATIC, classNode.name, generatorMethod.name, generatorMethod.desc, false));
            }


            classNode.methods.add(bootstrap);
            classNode.methods.add(generatorMethod);
            classNode.fields.add(arrayField);
            classNode.fields.add(typeArrayField);
        }

    }

    @Override
    public ObfuscationTransformer getType() {
        return ObfuscationTransformer.INVOKE_DYNAMIC;
    }

}