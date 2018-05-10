package me.superblaubeere27.jobf.processors;

import me.superblaubeere27.jobf.IClassProcessor;
import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.utils.NameUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ReferenceProxy implements IClassProcessor {
    private static Random random = new Random();
    private JObfImpl inst;

    public ReferenceProxy(JObfImpl inst) {
        this.inst = inst;
    }

    @Override
    public void process(ClassNode node, int mode) {
        HashMap<String, MethodNode> nodes = new HashMap<>();
        for (MethodNode method : node.methods) {
            for (AbstractInsnNode abstractInsnNode : method.instructions.toArray()) {
                if (abstractInsnNode instanceof MethodInsnNode) {
                    MethodInsnNode insnNode = (MethodInsnNode) abstractInsnNode;

                    if (insnNode.owner.equals("java/lang/System") || insnNode.owner.equals("java/lang/Math")) {
                        if (insnNode.getOpcode() == Opcodes.INVOKESTATIC && (insnNode.desc.equals("()I") || insnNode.desc.equals("()V"))) {
                            String uid = insnNode.owner + insnNode.name + insnNode.desc;
                            if (!nodes.containsKey(uid)) {
                                MethodNode newMethod = new MethodNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, NameUtils.generateMethodName(node, insnNode.desc), insnNode.desc, "", new String[0]);
                                newMethod.instructions = new InsnList();
                                newMethod.instructions.add(abstractInsnNode.clone(null));
                                if (insnNode.desc.endsWith("I")) {
                                    newMethod.instructions.add(new InsnNode(Opcodes.IRETURN));
                                } else {
                                    newMethod.instructions.add(new InsnNode(Opcodes.RETURN));
                                }
                                nodes.put(uid, newMethod);
                            }
                            MethodNode mn = nodes.get(uid);
                            method.instructions.insert(abstractInsnNode, new MethodInsnNode(Opcodes.INVOKESTATIC, node.name, mn.name, mn.desc, false));
                            method.instructions.remove(abstractInsnNode);
                        }
                    }
                }
            }
        }
        for (Map.Entry<String, MethodNode> stringMethodNodeEntry : nodes.entrySet()) {
            node.methods.add(stringMethodNodeEntry.getValue());
        }
        inst.setWorkDone();
    }

}