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
import me.superblaubeere27.jobf.utils.NodeUtils;
import me.superblaubeere27.jobf.utils.VariableProvider;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class LocalVariableMangler {
    static void mangleLocalVariables(ProcessorCallback callback, ClassNode node, MethodNode method) {
        int maxStackSize = method.maxStack;
        int maxLocals = method.maxLocals;
        method.maxStack = 1337;
        method.maxLocals = 1337;

        Frame<SourceValue>[] frames;
        try {
            frames = new Analyzer<>(new SourceInterpreter()).analyze(node.name, method);
        } catch (AnalyzerException e) {
            throw new RuntimeException(e);
        }

        method.maxStack = maxStackSize;
        method.maxLocals = maxLocals;

        VariableProvider provider = new VariableProvider(method);

        // Map of local variables and their types. They are added if the type of the variable is double, float, int or long
        HashMap<Integer, Type> localVarMap = new HashMap<>();

        // KEY: Type of array
        // VALUE: Array local variable index
        HashMap<Type, Integer> typeArrayMap = new HashMap<>();

        // KEY: Original Local variable
        // VALUE: [0]: Local variable index of the array, [1]: Array index
        HashMap<Integer, int[]> slotMap = new HashMap<>();

        // KEY: Array local variable index
        // VALUE: Current highest array index
        HashMap<Integer, Integer> arrayIndices = new HashMap<>();

        //<editor-fold desc="Scanning">
        for (AbstractInsnNode abstractInsnNode : method.instructions.toArray()) {
            if (abstractInsnNode instanceof VarInsnNode) {
                VarInsnNode insnNode = (VarInsnNode) abstractInsnNode;

                if (provider.isArgument(insnNode.var)) continue;

                if (!localVarMap.containsKey(insnNode.var)) {
                    Type t = null;

                    switch (insnNode.getOpcode() - Opcodes.ILOAD) {
                        case 0:
                            t = Type.INT_TYPE;
                            break;
//                        case Opcodes.LLOAD - Opcodes.ILOAD:
//                            t = Type.LONG_TYPE;
//                            break;
                        case Opcodes.FLOAD - Opcodes.ILOAD:
                            t = Type.FLOAT_TYPE;
                            break;
//                        case Opcodes.DLOAD - Opcodes.ILOAD:
//                            t = Type.DOUBLE_TYPE;
//                            break;
                    }
                    if (t != null) {
                        localVarMap.put(insnNode.var, t);
                    }
                }

                if (insnNode.getOpcode() >= Opcodes.ISTORE && insnNode.getOpcode() <= Opcodes.ASTORE) {
                    Frame<SourceValue> currentFrame = frames[method.instructions.indexOf(insnNode)];

                    SourceValue stack = currentFrame.getStack(currentFrame.getStackSize() - 1);

                    if (stack.getSize() > 1) {
                        localVarMap.put(insnNode.var, Type.VOID_TYPE);
                    }
                }
            }
        }
        //</editor-fold>


        {
            List<Integer> remove = new ArrayList<>();

            for (Map.Entry<Integer, Type> integerTypeEntry : localVarMap.entrySet()) {
                if (integerTypeEntry.getValue().getSort() == Type.VOID) {
                    remove.add(integerTypeEntry.getKey());
                }
            }

            for (Integer integer : remove) {
                localVarMap.remove(integer);
            }
        }

        {
            for (Map.Entry<Integer, Type> integerTypeEntry : localVarMap.entrySet()) {

                if (!typeArrayMap.containsKey(integerTypeEntry.getValue()))
                    typeArrayMap.put(integerTypeEntry.getValue(), provider.allocateVar());

                int index = typeArrayMap.get(integerTypeEntry.getValue());
                int arrayIndex;

                if (!arrayIndices.containsKey(index)) {
                    arrayIndices.put(index, 0);
                }

                arrayIndex = arrayIndices.get(index);

                arrayIndices.put(index, arrayIndex + 1);

                slotMap.put(integerTypeEntry.getKey(), new int[]{index, arrayIndex});
            }
        }

        InsnList initialize = new InsnList();

        for (Map.Entry<Type, Integer> integerTypeEntry : typeArrayMap.entrySet()) {
            int arrayType = integerTypeEntry.getKey().getSort();

            switch (arrayType) {
                case Type.INT:
                    arrayType = Opcodes.T_INT;
                    break;
                case Type.LONG:
                    arrayType = Opcodes.T_LONG;
                    break;
                case Type.DOUBLE:
                    arrayType = Opcodes.T_DOUBLE;
                    break;
                case Type.FLOAT:
                    arrayType = Opcodes.T_FLOAT;
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            initialize.add(NodeUtils.generateIntPush(arrayIndices.get(integerTypeEntry.getValue())));
            initialize.add(new IntInsnNode(Opcodes.NEWARRAY, arrayType));
            initialize.add(new VarInsnNode(Opcodes.ASTORE, integerTypeEntry.getValue()));
        }

        for (AbstractInsnNode abstractInsnNode : method.instructions.toArray()) {
            if (abstractInsnNode instanceof VarInsnNode) {
                VarInsnNode varInsnNode = (VarInsnNode) abstractInsnNode;

                if (slotMap.containsKey(varInsnNode.var)) {
                    // Check if it is a load instruction
                    if (abstractInsnNode.getOpcode() == Opcodes.ILOAD
                            || abstractInsnNode.getOpcode() == Opcodes.LLOAD
                            || abstractInsnNode.getOpcode() == Opcodes.FLOAD
                            || abstractInsnNode.getOpcode() == Opcodes.DLOAD) {
                        int[] value = slotMap.get(varInsnNode.var);

                        InsnList replace = new InsnList();

                        replace.add(new VarInsnNode(Opcodes.ALOAD, value[0]));
                        replace.add(NodeUtils.generateIntPush(value[1]));
                        replace.add(new InsnNode(localVarMap.get(varInsnNode.var).getOpcode(Opcodes.IALOAD)));

                        method.instructions.insert(varInsnNode, replace);
                        method.instructions.remove(varInsnNode);
                    }
                    // Check if it is a store instruction
                    if (abstractInsnNode.getOpcode() == Opcodes.ISTORE
                            || abstractInsnNode.getOpcode() == Opcodes.LSTORE
                            || abstractInsnNode.getOpcode() == Opcodes.FSTORE
                            || abstractInsnNode.getOpcode() == Opcodes.DSTORE) {
                        int[] value = slotMap.get(varInsnNode.var);


                        InsnList replace = new InsnList();


                        Type type = localVarMap.get(varInsnNode.var);


                        replace.add(new VarInsnNode(Opcodes.ALOAD, value[0]));
                        replace.add(new InsnNode(Opcodes.SWAP));
                        replace.add(NodeUtils.generateIntPush(value[1]));
                        replace.add(new InsnNode(Opcodes.SWAP));
                        replace.add(new InsnNode(type.getOpcode(Opcodes.IASTORE)));

                        method.instructions.insert(varInsnNode, replace);
                        method.instructions.remove(varInsnNode);
                    }
                }
            }

            if (abstractInsnNode instanceof IincInsnNode) {
                IincInsnNode iincInsnNode = (IincInsnNode) abstractInsnNode;

                if (slotMap.containsKey(iincInsnNode.var)) {
                    int[] value = slotMap.get(iincInsnNode.var);

                    InsnList replace = new InsnList();

                    replace.add(new VarInsnNode(Opcodes.ALOAD, value[0]));
                    replace.add(NodeUtils.generateIntPush(value[1]));

                    replace.add(new VarInsnNode(Opcodes.ALOAD, value[0]));
                    replace.add(NodeUtils.generateIntPush(value[1]));
                    replace.add(new InsnNode(localVarMap.get(iincInsnNode.var).getOpcode(Opcodes.IALOAD)));

                    replace.add(NodeUtils.generateIntPush(iincInsnNode.incr));

                    replace.add(new InsnNode(Opcodes.IADD));

                    replace.add(new InsnNode(localVarMap.get(iincInsnNode.var).getOpcode(Opcodes.IASTORE)));

                    method.instructions.insert(iincInsnNode, replace);
                    method.instructions.remove(iincInsnNode);
                }
            }

        }


        if (localVarMap.size() > 0)
            method.instructions.insertBefore(method.instructions.getFirst(), initialize);

        callback.setForceComputeFrames();
    }
}
