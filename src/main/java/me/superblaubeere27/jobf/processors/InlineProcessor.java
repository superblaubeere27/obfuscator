package me.superblaubeere27.jobf.processors;

import me.superblaubeere27.jobf.IClassProcessor;
import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.util.values.DeprecationLevel;
import me.superblaubeere27.jobf.util.values.EnabledValue;
import me.superblaubeere27.jobf.utils.InliningUtils;
import me.superblaubeere27.jobf.utils.Utils;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InlineProcessor implements IClassProcessor {
    private static Random random = new Random();
    private static List<String> exceptions = new ArrayList<>();

    private EnabledValue enabled = new EnabledValue("Inlining", DeprecationLevel.BAD, false);

    private JObfImpl inst;

    public InlineProcessor(JObfImpl inst) {
        this.inst = inst;
    }

    @Override
    public void process(ClassNode node) {
        if (!enabled.getObject()) return;

        int maxPasses = 3;

        boolean ok;
        int index = 0;

        do {
            ok = false;
            for (MethodNode method : node.methods) {
                for (AbstractInsnNode abstractInsnNode : method.instructions.toArray()) {
                    if (abstractInsnNode instanceof MethodInsnNode) {
                        MethodInsnNode insnNode = (MethodInsnNode) abstractInsnNode;

                        ClassNode lookupClass = Utils.lookupClass(insnNode.owner);

                        if (lookupClass == null) continue;

                        MethodNode lookupMethod = Utils.getMethod(lookupClass, insnNode.name, insnNode.desc);

                        if (lookupMethod == null || lookupMethod.instructions.size() > 100 || !InliningUtils.canInlineMethod(node, lookupClass, lookupMethod))
                            continue;

                        InsnList inline = InliningUtils.inline(lookupMethod, lookupClass, method);
//                        inline.insertBefore(inline.getFirst(), NodeUtils.debugString("--- INLINE (" + lookupClass.name + "." + lookupMethod.name + lookupMethod.desc + ") ---"));
//                        inline.add(NodeUtils.debugString("--- END ---"));

//                    System.out.println(NodeUtils.prettyprint(inline));

                        method.instructions.insert(abstractInsnNode, inline);
                        method.instructions.remove(abstractInsnNode);

                        System.out.println("Inlined method in " + node.name + "." + method.name + method.desc + "(" + lookupClass.name + "." + lookupMethod.name + lookupMethod.desc + ")");

                        ok = true;
                    }
                }
            }
            index++;
        } while (ok && index <= maxPasses);

//        System.out.println("Inlined " + inlined + " methods.");

        inst.setWorkDone();
    }


}