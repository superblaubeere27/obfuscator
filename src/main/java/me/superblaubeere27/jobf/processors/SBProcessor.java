package me.superblaubeere27.jobf.processors;

import me.superblaubeere27.jobf.IClassProcessor;
import me.superblaubeere27.jobf.JObfImpl;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Random;

public class SBProcessor implements IClassProcessor {
    private static Random random = new Random();
    private JObfImpl inst;

    public SBProcessor(JObfImpl inst) {
        this.inst = inst;
    }

    @Override
    public void process(ClassNode node, int mode) {
        if ((node.access & Opcodes.ACC_INTERFACE) == 0) {
            for (MethodNode method : node.methods) {
//            if ((method.access & Opcodes.ACC_BRIDGE) == 0 && (method.access & Opcodes.ACC_STATIC) == 0 && !method.name.startsWith("<")) {
//                method.access |= Opcodes.ACC_BRIDGE;
//            }
//            if ((method.access & Opcodes.ACC_SYNTHETIC) == 0) {
                if (method.name.startsWith("<"))
                    continue;
                if ((method.access & Opcodes.ACC_NATIVE) == 0) {
                    continue;
                }
                method.access = method.access | Opcodes.ACC_BRIDGE;
                method.access = method.access | Opcodes.ACC_SYNTHETIC;
//            }
            }
        }
        for (FieldNode field : node.fields) {
//            if ((field.access & Opcodes.ACC_FINAL) == 0)
            field.access = field.access | Opcodes.ACC_SYNTHETIC;
        }
//        if ((node.access & Opcodes.ACC_FINAL) == 0) {
//            node.access = node.access | Opcodes.ACC_SYNTHETIC;
//        }
        inst.setWorkDone();
    }

}