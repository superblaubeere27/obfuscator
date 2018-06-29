package me.superblaubeere27.jobf.utils;

import me.superblaubeere27.jobf.JObf;
import me.superblaubeere27.jobf.util.Util;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import static org.objectweb.asm.Opcodes.*;

/**
 * Copyright © 2015 - 2017 | CCBlueX | All rights reserved.
 * <p>
 * Fume - By CCBlueX(Marco)
 */
public class NodeUtils {
    public final static HashMap<Type, Integer> TYPE_TO_LOAD = new HashMap<>();
    public final static HashMap<Type, Integer> TYPE_TO_STORE = new HashMap<>();
    public static HashMap<Type, String> TYPE_TO_WRAPPER = new HashMap<>();

    static {
        TYPE_TO_WRAPPER.put(Type.INT_TYPE, "java/lang/Integer");
        TYPE_TO_WRAPPER.put(Type.VOID_TYPE, "java/lang/Void");
        TYPE_TO_WRAPPER.put(Type.BOOLEAN_TYPE, "java/lang/Boolean");
        TYPE_TO_WRAPPER.put(Type.CHAR_TYPE, "java/lang/Character");
        TYPE_TO_WRAPPER.put(Type.BYTE_TYPE, "java/lang/Byte");
        TYPE_TO_WRAPPER.put(Type.SHORT_TYPE, "java/lang/Short");
        TYPE_TO_WRAPPER.put(Type.FLOAT_TYPE, "java/lang/Float");
        TYPE_TO_WRAPPER.put(Type.LONG_TYPE, "java/lang/Long");
        TYPE_TO_WRAPPER.put(Type.DOUBLE_TYPE, "java/lang/Double");

        TYPE_TO_LOAD.put(Type.INT_TYPE, Opcodes.ILOAD);
        TYPE_TO_LOAD.put(Type.VOID_TYPE, Opcodes.NOP);
        TYPE_TO_LOAD.put(Type.BOOLEAN_TYPE, Opcodes.ILOAD);
        TYPE_TO_LOAD.put(Type.CHAR_TYPE, Opcodes.ILOAD);
        TYPE_TO_LOAD.put(Type.BYTE_TYPE, Opcodes.ILOAD);
        TYPE_TO_LOAD.put(Type.SHORT_TYPE, Opcodes.ILOAD);
        TYPE_TO_LOAD.put(Type.FLOAT_TYPE, Opcodes.FLOAD);
        TYPE_TO_LOAD.put(Type.LONG_TYPE, Opcodes.LLOAD);
        TYPE_TO_LOAD.put(Type.DOUBLE_TYPE, Opcodes.DLOAD);

        TYPE_TO_STORE.put(Type.INT_TYPE, Opcodes.ISTORE);
        TYPE_TO_STORE.put(Type.VOID_TYPE, Opcodes.NOP);
        TYPE_TO_STORE.put(Type.BOOLEAN_TYPE, Opcodes.ISTORE);
        TYPE_TO_STORE.put(Type.CHAR_TYPE, Opcodes.ISTORE);
        TYPE_TO_STORE.put(Type.BYTE_TYPE, Opcodes.ISTORE);
        TYPE_TO_STORE.put(Type.SHORT_TYPE, Opcodes.ISTORE);
        TYPE_TO_STORE.put(Type.FLOAT_TYPE, Opcodes.FSTORE);
        TYPE_TO_STORE.put(Type.LONG_TYPE, Opcodes.LSTORE);
        TYPE_TO_STORE.put(Type.DOUBLE_TYPE, Opcodes.DLOAD);
    }

    public static AbstractInsnNode getWrapperMethod(Type type) {
        if (TYPE_TO_WRAPPER.containsKey(type)) {
            return new MethodInsnNode(Opcodes.INVOKESTATIC, TYPE_TO_WRAPPER.get(type), "valueOf", "(" + type.toString() + ")L" + TYPE_TO_WRAPPER.get(type) + ";", false);
        }

        return new InsnNode(Opcodes.NOP);
    }

    public static AbstractInsnNode getTypeNode(Type type) {
        if (TYPE_TO_WRAPPER.containsKey(type)) {
            return new FieldInsnNode(Opcodes.GETSTATIC, TYPE_TO_WRAPPER.get(type), "TYPE", "Ljava/lang/Class;");
        }
        return new LdcInsnNode(type);
    }

    public static AbstractInsnNode getUnWrapMethod(Type type) {
        if (TYPE_TO_WRAPPER.containsKey(type)) {
            String internalName = Util.getInternalName(type);
            return new MethodInsnNode(Opcodes.INVOKESTATIC, TYPE_TO_WRAPPER.get(type), internalName + "Value", "(L" + TYPE_TO_WRAPPER.get(type) + ";)" + type.toString(), false);
        }

        return new InsnNode(Opcodes.NOP);
    }

    //mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);

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

    public static int getInvertedJump(int opcode) {
        int i = -1;

        switch (opcode) {
            case Opcodes.IFEQ:
                i = Opcodes.IFNE;
                break;
            case Opcodes.IFNE:
                i = Opcodes.IFEQ;
                break;
            case Opcodes.IF_ACMPEQ:
                i = Opcodes.IF_ACMPNE;
                break;
            case Opcodes.IF_ACMPNE:
                i = Opcodes.IF_ACMPEQ;
                break;
        }
        return i;
    }

    public static boolean isMethodValid(MethodNode method) {
        return !Modifier.isNative(method.access) && !Modifier.isAbstract(method.access) && method.instructions.size() != 0;
    }

    public static boolean isClassValid(ClassNode node) {
        return (node.access & Opcodes.ACC_ENUM) == 0 && (node.access & Opcodes.ACC_INTERFACE) == 0;
    }

//    public static int getTypeLoad(Type argumentType) {
//        if (argumentType.getOpcode()) {
//
//        }
//
//        return NodeUtils.TYPE_TO_LOAD.get(argumentType);
//    }
}