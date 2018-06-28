package me.superblaubeere27.jobf.processors;

import me.superblaubeere27.jobf.IClassProcessor;
import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.utils.NodeUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Random;

public class FlowObfuscator implements IClassProcessor {
    private static Random random = new Random();
    private JObfImpl inst;

    public FlowObfuscator(JObfImpl inst) {
        this.inst = inst;
    }

    private static InsnList ifGoto(LabelNode label) {
        final InsnList insnList = new InsnList();

        int i = random.nextInt(5);

        switch (i) {
            case 0: {
                int first;
                int second;

                do {
                    first = random.nextInt(6) - 1;
                    second = random.nextInt(6) - 1;
                } while (second == first);

                insnList.add(NodeUtils.generateIntPush(first));
                insnList.add(NodeUtils.generateIntPush(second));
                insnList.add(new JumpInsnNode(Opcodes.IF_ICMPNE, label));
                insnList.add(new InsnNode(Opcodes.ACONST_NULL));
                insnList.add(new InsnNode(Opcodes.ATHROW));
                break;
            }
            case 1: {
                int first;
                int second;

                do {
                    first = random.nextInt(6) - 1;
                    second = random.nextInt(6) - 1;
                } while (second != first);

                insnList.add(NodeUtils.generateIntPush(first));
                insnList.add(NodeUtils.generateIntPush(second));
                insnList.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, label));
                insnList.add(new InsnNode(Opcodes.ACONST_NULL));
                insnList.add(new InsnNode(Opcodes.ATHROW));
                break;
            }
            case 2: {
                int first;
                int second;

                do {
                    first = random.nextInt(6) - 1;
                    second = random.nextInt(6) - 1;
                } while (first >= second);

                insnList.add(NodeUtils.generateIntPush(first));
                insnList.add(NodeUtils.generateIntPush(second));
                insnList.add(new JumpInsnNode(Opcodes.IF_ICMPLT, label));
                insnList.add(new InsnNode(Opcodes.ACONST_NULL));
                insnList.add(new InsnNode(Opcodes.ATHROW));
                break;
            }
            case 3: {
                int first;
                int second;

                do {
                    first = random.nextInt(6) - 1;
                    second = random.nextInt(6) - 1;
                } while (first < second);

                insnList.add(NodeUtils.generateIntPush(first));
                insnList.add(NodeUtils.generateIntPush(second));
                insnList.add(new JumpInsnNode(Opcodes.IF_ICMPGE, label));
                insnList.add(new InsnNode(Opcodes.ACONST_NULL));
                insnList.add(new InsnNode(Opcodes.ATHROW));
                break;
            }
            case 4: {
                int first;
                int second;

                do {
                    first = random.nextInt(6) - 1;
                    second = random.nextInt(6) - 1;
                } while (first <= second);

                insnList.add(NodeUtils.generateIntPush(first));
                insnList.add(NodeUtils.generateIntPush(second));
                insnList.add(new JumpInsnNode(Opcodes.IF_ICMPGT, label));
                insnList.add(new InsnNode(Opcodes.ACONST_NULL));
                insnList.add(new InsnNode(Opcodes.ATHROW));
                break;
            }
            case 5: {
                int first;
                int second;

                do {
                    first = random.nextInt(6) - 1;
                    second = random.nextInt(6) - 1;
                } while (first > second);

                insnList.add(NodeUtils.generateIntPush(first));
                insnList.add(NodeUtils.generateIntPush(second));
                insnList.add(new JumpInsnNode(Opcodes.IF_ICMPLE, label));
                insnList.add(new InsnNode(Opcodes.ACONST_NULL));
                insnList.add(new InsnNode(Opcodes.ATHROW));
                break;
            }
        }
        return insnList;
    }

    @Override
    public void process(ClassNode node, int mode) {
        for (MethodNode method : node.methods) {
            LabelNode label = new LabelNode();
            LabelNode label1 = new LabelNode();

//            if (method.instructions.getFirst() != null) {
//                method.instructions.insertBefore(method.instructions.getFirst(), label);
//                method.instructions.insertBefore(method.instructions.getFirst(), new JumpInsnNode(Opcodes.GOTO, label1));
//                method.instructions.insertBefore(method.instructions.getFirst(), new InsnNode(Opcodes.ATHROW));
//                method.instructions.insertBefore(method.instructions.getFirst(), new InsnNode(Opcodes.ACONST_NULL));
//                method.instructions.insertBefore(method.instructions.getFirst(), new JumpInsnNode(Opcodes.GOTO, label));
//                method.instructions.insertBefore(method.instructions.getFirst(), label1);
//            }

            for (AbstractInsnNode abstractInsnNode : method.instructions.toArray()) {
                if (abstractInsnNode instanceof JumpInsnNode && abstractInsnNode.getOpcode() == Opcodes.GOTO) {
                    method.instructions.insertBefore(abstractInsnNode, new LdcInsnNode(""));
                    method.instructions.insertBefore(abstractInsnNode, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I", false));
                    method.instructions.insertBefore(abstractInsnNode, new InsnNode(Opcodes.POP));
                }
                if (abstractInsnNode instanceof JumpInsnNode && abstractInsnNode.getOpcode() == Opcodes.GOTO) {
                    JumpInsnNode insnNode = (JumpInsnNode) abstractInsnNode;
                    final InsnList insnList = new InsnList();
                    insnList.add(ifGoto(insnNode.label));
                    method.instructions.insert(insnNode, insnList);
                    method.instructions.remove(insnNode);
                }

//                if (abstractInsnNode instanceof JumpInsnNode && abstractInsnNode.getOpcode() != Opcodes.GOTO) {
//                    JumpInsnNode insnNode = (JumpInsnNode) abstractInsnNode;
//
//                    int invertedJmp = NodeUtils.getInvertedJump(insnNode.getOpcode());
//
//                    if (invertedJmp != -1) {
//                        LabelNode invertedLabel = new LabelNode();
//                        method.instructions.insert(insnNode, invertedLabel);
//                        method.instructions.insert(insnNode, new JumpInsnNode(Opcodes.GOTO, insnNode.label));
//                        method.instructions.insert(insnNode, new JumpInsnNode(invertedJmp, invertedLabel));
//                        method.instructions.remove(insnNode);
//                    }
//                }
//                if (abstractInsnNode instanceof MethodInsnNode || abstractInsnNode instanceof FieldInsnNode) {
//                    method.instructions.insertBefore(abstractInsnNode, new LdcInsnNode(""));
//                    method.instructions.insertBefore(abstractInsnNode, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I", false));
//                    method.instructions.insertBefore(abstractInsnNode, new InsnNode(Opcodes.POP));
//                }
            }
//            method.desc = method.desc.replace('Z', 'I');
        }
        inst.setWorkDone();
    }
}