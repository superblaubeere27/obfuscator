/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27.jobf.processors.optimizer;

import me.superblaubeere27.jobf.utils.Utils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

class ComparisionReplacer {
    static void replaceComparisons(MethodNode method, boolean replaceEquals, boolean replaceEqualsIgnoreCase) {
        for (AbstractInsnNode insnNode : method.instructions.toArray()) {
            if (insnNode instanceof MethodInsnNode) {
                MethodInsnNode methodInsnNode = (MethodInsnNode) insnNode;

                if (replaceEquals && Utils.matchMethodNode(methodInsnNode, "java/lang/String.equals:(Ljava/lang/Object;)Z")) {
                    InsnList replacement = new InsnList();

                    replacement.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "hashCode", "()I", false));
                    replacement.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false));

                    replacement.add(new InsnNode(Opcodes.SWAP));

                    replacement.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false));
                    replacement.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false));

                    replacement.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "equals", "(Ljava/lang/Object;)Z", false));

                    method.instructions.insert(insnNode, replacement);
                    method.instructions.remove(insnNode);
                }
                if (replaceEqualsIgnoreCase && Utils.matchMethodNode(methodInsnNode, "java/lang/String.equalsIgnoreCase:(Ljava/lang/String;)Z")) {
                    InsnList replacement = new InsnList();

                    replacement.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "toUpperCase", "()Ljava/lang/String;", false));
                    replacement.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "hashCode", "()I", false));
                    replacement.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false));

                    replacement.add(new InsnNode(Opcodes.SWAP));

                    replacement.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "toUpperCase", "()Ljava/lang/String;", false));
                    replacement.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false));
                    replacement.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false));

                    replacement.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "equals", "(Ljava/lang/Object;)Z", false));

                    method.instructions.insert(insnNode, replacement);
                    method.instructions.remove(insnNode);
                }
            }
        }

    }

}
