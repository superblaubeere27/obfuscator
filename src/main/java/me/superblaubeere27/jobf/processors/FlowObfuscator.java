package me.superblaubeere27.jobf.processors;

import me.superblaubeere27.jobf.IClassProcessor;
import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.util.values.BooleanValue;
import me.superblaubeere27.jobf.util.values.DeprecationLevel;
import me.superblaubeere27.jobf.util.values.EnabledValue;
import me.superblaubeere27.jobf.utils.NameUtils;
import me.superblaubeere27.jobf.utils.NodeUtils;
import me.superblaubeere27.jobf.utils.VariableProvider;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class FlowObfuscator implements IClassProcessor {
    private static Random random = new Random();
    private JObfImpl inst;

    private static final String PROCESSOR_NAME = "FlowObfuscator";

    private EnabledValue enabled = new EnabledValue(PROCESSOR_NAME, DeprecationLevel.GOOD, true);
    private BooleanValue mangleSwitchesEnabled = new BooleanValue(PROCESSOR_NAME, "Mangle Switches", DeprecationLevel.GOOD, true);
    private BooleanValue mangleReturn = new BooleanValue(PROCESSOR_NAME, "Mangle Return", DeprecationLevel.GOOD, true);
    private BooleanValue replaceGoto = new BooleanValue(PROCESSOR_NAME, "Replace GOTO", DeprecationLevel.GOOD, true);
    private BooleanValue replaceIf = new BooleanValue(PROCESSOR_NAME, "Replace If", DeprecationLevel.GOOD, true);
    private BooleanValue badPop = new BooleanValue(PROCESSOR_NAME, "Bad POP", DeprecationLevel.GOOD, true);
    private BooleanValue badConcat = new BooleanValue(PROCESSOR_NAME, "Bad Concat", DeprecationLevel.GOOD, true);

    public FlowObfuscator(JObfImpl inst) {
        this.inst = inst;
    }


    public static InsnList generateIfGoto(int i, LabelNode label) {
        InsnList insnList = new InsnList();

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
                break;
            }
            case 6: {
                int first;

                first = random.nextInt(5) + 1;

                insnList.add(NodeUtils.generateIntPush(first));
                insnList.add(new JumpInsnNode(Opcodes.IFNE, label));
                break;
            }
            case 7: {
                int first = 0;


                insnList.add(NodeUtils.generateIntPush(first));
                insnList.add(new JumpInsnNode(Opcodes.IFEQ, label));
                break;
            }
            case 8: {
                int second;

                second = random.nextInt(5);

                insnList.add(NodeUtils.generateIntPush(second));
                insnList.add(new JumpInsnNode(Opcodes.IFGE, label));
                break;
            }
            case 9: {
                int second;

                second = random.nextInt(5) + 1;

                insnList.add(NodeUtils.generateIntPush(second));
                insnList.add(new JumpInsnNode(Opcodes.IFGT, label));
                break;
            }
            case 10: {
                int second;

                second = -random.nextInt(5);

                insnList.add(NodeUtils.generateIntPush(second));
                insnList.add(new JumpInsnNode(Opcodes.IFLE, label));
                break;
            }
            case 11: {
                int second;

                second = -random.nextInt(5) - 1;

                insnList.add(NodeUtils.generateIntPush(second));
                insnList.add(new JumpInsnNode(Opcodes.IFLT, label));
                break;
            }
            default: {
                insnList.add(new InsnNode(Opcodes.ACONST_NULL));
                insnList.add(new JumpInsnNode(Opcodes.IFNULL, label));
                break;
            }
//            case 13: {
//                insnList.add(NodeUtils.notNullPush());
//                insnList.add(new JumpInsnNode(Opcodes.IFNONNULL, label));
//                break;
//            }
        }
        return insnList;
    }

    @Override
    public void process(ClassNode node) {
        if (!enabled.getObject()) return;

        HashMap<Integer, MethodNode> jumpMethodMap = new HashMap<>();

        for (MethodNode method : node.methods) {
            mangleSwitches(method);
            mangleReturn(method);
            mangleNew(method);

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
                if (abstractInsnNode instanceof MethodInsnNode && badConcat.getObject()) {
                    MethodInsnNode insnNode = (MethodInsnNode) abstractInsnNode;

                    if (insnNode.owner.equals("java/lang/StringBuilder") && insnNode.name.equals("toString")) {
                        method.instructions.insert(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;", false));
                        method.instructions.remove(insnNode);
                    }
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

    private void mangleReturn(MethodNode node) {
        if (!mangleReturn.getObject() || Modifier.isAbstract(node.access) || Modifier.isNative(node.access)) return;

        VariableProvider variableProvider = new VariableProvider(node);

        LabelNode returnLabel = new LabelNode();
        Type returnType = Type.getReturnType(node.desc);
        boolean isVoidType = returnType.getSort() == Type.VOID;
        int returnSlot = -1;

        if (!isVoidType) {
            returnSlot = variableProvider.allocateVar();
        }

        for (AbstractInsnNode abstractInsnNode : node.instructions.toArray()) {
            if (abstractInsnNode.getOpcode() >= Opcodes.IRETURN && abstractInsnNode.getOpcode() <= Opcodes.RETURN) {
                InsnList insnList = new InsnList();

                if (!isVoidType) {
                    insnList.add(new VarInsnNode(returnType.getOpcode(Opcodes.ISTORE), returnSlot));
                }

                insnList.add(new JumpInsnNode(Opcodes.GOTO, returnLabel));

                node.instructions.insert(abstractInsnNode, insnList);
                node.instructions.remove(abstractInsnNode);
            }
        }

        if (isVoidType) {
            node.instructions.add(returnLabel);
            node.instructions.add(new InsnNode(Opcodes.RETURN));
        } else {
            node.instructions.add(returnLabel);
            node.instructions.add(new VarInsnNode(returnType.getOpcode(Opcodes.ILOAD), returnSlot));
            node.instructions.add(new InsnNode((returnType.getOpcode(Opcodes.IRETURN))));
        }
    }

    private void mangleSwitches(MethodNode node) {
        if (!mangleSwitchesEnabled.getObject() || Modifier.isAbstract(node.access) || Modifier.isNative(node.access))
            return;

        VariableProvider provider = new VariableProvider(node);
        int resultSlot = provider.allocateVar();

        for (AbstractInsnNode abstractInsnNode : node.instructions.toArray()) {
            if (abstractInsnNode instanceof TableSwitchInsnNode) {
                TableSwitchInsnNode switchInsnNode = (TableSwitchInsnNode) abstractInsnNode;

                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(Opcodes.ISTORE, resultSlot));

                int j = 0;

                for (int i = switchInsnNode.min; i <= switchInsnNode.max; i++) {
                    insnList.add(new VarInsnNode(Opcodes.ILOAD, resultSlot));
                    insnList.add(NumberObfuscationProcessor.getInstructions(i));
                    insnList.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, switchInsnNode.labels.get(j)));

                    j++;
                }
                insnList.add(new JumpInsnNode(Opcodes.GOTO, switchInsnNode.dflt));


                node.instructions.insert(abstractInsnNode, insnList);
                node.instructions.remove(abstractInsnNode);
            }
            if (abstractInsnNode instanceof LookupSwitchInsnNode) {
                LookupSwitchInsnNode switchInsnNode = (LookupSwitchInsnNode) abstractInsnNode;

                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(Opcodes.ISTORE, resultSlot));

                List<Integer> keys = switchInsnNode.keys;
                for (int i = 0; i < keys.size(); i++) {
                    Integer key = keys.get(i);
                    insnList.add(new VarInsnNode(Opcodes.ILOAD, resultSlot));
                    insnList.add(NumberObfuscationProcessor.getInstructions(key));
                    insnList.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, switchInsnNode.labels.get(i)));

                }

                insnList.add(new JumpInsnNode(Opcodes.GOTO, switchInsnNode.dflt));


                node.instructions.insert(abstractInsnNode, insnList);
                node.instructions.remove(abstractInsnNode);
            }
        }
