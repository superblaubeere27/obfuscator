package me.superblaubeere27.jobf.processors;

import me.superblaubeere27.jobf.IClassProcessor;
import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.util.values.DeprecationLevel;
import me.superblaubeere27.jobf.util.values.EnabledValue;
import me.superblaubeere27.jobf.utils.NameUtils;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CrasherProcessor implements IClassProcessor {
    private static Random random = new Random();
    private static List<String> exceptions = new ArrayList<>();

    private EnabledValue enabled = new EnabledValue("Crasher", DeprecationLevel.GOOD, true);

    static {
        exceptions.add("java/lang/Throwable");
        exceptions.add("java/lang/RuntimeException");
        exceptions.add("java/lang/Exception");
        exceptions.add("java/lang/Error");
        exceptions.add("java/lang/CloneNotSupportedException");
        exceptions.add("java/lang/CloneNotSupportedException");
        exceptions.add("java/lang/InterruptedException");
        exceptions.add("java/lang/ThreadDeath");
    }

    private JObfImpl inst;

    public CrasherProcessor(JObfImpl inst) {
        this.inst = inst;
    }

    @Override
    public void process(ClassNode node) {
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