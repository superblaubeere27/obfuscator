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

import me.superblaubeere27.jobf.utils.NodeUtils;
import me.superblaubeere27.jobf.utils.Utils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;

import java.util.ArrayList;
import java.util.List;

public class JumpReplacer {

    public static void process(ClassNode node, MethodNode methodNode) {
        // Labels in this list will be replaced
        List<LabelNode> labels = new ArrayList<>();
        // Those labels will be removed from the good labels list
        List<LabelNode> badLabels = new ArrayList<>();

        Frame<SourceValue>[] frames;

        try {
            frames = new Analyzer<>(new SourceInterpreter()).analyze(node.name, methodNode);
        } catch (AnalyzerException e) {
            throw new RuntimeException(e);
        }

        for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
            if (insnNode instanceof JumpInsnNode) {
                JumpInsnNode jumpInsnNode = (JumpInsnNode) insnNode;

                if (jumpInsnNode.getOpcode() == Opcodes.GOTO) {
                    labels.add(jumpInsnNode.label);
                }
            }
        }

        labels.removeAll(badLabels);

        //Collections.shuffle(labels);

        List<LabelNode> alreadyAssigned = new ArrayList<>();

        List<ReplacedLabelPair> replaced = new ArrayList<>();

        for (LabelNode label : labels) {
            if (alreadyAssigned.contains(label)) continue;

            LabelNode secondLabel = null;

            for (LabelNode labelNode : labels) {
                if (alreadyAssigned.contains(labelNode)) continue;

                secondLabel = labelNode;
                break;
            }


            if (secondLabel != null) {
                replaced.add(new ReplacedLabelPair(label, secondLabel));
                alreadyAssigned.add(label);
                alreadyAssigned.add(secondLabel);
            }
        }

        InsnList insnList = new InsnList();

        if (replaced.size() > 1) {
            Type returnType = Type.getReturnType(methodNode.desc);

            //if (returnType.getSize() != 0) insnList.add(NodeUtils.nullValueForType(returnType));
            //insnList.add(new InsnNode(returnType.getOpcode(Opcodes.IRETURN)));

            for (ReplacedLabelPair replacedLabelPair : replaced) {
                replacedLabelPair.firstNumber = Utils.random(-10, 10);

                int notFirstNumber;

                do notFirstNumber = Utils.random(-10, 10); while (notFirstNumber == replacedLabelPair.firstNumber);

                insnList.add(replacedLabelPair.replacement);
                insnList.add(new InsnNode(Opcodes.DUP));
                insnList.add(NodeUtils.generateIntPush(11));
                insnList.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, replacedLabelPair.replacement));
                insnList.add(NodeUtils.generateIntPush(replacedLabelPair.firstNumber));
                insnList.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, replacedLabelPair.first));
                insnList.add(new JumpInsnNode(Opcodes.GOTO, replacedLabelPair.second));
            }

            if (returnType.getSize() != 0) insnList.add(NodeUtils.nullValueForType(returnType));

            insnList.add(new InsnNode(returnType.getOpcode(Opcodes.IRETURN)));

            //System.out.println(NodeUtils.prettyprint(insnList));

        }


        for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
            if (insnNode instanceof JumpInsnNode) {
                JumpInsnNode jumpInsnNode = (JumpInsnNode) insnNode;

                int number = 0;
                LabelNode label = null;

                for (ReplacedLabelPair replacedLabelPair : replaced) {
                    if (replacedLabelPair.first == jumpInsnNode.label) {
                        number = replacedLabelPair.firstNumber;
                        label = replacedLabelPair.replacement;
                    } else if (replacedLabelPair.second == jumpInsnNode.label) {
                        do number = Utils.random(-10, 10); while (replacedLabelPair.firstNumber == number);

                        label = replacedLabelPair.replacement;
                    }
                }

                if (label == null) continue;

                if (insnNode.getOpcode() == Opcodes.GOTO) {
                    InsnList replacement = new InsnList();

                    replacement.add(NodeUtils.generateIntPush(number));
                    replacement.add(new JumpInsnNode(jumpInsnNode.getOpcode(), label));

                    methodNode.instructions.insert(insnNode, replacement);
                    methodNode.instructions.remove(insnNode);

                }
            }
        }

        methodNode.instructions.add(insnList);


    }

    static class ReplacedLabelPair {
        private int firstNumber;
        private LabelNode first;
        private LabelNode second;
        private LabelNode replacement;

        ReplacedLabelPair(LabelNode first, LabelNode second) {
            this.first = first;
            this.second = second;

            this.replacement = new LabelNode();
        }
    }

}
