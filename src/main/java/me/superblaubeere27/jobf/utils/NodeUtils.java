package me.superblaubeere27.jobf.utils;

import me.superblaubeere27.jobf.JObf;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.lang.reflect.Modifier;

import static org.objectweb.asm.Opcodes.*;

/**
 * Copyright © 2015 - 2017 | CCBlueX | All rights reserved.
 * <p>
 * Fume - By CCBlueX(Marco)
 */
public class NodeUtils {

    public static boolean isIntegerNumber(AbstractInsnNode ain) {
        if (ain.getOpcode() == BIPUSH || ain.getOpcode() == SIPUSH) {
            return true;
        }
        if (ain.getOpcode() >= ICONST_M1 && ain.getOpcode() <= ICONST_5) {
            return true;
        }
        if (ain instanceof LdcInsnNode) {
            LdcInsnNode ldc = (LdcInsnNode) ain;
            return ldc.cst instanceof Integer;
        }
        return false;
    }

    public static AbstractInsnNode generateIntPush(int i) {
        if (i <= 5 && i >= -1) {
            return new InsnNode(i + 3); //iconst_i
        }
        if (i >= -128 && i <= 127) {
            return new IntInsnNode(BIPUSH, i);
        }

        if (i >= -32768 && i <= 32767) {
            return new IntInsnNode(SIPUSH, i);
        }
        return new LdcInsnNode(i);
    }

    public static int getIntValue(AbstractInsnNode node) {
        if (node.getOpcode() >= ICONST_M1 && node.getOpcode() <= ICONST_5) {
            return node.getOpcode() - 3;
        }
        if (node.getOpcode() == SIPUSH || node.getOpcode() == BIPUSH) {
            return ((IntInsnNode) node).operand;
        }
        if (node instanceof LdcInsnNode && ((LdcInsnNode) node).cst instanceof Integer) {
            return (int) ((LdcInsnNode) node).cst;
        }

        throw new IllegalArgumentException(node + " isn't an integer node");
    }

    public static MethodInsnNode toCallNode(final MethodNode method, final ClassNode classNode) {
        return new MethodInsnNode(Modifier.isStatic(method.access) ? Opcodes.INVOKESTATIC : Opcodes.INVOKEVIRTUAL, classNode.name, method.name, method.desc, false);
    }

    public static InsnList removeFromOpcode(InsnList insnList, int code) {
        for (AbstractInsnNode node :
                insnList.toArray().clone()) {
            if (node.getOpcode() == code) {
                insnList.remove(node);
            }
        }
        return insnList;
    }

    public static boolean isConditionalGoto(AbstractInsnNode abstractInsnNode) {
        return abstractInsnNode.getOpcode() >= Opcodes.IFEQ && abstractInsnNode.getOpcode() <= Opcodes.IF_ACMPNE;
    }

    public static int getFreeSlot(MethodNode method) {
        int max = 0;
        for (AbstractInsnNode ain :
                method.instructions.toArray()) {
            if (ain instanceof VarInsnNode) {
                if (((VarInsnNode) ain).var > max) {
                    max = ((VarInsnNode) ain).var;
                }
            }
        }
        return max + 1;
    }

    public static MethodNode getMethod(final ClassNode classNode, final String name) {
        for (final MethodNode method : classNode.methods)
            if (method.name.equals(name))
                return method;
        return null;
    }

    public static ClassNode toNode(final String className) throws IOException {
        final ClassReader classReader = new ClassReader(JObf.class.getResourceAsStream("/" + className.replace('.', '/') + ".class"));
        final ClassNode classNode = new ClassNode();

        classReader.accept(classNode, 0);

        return classNode;
    }
}