/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27.jobf.processors;

import me.superblaubeere27.jobf.IClassProcessor;
import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.ProcessorCallback;
import me.superblaubeere27.jobf.util.values.DeprecationLevel;
import me.superblaubeere27.jobf.util.values.EnabledValue;
import me.superblaubeere27.jobf.utils.NameUtils;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Modifier;

public class CrasherProcessor implements IClassProcessor {
    private EnabledValue enabled = new EnabledValue("Crasher", DeprecationLevel.GOOD, false);
    private JObfImpl inst;

    public CrasherProcessor(JObfImpl inst) {
        this.inst = inst;
    }

    @Override
    public void process(ProcessorCallback callback, ClassNode node) {
        if (Modifier.isInterface(node.access)) return;
        if (!enabled.getObject()) return;

        /*
         * By ItzSomebody
         */
        if (node.signature == null) {
            node.signature = NameUtils.crazyString(10);
        }

//        for (MethodNode method : node.methods) {
//            if (Modifier.isAbstract(method.access) || method.instructions.size() <= 0)
//                return;

//            LabelNode tryStart,
//                    tryFinalStart, // At end must jump over catch block (goto tryCatchEnd)
//                    tryCatchStart,
//                    tryCatchEnd;
//
//            tryStart = new LabelNode();
//            tryFinalStart = new LabelNode();
//            tryCatchStart = new LabelNode();
//            tryCatchEnd = new LabelNode();
//
//            InsnList list = new InsnList();
//
//            list.add(tryStart); // Auto iterator.next()
//            list.add(method.instructions);
//            list.add(tryFinalStart);
//            {
//                method.instructions.add(
//                        new JumpInsnNode(
//                                Opcodes.GOTO,
//                                tryCatchEnd
//                        )
//                );
//            }
//            list.add(tryCatchStart);
//            list.add(
//                    new InsnNode(
//                            Opcodes.ATHROW
//                    )
//            );
//            list.add(tryCatchEnd);
//            method.tryCatchBlocks.add(new TryCatchBlockNode(tryStart, tryFinalStart, tryCatchStart, "java/lang/Exception"));
//            method.instructions = list;
//            String ex1 = exceptions.get(random.nextInt(exceptions.size()));
//            String ex2 = exceptions.get(random.nextInt(exceptions.size()));
//
//            final LabelNode labelNode0 = new LabelNode();
//            final LabelNode labelNode1 = new LabelNode();
//            final LabelNode labelNode2 = new LabelNode();
//            final LabelNode labelNode3 = new LabelNode();
//            final LabelNode labelNode4 = new LabelNode();
//            final LabelNode labelNode5 = new LabelNode();
//
//            method.tryCatchBlocks.add(new TryCatchBlockNode(labelNode1, labelNode4, labelNode0, ex1));
//            method.tryCatchBlocks.add(new TryCatchBlockNode(labelNode0, labelNode1, labelNode3, ex2));
//
//            final InsnList insnList = new InsnList();
//
//            insnList.add(new JumpInsnNode(Opcodes.GOTO, labelNode1));
//            insnList.add(labelNode0);
//            insnList.add(new FrameNode(Opcodes.F_SAME1, 0, null, 1, new Object[]{ex1}));
//            insnList.add(new InsnNode(Opcodes.ATHROW));
//            insnList.add(labelNode1);
//            insnList.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
//            insnList.add(NodeUtils.generateIntPush(random.nextInt(9) + 1));
//            insnList.add(new JumpInsnNode(Opcodes.IFGT, labelNode0));
//            insnList.add(new InsnNode(Opcodes.ACONST_NULL));
//            insnList.add(labelNode2);
//            insnList.add(new FrameNode(Opcodes.F_SAME1, 0, null, 1, new Object[]{Opcodes.NULL}));
//            insnList.add(new JumpInsnNode(Opcodes.GOTO, labelNode2));
//            insnList.add(labelNode3);
//            insnList.add(new FrameNode(Opcodes.F_SAME1, 0, null, 1, new Object[]{ex2}));
//            insnList.add(new InsnNode(Opcodes.ATHROW));
//            insnList.add(labelNode4);
//            insnList.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
//
//            insnList.add(method.instructions);
//
//            insnList.add(labelNode5);
//
//            method.instructions = insnList;
//        }

        inst.setWorkDone();
    }


}