//        System.out.println(NodeUtils.prettyprint(node));
    }

    private static InsnList ifGoto(LabelNode label) {
        InsnList insnList;

        int i = random.nextInt(14);

        insnList = generateIfGoto(i, label);
        insnList.add(new InsnNode(Opcodes.ACONST_NULL));
        insnList.add(new InsnNode(Opcodes.ATHROW));

        for (int j = 0; j < random.nextInt(2) + 1; j++) {
            insnList = NumberObfuscationProcessor.obfuscateInsnList(insnList);
        }

        return insnList;
    }

    private void mangleNew(MethodNode node) {
//        for (AbstractInsnNode abstractInsnNode : node.instructions.toArray()) {
//            AbstractInsnNode next = Utils.getNext(abstractInsnNode);
//
//            if (abstractInsnNode instanceof TypeInsnNode && abstractInsnNode.getOpcode() == Opcodes.NEW && next != null && next.getOpcode() == Opcodes.DUP) {
//                InsnList afterNew = new InsnList();
//                afterNew.add(new InsnNode(Opcodes.DUP));
//                afterNew.add(new LdcInsnNode(NameUtils.generateSpaceString(Utils.random(1, 3))));
//                afterNew.add(new InsnNode(Opcodes.DUP_X2));
//
//                InsnList after = new InsnList();
//                after.add(new InsnNode(Opcodes.SWAP));
//                after.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C", false));
//                after.add(new InsnNode(Opcodes.POP));
//
//                node.instructions.insert(abstractInsnNode, afterNew);
//                node.instructions.insert(next, afterNew);
//                node.instructions.insert(next, new InsnNode(Opcodes.POP));
//                node.instructions.remove(next);
//            }
//        }

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
}