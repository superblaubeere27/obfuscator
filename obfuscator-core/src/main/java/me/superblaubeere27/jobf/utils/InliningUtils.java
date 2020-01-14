/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27.jobf.utils;

import me.superblaubeere27.jobf.JObfImpl;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceValue;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Pattern;

public class InliningUtils {
    private static Pattern ANONYM_CLASSES = Pattern.compile(".+[$][0-9]+");

    public static InsnList inline(MethodNode node, ClassNode classNode, MethodNode target, Frame<SourceValue> frame) {
        InsnList insns = new InsnList();

        VariableProvider provider = new VariableProvider(target);

        HashMap<LabelNode, LabelNode> labelMap = new HashMap<>();
        // Mapping of slots. KEY: Old, VALUE: New
        HashMap<Integer, Integer> slotMap = new HashMap<>();
        // Mapping of arguments
        HashMap<Integer, Integer> argumentMap = new HashMap<>();
        // A parameter is added if a STORE changes the variable
        List<Integer> changedParamenters = new ArrayList<>();
        // KEY: Parameter of the method, VALUE: Inlined Instruction
        HashMap<Integer, AbstractInsnNode> inlinedParameters = new HashMap<>();

        List<Type> argumentTypes = new ArrayList<>();

        if (!Modifier.isStatic(node.access)) {
            argumentTypes.add(Type.getType("L" + classNode.name + ";"));
        }

        argumentTypes.addAll(Arrays.asList(Type.getArgumentTypes(node.desc)));

        int j = 0;

        for (int i = 0; i < argumentTypes.size(); j += argumentTypes.get(i).getSize(), i++) {
            int var = provider.allocateVar();

            slotMap.put(j, var);
            argumentMap.put(i, var);
        }

        // Search for labels and replace them
        // + replace local vars / parameters with new ones
        for (AbstractInsnNode abstractInsnNode : node.instructions.toArray()) {
            if (abstractInsnNode instanceof LabelNode) {
                LabelNode insnNode = (LabelNode) abstractInsnNode;
                labelMap.put(insnNode, new LabelNode());
            }
            if (abstractInsnNode instanceof VarInsnNode) {
                if (!slotMap.containsKey(((VarInsnNode) abstractInsnNode).var)) {
                    slotMap.put(((VarInsnNode) abstractInsnNode).var, provider.allocateVar());
                }
                if (abstractInsnNode.getOpcode() >= Opcodes.ISTORE && abstractInsnNode.getOpcode() <= Opcodes.ASTORE) {
                    changedParamenters.add(((VarInsnNode) abstractInsnNode).var);
                }
            }
            if (abstractInsnNode instanceof IincInsnNode) {
                changedParamenters.add(((IincInsnNode) abstractInsnNode).var);
            }
        }

        Type returnType = Type.getReturnType(node.desc);

        {
            int args = argumentTypes.size();

            int i;

            for (i = args; i > 0; i--) {
                SourceValue stack = frame.getStack(frame.getStackSize() - 1 - i);

                if (stack.insns.size() == 1) {
                    AbstractInsnNode abstractInsnNode = stack.insns.iterator().next();

                    if (canInlineInstruction(abstractInsnNode, changedParamenters)) {
                        insns.add(new InsnNode(stack.getSize() == 2 ? Opcodes.POP2 : Opcodes.POP));
                        inlinedParameters.put(argumentMap.get(i - 1), abstractInsnNode);
                    } else
                        insns.add(new VarInsnNode(argumentTypes.get(i - 1).getOpcode(Opcodes.ISTORE), argumentMap.get(i - 1)));
                } else {
                    insns.add(new VarInsnNode(argumentTypes.get(i - 1).getOpcode(Opcodes.ISTORE), argumentMap.get(i - 1)));
                }
            }
        }

        int returnSlot = provider.allocateVar();

        LabelNode endLabel = new LabelNode();

        for (AbstractInsnNode abstractInsnNode : node.instructions.toArray()) {
            if (abstractInsnNode.getOpcode() >= Opcodes.IRETURN && abstractInsnNode.getOpcode() <= Opcodes.RETURN) {
                if (abstractInsnNode.getOpcode() != Opcodes.RETURN) {
                    insns.add(new VarInsnNode(returnType.getOpcode(Opcodes.ISTORE), returnSlot));
                }

                insns.add(new JumpInsnNode(Opcodes.GOTO, endLabel));

                continue;
            }

            if (abstractInsnNode instanceof LabelNode) {
                insns.add(labelMap.get(abstractInsnNode));
                continue;
            }

            if (abstractInsnNode instanceof VarInsnNode) {
                if (inlinedParameters.containsKey(((VarInsnNode) abstractInsnNode).var)) {
                    insns.add(inlinedParameters.get(((VarInsnNode) abstractInsnNode).var).clone(null)); // TODO Prevent STORE instructions
                } else {
                    insns.add(new VarInsnNode(abstractInsnNode.getOpcode(), slotMap.get(((VarInsnNode) abstractInsnNode).var)));
                }

                continue;
            }

            if (abstractInsnNode instanceof IincInsnNode) {
                insns.add(new IincInsnNode(slotMap.get(((IincInsnNode) abstractInsnNode).var), ((IincInsnNode) abstractInsnNode).incr));
                continue;
            }

            if (abstractInsnNode instanceof LineNumberNode || abstractInsnNode instanceof FrameNode)
                continue;

            if (abstractInsnNode instanceof MethodInsnNode) {
                ClassNode lookup = Objects.requireNonNull(Utils.lookupClass(((MethodInsnNode) abstractInsnNode).owner));
                MethodNode lookupMethod = Objects.requireNonNull(Utils.getMethod(lookup, ((MethodInsnNode) abstractInsnNode).name, ((MethodInsnNode) abstractInsnNode).desc, true));

                lookup.access = makePublic(lookup.access);
                lookupMethod.access = makePublic(lookupMethod.access);
            }

            if (abstractInsnNode instanceof FieldInsnNode) {
                ClassNode lookup = Objects.requireNonNull(Utils.lookupClass(((FieldInsnNode) abstractInsnNode).owner));
                FieldNode lookupMethod = Objects.requireNonNull(Utils.getField(lookup, ((FieldInsnNode) abstractInsnNode).name));


                lookup.access = makePublic(lookup.access);
                lookupMethod.access = makePublic(lookupMethod.access);
            }

            insns.add(abstractInsnNode.clone(labelMap));

        }

        insns.add(endLabel);

        if (returnType.getSort() != Type.VOID) {
            insns.add(new VarInsnNode(returnType.getOpcode(Opcodes.ILOAD), returnSlot));
        }

        for (TryCatchBlockNode tryCatchBlock : node.tryCatchBlocks) {
            target.tryCatchBlocks.add(new TryCatchBlockNode(labelMap.get(tryCatchBlock.start), labelMap.get(tryCatchBlock.end), labelMap.get(tryCatchBlock.handler), tryCatchBlock.type));
        }

        return insns;
    }

