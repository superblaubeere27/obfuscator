package me.superblaubeere27.jobf.processors;

import me.superblaubeere27.jobf.IClassProcessor;
import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.util.values.BooleanValue;
import me.superblaubeere27.jobf.util.values.DeprecationLevel;
import me.superblaubeere27.jobf.util.values.EnabledValue;
import me.superblaubeere27.jobf.utils.NameUtils;
import me.superblaubeere27.jobf.utils.NodeUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Random;

public class FlowObfuscator implements IClassProcessor {
    private static Random random = new Random();
    private JObfImpl inst;

    private static final String PROCESSOR_NAME = "FlowObfuscator";

    private EnabledValue enabled = new EnabledValue(PROCESSOR_NAME, DeprecationLevel.GOOD, true);
    private BooleanValue replaceGoto = new BooleanValue(PROCESSOR_NAME, "Replace GOTO", DeprecationLevel.GOOD, true);
    private BooleanValue replaceIf = new BooleanValue(PROCESSOR_NAME, "Replace If", DeprecationLevel.GOOD, true);
    private BooleanValue badPop = new BooleanValue(PROCESSOR_NAME, "Bad POP", DeprecationLevel.GOOD, true);

    public FlowObfuscator(JObfImpl inst) {
        this.inst = inst;
    }

