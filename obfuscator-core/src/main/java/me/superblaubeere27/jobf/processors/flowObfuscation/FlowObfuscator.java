/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27.jobf.processors.flowObfuscation;

import me.superblaubeere27.annotations.ObfuscationTransformer;
import me.superblaubeere27.jobf.IClassTransformer;
import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.ProcessorCallback;
import me.superblaubeere27.jobf.processors.NumberObfuscationTransformer;
import me.superblaubeere27.jobf.utils.NameUtils;
import me.superblaubeere27.jobf.utils.NodeUtils;
import me.superblaubeere27.jobf.utils.values.BooleanValue;
import me.superblaubeere27.jobf.utils.values.DeprecationLevel;
import me.superblaubeere27.jobf.utils.values.EnabledValue;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static me.superblaubeere27.jobf.processors.flowObfuscation.LocalVariableMangler.mangleLocalVariables;
import static me.superblaubeere27.jobf.processors.flowObfuscation.ReturnMangler.mangleReturn;
import static me.superblaubeere27.jobf.processors.flowObfuscation.SwitchMangler.mangleSwitches;

public class FlowObfuscator implements IClassTransformer {
    private static final String PROCESSOR_NAME = "FlowObfuscator";
    private static Random random = new Random();
    private JObfImpl inst;
    private EnabledValue enabled = new EnabledValue(PROCESSOR_NAME, DeprecationLevel.GOOD, true);
    private BooleanValue mangleComparisions = new BooleanValue(PROCESSOR_NAME, "Mangle Comparisons", "Replaces long, float and double comparisons with method calls", DeprecationLevel.GOOD, true);
    private BooleanValue replaceGoto = new BooleanValue(PROCESSOR_NAME, "Replace GOTO", "Replaces unconditional jumps with conditionals", DeprecationLevel.GOOD, true);
    private BooleanValue replaceIf = new BooleanValue(PROCESSOR_NAME, "Replace If", "Replaces comparisions with method calls", DeprecationLevel.GOOD, true);
    private BooleanValue badPop = new BooleanValue(PROCESSOR_NAME, "Bad POP", DeprecationLevel.GOOD, true);
    private BooleanValue badConcat = new BooleanValue(PROCESSOR_NAME, "Bad Concat", "Breaks string concatenations", DeprecationLevel.GOOD, true);
    private BooleanValue mangleSwitchesEnabled = new BooleanValue(PROCESSOR_NAME, "Mangle Switches", "Replaces switch statements with if-else statements", DeprecationLevel.OK, false);
    private BooleanValue mangleReturn = new BooleanValue(PROCESSOR_NAME, "Mangle Return", "!! Needs COMPUTE_FRAMES (See documentation) !!", DeprecationLevel.BAD, false);
    private BooleanValue mangleLocals = new BooleanValue(PROCESSOR_NAME, "Mangle Local Variables", "!! Needs COMPUTE_FRAMES (See documentation) !!", DeprecationLevel.BAD, false);

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

    private static InsnList ifGoto(LabelNode label, MethodNode methodNode, Type returnType) {
        InsnList insnList;

        int i = random.nextInt(14);

        insnList = generateIfGoto(i, label);

        if (methodNode.name.equals("<init>")) {
            insnList.add(new InsnNode(Opcodes.ACONST_NULL));
            insnList.add(new InsnNode(Opcodes.ATHROW));
        } else {
            if (returnType.getSize() != 0) insnList.add(NodeUtils.nullValueForType(returnType));
            insnList.add(new InsnNode(returnType.getOpcode(Opcodes.IRETURN)));
        }

        for (int j = 0; j < random.nextInt(2) + 1; j++)
            insnList = NumberObfuscationTransformer.obfuscateInsnList(insnList);

        return insnList;
    }

    private static MethodNode ifWrapper(int opcode) {
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
    public void process(ProcessorCallback callback, ClassNode node) {
        if (!enabled.getObject()) return;

        HashMap<Integer, MethodNode> jumpMethodMap = new HashMap<>();

        List<MethodNode> toAdd = new ArrayList<>();

        for (MethodNode method : node.methods) {
            if (mangleLocals.getObject()) mangleLocalVariables(callback, node, method);
            if (mangleReturn.getObject()) mangleReturn(callback, method);
            if (mangleSwitchesEnabled.getObject()) mangleSwitches(method);
            if (mangleComparisions.getObject())
                toAdd.addAll(FloatingPointComparisionMangler.mangleComparisions(node, method));
            //JumpReplacer.process(node, method);


            for (AbstractInsnNode abstractInsnNode : method.instructions.toArray()) {
                if (badPop.getObject() && abstractInsnNode instanceof JumpInsnNode && abstractInsnNode.getOpcode() == Opcodes.GOTO) {
                    method.instructions.insertBefore(abstractInsnNode, new LdcInsnNode(""));
                    method.instructions.insertBefore(abstractInsnNode, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I", false));
                    method.instructions.insertBefore(abstractInsnNode, new InsnNode(Opcodes.POP));
                }
                if (badPop.getObject() && abstractInsnNode.getOpcode() == Opcodes.POP) {
                    method.instructions.insertBefore(abstractInsnNode, new LdcInsnNode(""));
                    method.instructions.insertBefore(abstractInsnNode, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I", false));
                    method.instructions.insert(abstractInsnNode, new InsnNode(Opcodes.POP2));
                    method.instructions.remove(abstractInsnNode);
                }
                if (replaceGoto.getObject() && abstractInsnNode instanceof JumpInsnNode && abstractInsnNode.getOpcode() == Opcodes.GOTO) {
                    JumpInsnNode insnNode = (JumpInsnNode) abstractInsnNode;
                    final InsnList insnList = new InsnList();
                    insnList.add(ifGoto(insnNode.label, method, Type.getReturnType(method.desc)));
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
                if (replaceIf.getObject() && abstractInsnNode instanceof JumpInsnNode && (abstractInsnNode.getOpcode() >= Opcodes.IFEQ && abstractInsnNode.getOpcode() <= Opcodes.IF_ACMPNE || abstractInsnNode.getOpcode() >= Opcodes.IFNULL && abstractInsnNode.getOpcode() <= Opcodes.IFNONNULL)) {
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
        node.methods.addAll(toAdd);

        inst.setWorkDone();
    }

    @Override
    public ObfuscationTransformer getType() {
        return ObfuscationTransformer.FLOW_OBFUSCATION;
    }
}