    private static boolean canInlineInstruction(AbstractInsnNode next, List<Integer> setParemeters) {
//        if (next instanceof LdcInsnNode) {
//            return true;
//        }
//        if (next.getOpcode() >= Opcodes.ACONST_NULL && next.getOpcode() <= Opcodes.SIPUSH) {
//            return true;
//        }
////        if (next.getOpcode() >= Opcodes.GETSTATIC) {
////            return true;
////        }
//        if (next instanceof VarInsnNode && next.getOpcode() >= Opcodes.ILOAD && next.getOpcode() <= Opcodes.ALOAD) {
//            return !setParemeters.contains(((VarInsnNode) next).var);
//        }

        return false;
    }

    private static int makePublic(int access) {
        access = access & ~Opcodes.ACC_PRIVATE;
        access = access & ~Opcodes.ACC_PRIVATE;
        access = access | Opcodes.ACC_PUBLIC;
        return access;
    }

    public static boolean canInlineMethod(ClassNode from, ClassNode clazz, MethodNode node) {
//        if (node.name.equals("acceptsAll")) {
//            System.out.println("Fuk");
//        }
        if (node.name.startsWith("<")) return false;
        if (Modifier.isAbstract(node.access) || Modifier.isNative(node.access)) return false;
        if (Modifier.isInterface(clazz.access)) return false;

        if (node.instructions.size() == 0) return false;
//        if (node.tryCatchBlocks.size() > 0) return false;

        boolean ok = true;

        for (AbstractInsnNode abstractInsnNode : node.instructions.toArray()) {
//            if (abstractInsnNode instanceof LabelNode) {
//                ok = false;
//                break;
//            }
            if (abstractInsnNode instanceof MethodInsnNode) {
                ClassNode lookup = Utils.lookupClass(((MethodInsnNode) abstractInsnNode).owner);

                if (lookup == null) {
                    ok = false;
                    break;
                }

                MethodNode lookupMethod = Utils.getMethod(lookup, ((MethodInsnNode) abstractInsnNode).name, ((MethodInsnNode) abstractInsnNode).desc, true);

                if (lookupMethod == null || !canAccessMethod(from, lookupMethod, lookup, lookupMethod)) {
                    ok = false;
                    break;
                }
            }

            if (abstractInsnNode instanceof FieldInsnNode) {
                ClassNode lookup = Utils.lookupClass(((FieldInsnNode) abstractInsnNode).owner);

                if (lookup == null) {
                    ok = false;
                    break;
                }

                FieldNode lookupMethod = Utils.getField(lookup, ((FieldInsnNode) abstractInsnNode).name);

                if (lookupMethod == null || !canAccessField(from, lookupMethod, lookup, lookupMethod)) {
                    ok = false;
                    break;
                }
            }
            if (abstractInsnNode instanceof LdcInsnNode) {
                LdcInsnNode ldc = (LdcInsnNode) abstractInsnNode;

                if (ldc.cst instanceof Type) {
                    if (((Type) ldc.cst).getSort() == Type.OBJECT) {
                        ClassNode lookup = Utils.lookupClass(((Type) ldc.cst).getInternalName());

                        if (lookup == null) {
                            ok = false;
                            break;
                        }

                        if (cantAccess(from, lookup)) {
                            ok = false;
                            break;
                        }
                    }
                }

            }
        }

        return ok;
    }


