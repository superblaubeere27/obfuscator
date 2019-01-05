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

import me.superblaubeere27.jobf.utils.NodeUtils;
import me.superblaubeere27.jobf.utils.Utils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

class StaticStringCallOptimizer {

    static void optimize(MethodNode method) {
        boolean found;

        do {
            found = false;

            for (AbstractInsnNode insnNode : method.instructions.toArray()) {
                if (insnNode instanceof MethodInsnNode) {
                    MethodInsnNode methodInsnNode = (MethodInsnNode) insnNode;
                    AbstractInsnNode prev = Utils.getPrevious(methodInsnNode, 1);

                    if (prev instanceof LdcInsnNode && ((LdcInsnNode) prev).cst instanceof String && (Utils.matchMethodNode(methodInsnNode, "java/lang/Object.hashCode:()I") || Utils.matchMethodNode(methodInsnNode, "java/lang/String.hashCode:()I"))) {
                        method.instructions.insert(insnNode, NodeUtils.generateIntPush(((LdcInsnNode) prev).cst.hashCode()));
                        method.instructions.remove(insnNode);
                        method.instructions.remove(prev);
                        found = true;
                    }
                    if (prev instanceof LdcInsnNode && ((LdcInsnNode) prev).cst instanceof String && (Utils.matchMethodNode(methodInsnNode, "java/lang/String.toUpperCase:()Ljava/lang/String;"))) {
                        method.instructions.insert(insnNode, new LdcInsnNode(((String) ((LdcInsnNode) prev).cst).toUpperCase()));
                        method.instructions.remove(insnNode);
                        method.instructions.remove(prev);
                        found = true;
                    }
                    if (prev instanceof LdcInsnNode && ((LdcInsnNode) prev).cst instanceof String && (Utils.matchMethodNode(methodInsnNode, "java/lang/String.toLowerCase:()Ljava/lang/String;"))) {
                        method.instructions.insert(insnNode, new LdcInsnNode(((String) ((LdcInsnNode) prev).cst).toLowerCase()));
                        method.instructions.remove(insnNode);
                        method.instructions.remove(prev);
                        found = true;
                    }
                }
            }
        } while (found);
    }

}
