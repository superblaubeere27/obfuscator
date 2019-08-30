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

import me.superblaubeere27.annotations.ObfuscationTransformer;
import me.superblaubeere27.jobf.IClassTransformer;
import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.ProcessorCallback;
import me.superblaubeere27.jobf.utils.NodeUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StaticInitializionTransformer implements IClassTransformer {
    private static Random random = new Random();
    private JObfImpl inst;

    public StaticInitializionTransformer(JObfImpl inst) {
        this.inst = inst;
    }

    @Override
    public void process(ProcessorCallback callback, ClassNode node) {
        HashMap<FieldNode, Object> objs = new HashMap<>();
        for (FieldNode field : node.fields) {
            if (field.value != null) {
                if ((field.access & Opcodes.ACC_STATIC) != 0 && (field.value instanceof String || field.value instanceof Integer)) {
                    objs.put(field, field.value);
                    field.value = null;
                }
            }
        }
        InsnList toAdd = new InsnList();
        for (Map.Entry<FieldNode, Object> fieldNodeObjectEntry : objs.entrySet()) {
            if (fieldNodeObjectEntry.getValue() instanceof String) {
                toAdd.add(new LdcInsnNode(fieldNodeObjectEntry.getValue()));
            }
            if (fieldNodeObjectEntry.getValue() instanceof Integer) {
                toAdd.add(NodeUtils.generateIntPush((Integer) fieldNodeObjectEntry.getValue()));
            }
            toAdd.add(new FieldInsnNode(Opcodes.PUTSTATIC, node.name, fieldNodeObjectEntry.getKey().name, fieldNodeObjectEntry.getKey().desc));
        }
        MethodNode clInit = NodeUtils.getMethod(node, "<clinit>");
        if (clInit == null) {
            clInit = new MethodNode(Opcodes.ACC_STATIC, "<clinit>", "()V", null, new String[0]);
            node.methods.add(clInit);
        }

        if (clInit.instructions == null || clInit.instructions.getFirst() == null) {
            clInit.instructions = toAdd;
            clInit.instructions.add(new InsnNode(Opcodes.RETURN));
        } else {
            clInit.instructions.insertBefore(clInit.instructions.getFirst(), toAdd);
        }
        inst.setWorkDone();
    }

    @Override
    public ObfuscationTransformer getType() {
        return null;
    }

}