    private static InsnList ifGoto(LabelNode label) {
        InsnList insnList = new InsnList();

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
            case 7: {
                int first;

                first = random.nextInt(5) + 1;

                insnList.add(NodeUtils.generateIntPush(first));
                insnList.add(new JumpInsnNode(Opcodes.IFNE, label));
                insnList.add(new InsnNode(Opcodes.ACONST_NULL));
                insnList.add(new InsnNode(Opcodes.ATHROW));
                break;
            }
            case 8: {
                int first = 0;


                insnList.add(NodeUtils.generateIntPush(first));
                insnList.add(new JumpInsnNode(Opcodes.IFEQ, label));
                insnList.add(new InsnNode(Opcodes.ACONST_NULL));
                insnList.add(new InsnNode(Opcodes.ATHROW));
                break;
            }
            case 9: {
                int first = 0;


                insnList.add(NodeUtils.generateIntPush(first));
                insnList.add(new JumpInsnNode(Opcodes.IFEQ, label));
                insnList.add(new InsnNode(Opcodes.ACONST_NULL));
                insnList.add(new InsnNode(Opcodes.ATHROW));
                break;
            }
        }
        for (int j = 0; j < random.nextInt(2) + 1; j++) {
            insnList = NumberObfuscationProcessor.obfuscateInsnList(insnList);
        }
        return insnList;
    }

    public static MethodNode ifWrapper(int opcode) {
        if (opcode >= Opcodes.IFEQ && opcode <= Opcodes.IFLE) {
            MethodNode method = new MethodNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, "", "(I)Z", null, new String[0]);
            LabelNode label1 = new LabelNode();
            LabelNode label2 = new LabelNode();
            method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 0));
            method.instructions.add(new JumpInsnNode(opcode, label1));
            method.instructions.add(new InsnNode(Opcodes.ICONST_1));
            method.instructions.add(new JumpInsnNode(Opcodes.GOTO, label2));
            method.instructions.add(label1);
            method.instructions.add(new FrameNode(Opcodes.F_NEW, 1, new Object[]{Opcodes.INTEGER}, 0, new Object[]{}));
            method.instructions.add(new InsnNode(Opcodes.ICONST_0));
            method.instructions.add(label2);
            method.instructions.add(new FrameNode(Opcodes.F_NEW, 1, new Object[]{Opcodes.INTEGER}, 1, new Object[]{Opcodes.INTEGER}));
            method.instructions.add(new InsnNode(Opcodes.IRETURN));
            return method;
        }
        if (opcode >= Opcodes.IFNULL && opcode <= Opcodes.IFNONNULL) {
            MethodNode method = new MethodNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, "", "(Ljava/lang/Object;)Z", null, new String[0]);
            LabelNode label1 = new LabelNode();
            LabelNode label2 = new LabelNode();
            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            method.instructions.add(new JumpInsnNode(opcode, label1));
            method.instructions.add(new InsnNode(Opcodes.ICONST_1));
            method.instructions.add(new JumpInsnNode(Opcodes.GOTO, label2));
            method.instructions.add(label1);
            method.instructions.add(new FrameNode(Opcodes.F_NEW, 1, new Object[]{"java/lang/Object"}, 0, new Object[]{}));
            method.instructions.add(new InsnNode(Opcodes.ICONST_0));
            method.instructions.add(label2);
            method.instructions.add(new FrameNode(Opcodes.F_NEW, 1, new Object[]{"java/lang/Object"}, 1, new Object[]{Opcodes.INTEGER}));
            method.instructions.add(new InsnNode(Opcodes.IRETURN));
            return method;
        }
        if (opcode >= Opcodes.IF_ACMPEQ && opcode <= Opcodes.IF_ACMPNE) {
            MethodNode method = new MethodNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, "", "(Ljava/lang/Object;Ljava/lang/Object;)Z", null, new String[0]);
            LabelNode label1 = new LabelNode();
            LabelNode label2 = new LabelNode();
            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
            method.instructions.add(new JumpInsnNode(opcode, label1));
            method.instructions.add(new InsnNode(Opcodes.ICONST_1));
            method.instructions.add(new JumpInsnNode(Opcodes.GOTO, label2));
            method.instructions.add(label1);
            method.instructions.add(new FrameNode(Opcodes.F_NEW, 2, new Object[]{"java/lang/Object", "java/lang/Object"}, 0, new Object[]{}));
            method.instructions.add(new InsnNode(Opcodes.ICONST_0));
            method.instructions.add(label2);
            method.instructions.add(new FrameNode(Opcodes.F_NEW, 2, new Object[]{"java/lang/Object", "java/lang/Object"}, 1, new Object[]{Opcodes.INTEGER}));
            method.instructions.add(new InsnNode(Opcodes.IRETURN));
            return method;
        }
        if (opcode >= Opcodes.IF_ICMPEQ && opcode <= Opcodes.IF_ICMPLE) {
            MethodNode method = new MethodNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, "", "(II)Z", null, new String[0]);
            LabelNode label1 = new LabelNode();
            LabelNode label2 = new LabelNode();
            method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 0));
            method.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
            method.instructions.add(new JumpInsnNode(opcode, label1));
            method.instructions.add(new InsnNode(Opcodes.ICONST_1));
            method.instructions.add(new JumpInsnNode(Opcodes.GOTO, label2));
            method.instructions.add(label1);
            method.instructions.add(new FrameNode(Opcodes.F_NEW, 2, new Object[]{Opcodes.INTEGER, Opcodes.INTEGER}, 0, new Object[]{}));
            method.instructions.add(new InsnNode(Opcodes.ICONST_0));
            method.instructions.add(label2);
            method.instructions.add(new FrameNode(Opcodes.F_NEW, 2, new Object[]{Opcodes.INTEGER, Opcodes.INTEGER}, 1, new Object[]{Opcodes.INTEGER}));
            method.instructions.add(new InsnNode(Opcodes.IRETURN));
            return method;
        }
        return null;
    }

    @Override
    public void process(ClassNode node) {
        if (!enabled.getObject()) return;

        HashMap<Integer, MethodNode> jumpMethodMap = new HashMap<>();

        for (MethodNode method : node.methods) {
            for (AbstractInsnNode abstractInsnNode : method.instructions.toArray()) {
                if (badPop.getObject() && abstractInsnNode instanceof JumpInsnNode && abstractInsnNode.getOpcode() == Opcodes.GOTO) {
                    method.instructions.insertBefore(abstractInsnNode, new LdcInsnNode(""));
                    method.instructions.insertBefore(abstractInsnNode, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I", false));
                    method.instructions.insertBefore(abstractInsnNode, new InsnNode(Opcodes.POP));
                }
                if (replaceIf.getObject() && abstractInsnNode instanceof JumpInsnNode && abstractInsnNode.getOpcode() == Opcodes.GOTO) {
                    JumpInsnNode insnNode = (JumpInsnNode) abstractInsnNode;
                    final InsnList insnList = new InsnList();
                    insnList.add(ifGoto(insnNode.label));
                    method.instructions.insert(insnNode, insnList);
                    method.instructions.remove(insnNode);
                }
                if (replaceGoto.getObject() && abstractInsnNode instanceof JumpInsnNode && (abstractInsnNode.getOpcode() >= Opcodes.IFEQ && abstractInsnNode.getOpcode() <= Opcodes.IF_ACMPNE || abstractInsnNode.getOpcode() >= Opcodes.IFNULL && abstractInsnNode.getOpcode() <= Opcodes.IFNONNULL)) {
                    JumpInsnNode insnNode = (JumpInsnNode) abstractInsnNode;

                    MethodNode wrapper = jumpMethodMap.get(insnNode.getOpcode());

                    if (wrapper == null) {
                        wrapper = ifWrapper(insnNode.getOpcode());

                        if (wrapper != null) {
                            wrapper.name = NameUtils.generateMethodName(node, wrapper.desc);
                            jumpMethodMap.put(insnNode.getOpcode(), wrapper);
                        }
                    }

                    if (wrapper != null) {
                        final InsnList insnList = new InsnList();
                        insnList.add(NodeUtils.methodCall(node, wrapper));
                        insnList.add(new JumpInsnNode(Opcodes.IFEQ, insnNode.label));
                        method.instructions.insert(insnNode, insnList);
                        method.instructions.remove(insnNode);
                    }
                }
//                if (abstractInsnNode instanceof MethodInsnNode || abstractInsnNode instanceof FieldInsnNode) {
//                    method.instructions.insertBefore(abstractInsnNode, new LdcInsnNode(""));
//                    method.instructions.insertBefore(abstractInsnNode, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I", false));
//                    method.instructions.insertBefore(abstractInsnNode, new InsnNode(Opcodes.POP));
//                }
            }
//            method.desc = method.desc.replace('Z', 'I');
        }

        node.methods.addAll(jumpMethodMap.values());

        inst.setWorkDone();
    }
}