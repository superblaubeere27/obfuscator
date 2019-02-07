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

import me.superblaubeere27.jobf.utils.NameUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Collection;
import java.util.HashMap;

class FloatingPointComparisionMangler {

    static Collection<MethodNode> mangleComparisions(ClassNode cn, MethodNode node) {
        HashMap<Integer, MethodNode> comparisionMethodMap = new HashMap<>();

        for (AbstractInsnNode insnNode : node.instructions.toArray()) {
            if (insnNode.getOpcode() >= Opcodes.LCMP && insnNode.getOpcode() <= Opcodes.DCMPG) {
                if (!comparisionMethodMap.containsKey(insnNode.getOpcode())) {
                    comparisionMethodMap.put(insnNode.getOpcode(), generateComparisionMethod(cn, insnNode.getOpcode()));
                }

                MethodNode comparisionMethod = comparisionMethodMap.get(insnNode.getOpcode());

                // Invokes the comparision method instead of the comparision opcode
                // e.g: invokestatic    Test.compare:(DD)I
                node.instructions.insert(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC, cn.name, comparisionMethod.name, comparisionMethod.desc, false));
                node.instructions.remove(insnNode);
            }
        }

        return comparisionMethodMap.values();

    }

    /**
     * Generates a method that looks like this:
     * <p>
     * private static int compare(double, double);
     * Flags: PRIVATE, STATIC
     * Code:
     * 0: dload_0
     * 1: dload_2
     * 2: dcmpl (<--- The opcode)
     * 3: ireturn
     *
     * @param cn     The ClassNode the method is supposed to be
     * @param opcode the comparision opcode. Allowed opcodes: LCMP, FCMPL, FCMPG, DCMPL, DCMPG
     * @return The method node
     */
    private static MethodNode generateComparisionMethod(ClassNode cn, int opcode) {
        if (!(opcode >= Opcodes.LCMP && opcode <= Opcodes.DCMPG))
            throw new IllegalArgumentException("The opcode must be LCMP, FCMPL, FCMPG, DCMPL or DCMPG");

        // The type of numbers which are compared
        Type type = opcode == Opcodes.LCMP ? Type.LONG_TYPE : (opcode == Opcodes.FCMPG || opcode == Opcodes.FCMPL) ? Type.FLOAT_TYPE : Type.DOUBLE_TYPE;
        String desc = "(" + type.toString() + type.toString() + ")I";

        MethodNode methodNode = new MethodNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, NameUtils.generateMethodName(cn, desc), desc, null, new String[0]);

        methodNode.instructions = new InsnList();

        methodNode.instructions.add(new VarInsnNode(type.getOpcode(Opcodes.ILOAD), 0));
        methodNode.instructions.add(new VarInsnNode(type.getOpcode(Opcodes.ILOAD), type.getSize()));
        methodNode.instructions.add(new InsnNode(opcode));
        methodNode.instructions.add(new InsnNode(Opcodes.IRETURN));

        return methodNode;
    }

}
