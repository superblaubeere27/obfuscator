/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27.jobf.utils;

import me.superblaubeere27.jobf.JObf;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.function.Predicate;

import static org.objectweb.asm.Opcodes.*;

public class NodeUtils {
    private static final Printer printer = new Textifier();
    private static final TraceMethodVisitor methodPrinter = new TraceMethodVisitor(printer);
    private static HashMap<Type, String> TYPE_TO_WRAPPER = new HashMap<>();

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
    }

    public static String prettyprint(AbstractInsnNode insnNode) {
        insnNode.accept(methodPrinter);
        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();
        return sw.toString().trim();
    }

    public static String prettyprint(InsnList insnNode) {
        insnNode.accept(methodPrinter);
        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();
        return sw.toString().trim();
    }

    public static String prettyprint(MethodNode insnNode) {
        insnNode.accept(methodPrinter);
        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();
        return sw.toString().trim();
    }

    public static AbstractInsnNode getWrapperMethod(Type type) {
        if (type.getSort() != Type.VOID && TYPE_TO_WRAPPER.containsKey(type)) {
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
            String internalName = Utils.getInternalName(type);
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

    public static AbstractInsnNode methodCall(ClassNode classNode, MethodNode methodNode) {
        int opcode = Opcodes.INVOKEVIRTUAL;

        if (Modifier.isInterface(classNode.access)) {
            opcode = Opcodes.INVOKEINTERFACE;
        }
        if (Modifier.isStatic(methodNode.access)) {
            opcode = Opcodes.INVOKESTATIC;
        }
        if (methodNode.name.startsWith("<")) {
            opcode = Opcodes.INVOKESPECIAL;
        }

        return new MethodInsnNode(opcode, classNode.name, methodNode.name, methodNode.desc, false);
    }

    public static void insertOn(InsnList instructions, Predicate<AbstractInsnNode> predicate, InsnList toAdd) {
        for (AbstractInsnNode abstractInsnNode : instructions.toArray()) {
            if (predicate.test(abstractInsnNode)) {
                instructions.insertBefore(abstractInsnNode, toAdd);
            }
        }
    }

    public static InsnList nullPush() {
        InsnList insns = new InsnList();

        insns.add(new InsnNode(Opcodes.ACONST_NULL));

        return insns;
    }

    public static InsnList notNullPush() {
        throw new RuntimeException("Not implemented");
//        InsnList insns = new InsnList();

//        insns.add(new LdcInsnNode(Math.random() * 100));
//        insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false));
//        insns.add(new TypeInsnNode());
//        insns.add(new LdcInsnNode(Type.getType("Ljava/lang/System;")));
//        insns.add(new FieldInsnNode(""));
//        return insns;
    }

    public static InsnList debugString(String s) {
        InsnList insns = new InsnList();
        insns.add(new LdcInsnNode(s));
        insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I", false));
        insns.add(new InsnNode(Opcodes.POP));
        return insns;
    }

    public static AbstractInsnNode nullValueForType(Type returnType) {
        switch (returnType.getSort()) {
            case Type.BOOLEAN:
            case Type.BYTE:
            case Type.CHAR:
            case Type.SHORT:
            case Type.INT:
                return new InsnNode(ICONST_0);
            case Type.FLOAT:
                return new InsnNode(FCONST_0);
            case Type.DOUBLE:
                return new InsnNode(DCONST_0);
            case Type.LONG:
                return new InsnNode(LCONST_0);
            case Type.ARRAY:
            case Type.OBJECT:
                return new InsnNode(ACONST_NULL);
            default:
                throw new UnsupportedOperationException();
        }
    }

//    public static int getTypeLoad(Type argumentType) {
//        if (argumentType.getOpcode()) {
//
//        }
//
//        return NodeUtils.TYPE_TO_LOAD.get(argumentType);
//    }
}