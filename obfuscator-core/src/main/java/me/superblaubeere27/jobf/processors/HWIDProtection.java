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
import me.superblaubeere27.hwid.HWID;
import me.superblaubeere27.jobf.IClassTransformer;
import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.ProcessorCallback;
import me.superblaubeere27.jobf.utils.NameUtils;
import me.superblaubeere27.jobf.utils.NodeUtils;
import me.superblaubeere27.jobf.utils.values.DeprecationLevel;
import me.superblaubeere27.jobf.utils.values.EnabledValue;
import me.superblaubeere27.jobf.utils.values.StringValue;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.Random;

public class HWIDProtection implements IClassTransformer {
    private static final String PROCESSOR_NAME = "HWIDPRotection";
    private static Random random = new Random();
    private JObfImpl inst;
    private EnabledValue enabled = new EnabledValue(PROCESSOR_NAME, DeprecationLevel.GOOD, false);
    private StringValue hwidValue = new StringValue(PROCESSOR_NAME, "HWID", DeprecationLevel.GOOD, HWID.bytesToHex(HWID.generateHWID()));

    public HWIDProtection(JObfImpl inst) {
        this.inst = inst;
    }

    private static String addHWIDGenerator(ClassNode cn) {
        MethodVisitor mv;
        String name;
        {
            mv = cn.visitMethod(Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC, name = NameUtils.generateMethodName(cn, "()B"), "()[B", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/security/NoSuchAlgorithmException");
            mv.visitLabel(l0);
            mv.visitLineNumber(23, l0);
            mv.visitLdcInsn("MD5");
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/security/MessageDigest", "getInstance", "(Ljava/lang/String;)Ljava/security/MessageDigest;", false);
            mv.visitVarInsn(Opcodes.ASTORE, 0);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitLineNumber(24, l3);
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitVarInsn(Opcodes.ASTORE, 1);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitLineNumber(26, l4);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitLdcInsn("os.name");
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "getProperty", "(Ljava/lang/String;)Ljava/lang/String;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(Opcodes.POP);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitLineNumber(27, l5);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitLdcInsn("os.arch");
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "getProperty", "(Ljava/lang/String;)Ljava/lang/String;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(Opcodes.POP);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitLineNumber(28, l6);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitLdcInsn("os.version");
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "getProperty", "(Ljava/lang/String;)Ljava/lang/String;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(Opcodes.POP);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitLineNumber(29, l7);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Runtime", "getRuntime", "()Ljava/lang/Runtime;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Runtime", "availableProcessors", "()I", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(Opcodes.POP);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitLineNumber(30, l8);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitLdcInsn("PROCESSOR_IDENTIFIER");
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "getenv", "(Ljava/lang/String;)Ljava/lang/String;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(Opcodes.POP);
            Label l9 = new Label();
            mv.visitLabel(l9);
            mv.visitLineNumber(31, l9);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitLdcInsn("PROCESSOR_ARCHITECTURE");
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "getenv", "(Ljava/lang/String;)Ljava/lang/String;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(Opcodes.POP);
            Label l10 = new Label();
            mv.visitLabel(l10);
            mv.visitLineNumber(32, l10);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitLdcInsn("PROCESSOR_ARCHITEW6432");
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "getenv", "(Ljava/lang/String;)Ljava/lang/String;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(Opcodes.POP);
            Label l11 = new Label();
            mv.visitLabel(l11);
            mv.visitLineNumber(33, l11);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitLdcInsn("NUMBER_OF_PROCESSORS");
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "getenv", "(Ljava/lang/String;)Ljava/lang/String;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(Opcodes.POP);
            Label l12 = new Label();
            mv.visitLabel(l12);
            mv.visitLineNumber(35, l12);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "getBytes", "()[B", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/security/MessageDigest", "digest", "([B)[B", false);
            mv.visitLabel(l1);
            mv.visitInsn(Opcodes.ARETURN);
            mv.visitLabel(l2);
            mv.visitLineNumber(36, l2);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/security/NoSuchAlgorithmException"});
            mv.visitVarInsn(Opcodes.ASTORE, 0);
            Label l13 = new Label();
            mv.visitLabel(l13);
            mv.visitLineNumber(37, l13);
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/Error");
            mv.visitInsn(Opcodes.DUP);
            mv.visitLdcInsn("Algorithm wasn't found.");
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Error", "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
            mv.visitInsn(Opcodes.ATHROW);
            mv.visitMaxs(4, 2);
            mv.visitEnd();
        }
        return name;
    }

    @Override
    public void process(ProcessorCallback callback, ClassNode node) {
        if (!enabled.getObject()) return;

        byte[] hwid = HWID.hexStringToByteArray(hwidValue.getObject());

        if (Modifier.isInterface(node.access)) {
            return;
        }

        String methodName = addHWIDGenerator(node);

        LabelNode l1 = new LabelNode();
        LabelNode l2 = new LabelNode();

        InsnList toAdd = new InsnList();

        toAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, node.name, methodName, "()[B", false));

        toAdd.add(NodeUtils.generateIntPush(hwid.length));
        toAdd.add(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_BYTE));
        toAdd.add(new InsnNode(Opcodes.DUP));

        for (int i = 0; i < hwid.length; i++) {
            toAdd.add(NodeUtils.generateIntPush(i));
            toAdd.add(NodeUtils.generateIntPush(hwid[i]));
            toAdd.add(new InsnNode(Opcodes.BASTORE));

            if (i != hwid.length - 1) {
                toAdd.add(new InsnNode(Opcodes.DUP));
            }
        }

        toAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Arrays", "equals", "([B[B)Z", false));

        toAdd.add(new JumpInsnNode(Opcodes.IFNE, l1));
        toAdd.add(l2);
        toAdd.add(new InsnNode(Opcodes.ACONST_NULL));
        toAdd.add(new LdcInsnNode("Invalid HWID (" + node.name + ")"));
        toAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "javax/swing/JOptionPane", "showMessageDialog", "(Ljava/awt/Component;Ljava/lang/Object;)V", false));

        toAdd.add(new IntInsnNode(Opcodes.SIPUSH, 1337));
        toAdd.add(new IntInsnNode(Opcodes.SIPUSH, 1337));
        toAdd.add(new IntInsnNode(Opcodes.SIPUSH, 1337));
        toAdd.add(new IntInsnNode(Opcodes.SIPUSH, 1337));
        toAdd.add(new IntInsnNode(Opcodes.SIPUSH, 1337));
        toAdd.add(new IntInsnNode(Opcodes.SIPUSH, 1337));
        toAdd.add(new IntInsnNode(Opcodes.SIPUSH, 1337));
        toAdd.add(new MultiANewArrayInsnNode("[[[[[[[J", 7));
        toAdd.add(new InsnNode(Opcodes.POP));

        toAdd.add(NodeUtils.generateIntPush(-1));
        toAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/System", "exit", "(I)V", false));

        toAdd.add(new TypeInsnNode(Opcodes.NEW, "java/lang/Error"));
        toAdd.add(new InsnNode(Opcodes.DUP));
        toAdd.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Error", "<init>", "()V", false));
        toAdd.add(new InsnNode(Opcodes.ATHROW));

        toAdd.add(l1);
        toAdd.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));

        MethodNode clInit = NodeUtils.getMethod(node, "<clinit>");
        if (clInit == null) {
            clInit = new MethodNode(Opcodes.ACC_STATIC, "<clinit>", "()V", null, new String[0]);
            node.methods.add(clInit);
        }

        if (clInit.instructions == null || clInit.instructions.getFirst() == null) {
            clInit.instructions = toAdd;
            clInit.instructions.add(new InsnNode(Opcodes.RETURN));
        } else {
            clInit.instructions.insertBefore(clInit.instructions.getFirst(), toAdd);
        }
        inst.setWorkDone();
    }

    @Override
    public ObfuscationTransformer getType() {
        return ObfuscationTransformer.HWID_PROTECTION;
    }
}