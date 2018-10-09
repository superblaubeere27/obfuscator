package me.superblaubeere27.jobf.utils;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.lang.reflect.Modifier;

public class VariableProvider {
    private int max = 0;

    private VariableProvider() {

    }

    public VariableProvider(MethodNode method) {
        this();

        if (!Modifier.isStatic(method.access)) registerExisting(0);

        registerExisting(Type.getArgumentTypes(method.desc).length - 1 + max);

        for (AbstractInsnNode abstractInsnNode : method.instructions.toArray()) {
            if (abstractInsnNode instanceof VarInsnNode) {
                registerExisting(((VarInsnNode) abstractInsnNode).var);
            }
        }
    }

    private void registerExisting(int var) {
        if (var >= max) max = var + 1;
    }

    public int allocateVar() {
        return max++;
    }

}