    private static boolean canAccessField(ClassNode from, FieldNode method, ClassNode node, FieldNode thing) {
        if (cantAccess(from, node)) return false;

        if (Modifier.isPublic(thing.access)) {
            return true;
        }
        if (JObfImpl.INSTANCE.isLoadedCode(node)) {
            return false;
        }
        if (Modifier.isPrivate(thing.access)) {
            return isInnerClass(node, from);
        }
        if (Modifier.isProtected(thing.access)) {
            return false;
        }

        return NameUtils.getPackage(from.name).equals(NameUtils.getPackage(node.name));
    }

    private static boolean canAccessMethod(ClassNode from, MethodNode method, ClassNode node, MethodNode thing) {
        if (cantAccess(from, node)) return false;

        if (Modifier.isPublic(thing.access)) {
            return true;
        }
        if (JObfImpl.INSTANCE.isLoadedCode(node)) {
            return true;
        }
        if (Modifier.isPrivate(thing.access)) {
            return isInnerClass(node, from);
        }
        if (Modifier.isProtected(thing.access)) {
            return isSuperClass(from, node);
        }

        return NameUtils.getPackage(from.name).equals(NameUtils.getPackage(node.name));
    }

    private static boolean cantAccess(ClassNode from, ClassNode node) {
        if (ANONYM_CLASSES.matcher(from.name).matches() || ANONYM_CLASSES.matcher(node.name).matches()) { // Is
            return true;
        }
        if (Modifier.isPublic(node.access)) {
            return false;
        }
        if (JObfImpl.INSTANCE.isLoadedCode(node)) {
            return true;
        }
        if (Modifier.isPrivate(node.access)) {
            return !isInnerClass(node, from);
        }
        if (Modifier.isProtected(node.access)) {
            return true;
        }
        return !NameUtils.getPackage(from.name).equals(NameUtils.getPackage(node.name));
    }

    private static boolean isInnerClass(ClassNode node, ClassNode of) {
        return node.name.equals(of.name) || node.outerClass != null && node.outerClass.equals(of.name);
    }

    private static boolean isSuperClass(ClassNode node, ClassNode of) {
        return node.superName != null && node.superName.equals(of.name);
    }

}
