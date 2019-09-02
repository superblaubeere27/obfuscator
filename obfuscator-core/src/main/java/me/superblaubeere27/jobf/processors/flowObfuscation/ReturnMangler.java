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

import me.superblaubeere27.jobf.ProcessorCallback;
import me.superblaubeere27.jobf.utils.VariableProvider;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;

class ReturnMangler {
    static void mangleReturn(ProcessorCallback callback, MethodNode node) {
        if (Modifier.isAbstract(node.access) || Modifier.isNative(node.access)) return;

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

        callback.setForceComputeFrames();
    }
}
