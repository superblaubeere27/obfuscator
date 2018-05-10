package me.superblaubeere27.jobf.processors;

import me.superblaubeere27.jobf.IClassProcessor;
import me.superblaubeere27.jobf.JObfImpl;
import org.objectweb.asm.tree.ClassNode;

import java.util.Random;

public class LineNumberRemover implements IClassProcessor {
    private static Random random = new Random();
    private JObfImpl inst;

    public LineNumberRemover(JObfImpl inst) {
        this.inst = inst;
    }

    @Override
    public void process(ClassNode node, int mode) {
//        for (MethodNode method : node.methods) {
//            for (AbstractInsnNode abstractInsnNode : method.instructions.toArray()) {
//                if (abstractInsnNode instanceof LineNumberNode) {
//                    LineNumberNode insnNode = (LineNumberNode) abstractInsnNode;
//                    method.instructions.remove(insnNode);
//                }
//            }
//            if (method.parameters != null) {
//                for (ParameterNode parameter : method.parameters) {
//                    parameter.name = NameUtils.unicodeString((int) (Math.random() * 20 + 1));
//                }
//            }
//            if (method.localVariables != null) {
//                for (LocalVariableNode parameter : method.localVariables) {
//                    parameter.name = NameUtils.generateLocalVariableName();
//                }
//            }
//        }
//        if (node.sourceFile == null || !node.sourceFile.contains(StringEncryptionProcessor.MAGICNUMBER_START)) {
//            node.sourceFile = null;
//        }
//        inst.setWorkDone();
    }

}