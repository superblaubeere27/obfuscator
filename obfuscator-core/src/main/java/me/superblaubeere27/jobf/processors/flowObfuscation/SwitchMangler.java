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

import me.superblaubeere27.jobf.processors.NumberObfuscationTransformer;
import me.superblaubeere27.jobf.utils.VariableProvider;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.List;

class SwitchMangler {

    static void mangleSwitches(MethodNode node) {
        if (Modifier.isAbstract(node.access) || Modifier.isNative(node.access))
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
                    insnList.add(NumberObfuscationTransformer.getInstructions(i));
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
                    insnList.add(NumberObfuscationTransformer.getInstructions(key));
                    insnList.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, switchInsnNode.labels.get(i)));

                }

                insnList.add(new JumpInsnNode(Opcodes.GOTO, switchInsnNode.dflt));


                node.instructions.insert(abstractInsnNode, insnList);
                node.instructions.remove(abstractInsnNode);
            }
        }
    }

}
