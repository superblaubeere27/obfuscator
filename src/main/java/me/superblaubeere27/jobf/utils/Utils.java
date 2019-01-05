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

import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.processors.name.ClassWrapper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Random;

public class Utils {
    private static final Random random = new Random();

    public static ClassNode lookupClass(String name) {
        ClassWrapper a = JObfImpl.INSTANCE.getClassPath().get(name);

        if (a != null) return a.classNode;

        return JObfImpl.getClasses().get(name);
    }


    public static MethodNode getMethod(ClassNode cls, String name, String desc) {
        for (MethodNode method : cls.methods) {
            if (method.name.equals(name) && method.desc.equals(desc))
                return method;
        }
        return null;
    }

    public static FieldNode getField(ClassNode cls, String name) {
        for (FieldNode method : cls.fields) {
            if (method.name.equals(name))
                return method;
        }
        return null;
    }

    private static boolean isNotInstruction(AbstractInsnNode node) {
        return node instanceof LineNumberNode || node instanceof FrameNode || node instanceof LabelNode;
    }

    public static boolean notAbstractOrNative(MethodNode methodNode) {
        return !Modifier.isNative(methodNode.access) && !Modifier.isAbstract(methodNode.access);
    }

    public static AbstractInsnNode getNextFollowGoto(AbstractInsnNode node) {
        AbstractInsnNode next = node.getNext();
        while (next instanceof LabelNode || next instanceof LineNumberNode || next instanceof FrameNode) {
            next = next.getNext();
        }
        if (next.getOpcode() == Opcodes.GOTO) {
            JumpInsnNode cast = (JumpInsnNode) next;
            next = cast.label;
            while (Utils.isNotInstruction(next)) {
                next = next.getNext();
            }
        }
        return next;
    }

    public static AbstractInsnNode getNext(AbstractInsnNode node) {
        if (node == null) return null;
        AbstractInsnNode next = node.getNext();

        if (next == null) return null;

        while (Utils.isNotInstruction(next)) {
            next = next.getNext();

            if (next == null) break;
        }
        return next;
    }

    public static AbstractInsnNode getPrevious(AbstractInsnNode node, int amount) {
        for (int i = 0; i < amount; i++) {
            node = getPrevious(node);
        }
        return node;
    }

    public static AbstractInsnNode getPrevious(AbstractInsnNode node) {
        AbstractInsnNode prev = node.getPrevious();
        while (Utils.isNotInstruction(prev)) {
            prev = prev.getPrevious();
        }
        return prev;
    }

    public static int random(int min, int max) {
        return min >= max ? min : random.nextInt(max - min) + min;
    }

    public static <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    public static HashMap<LabelNode, LabelNode> generateNewLabelMap(InsnList insnList) {
        HashMap<LabelNode, LabelNode> labelNodeHashMap = new HashMap<>();

        for (AbstractInsnNode abstractInsnNode : insnList.toArray()) {
            if (abstractInsnNode instanceof LabelNode) {
                LabelNode label = (LabelNode) abstractInsnNode;

                labelNodeHashMap.put(label, new LabelNode());
            }
        }

        return labelNodeHashMap;
    }

    public static boolean matchMethodNode(MethodInsnNode methodInsnNode, String s) {
        return s.equals(methodInsnNode.owner + "." + methodInsnNode.name + ":" + methodInsnNode.desc);
    }
}
