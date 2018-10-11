package me.superblaubeere27.jobf.utils;

import me.superblaubeere27.jobf.JObfImpl;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.Random;

public class Utils {
    private static final Random random = new Random();

    public static ClassNode lookupClass(String name) {
        ClassNode a = JObfImpl.INSTANCE.getClasspath().get(name);

        if (a != null) return a;

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

    public static boolean isInstruction(AbstractInsnNode node) {
        return !(node instanceof LineNumberNode) && !(node instanceof FrameNode) && !(node instanceof LabelNode);
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
            while (!Utils.isInstruction(next)) {
                next = next.getNext();
            }
        }
        return next;
    }

    public static AbstractInsnNode getNext(AbstractInsnNode node) {
        if (node == null) return null;
        AbstractInsnNode next = node.getNext();

        if (next == null) return null;

        while (!Utils.isInstruction(next)) {
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
        while (!Utils.isInstruction(prev)) {
            prev = prev.getPrevious();
        }
        return prev;
    }

    public static int random(int min, int max) {
        return min >= max ? min : random.nextInt(max - min) + min;
    }
}
