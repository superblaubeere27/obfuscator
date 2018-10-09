package me.superblaubeere27.jobf.utils;

import me.superblaubeere27.jobf.JObfImpl;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class InliningUtils {
    private static Pattern ANONYM_CLASSES = Pattern.compile(".+[$][0-9]+");

    public static InsnList inline(MethodNode node, ClassNode classNode, MethodNode target) {
        InsnList insns = new InsnList();

        VariableProvider provider = new VariableProvider(target);

        HashMap<LabelNode, LabelNode> labelMap = new HashMap<>();
        HashMap<Integer, Integer> slotMap = new HashMap<>();

        List<Type> argumentTypes = new ArrayList<>();

        if (!Modifier.isStatic(node.access)) {
            argumentTypes.add(Type.getType("L" + classNode.name + ";"));
        }
        argumentTypes.addAll(Arrays.asList(Type.getArgumentTypes(node.desc)));

        for (int i = 0; i < argumentTypes.size(); i++) {
            int pos = i;
            slotMap.put(pos, provider.allocateVar());
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
            }
        }

        Type returnType = Type.getReturnType(node.desc);

        {
            int args = argumentTypes.size();

            int i;

            for (i = args; i > 0; i--) {
                insns.add(new VarInsnNode(argumentTypes.get(i - 1).getOpcode(Opcodes.ISTORE), slotMap.get(i - 1)));
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
                insns.add(new VarInsnNode(abstractInsnNode.getOpcode(), slotMap.get(((VarInsnNode) abstractInsnNode).var)));
                continue;
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

    public static boolean canInlineMethod(ClassNode from, ClassNode clazz, MethodNode node) {
        if (node.name.startsWith("<")) return false;
        if (Modifier.isAbstract(node.access) || Modifier.isNative(node.access)) return false;
        if (Modifier.isInterface(clazz.access)) return false;

        if (node.instructions.size() < 2) return false;

        boolean ok = true;

        for (AbstractInsnNode abstractInsnNode : node.instructions.toArray()) {
//            if (abstractInsnNode instanceof LabelNode) {
//                ok = false;
//                break;
//            }
            if (abstractInsnNode instanceof MethodInsnNode) {
                if (abstractInsnNode.getOpcode() == Opcodes.INVOKESPECIAL && !((MethodInsnNode) abstractInsnNode).name.startsWith("<")) {
                    ok = false;
                    break;
                }
                ClassNode lookup = Utils.lookupClass(((MethodInsnNode) abstractInsnNode).owner);

                if (lookup == null) {
                    ok = false;
                    break;
                }

                MethodNode lookupMethod = Utils.getMethod(lookup, ((MethodInsnNode) abstractInsnNode).name, ((MethodInsnNode) abstractInsnNode).desc);

                if (lookupMethod == null || !canAccessMethod(clazz, lookupMethod, lookup, lookupMethod)) {
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
        }

        return ok;
    }


    public static boolean canAccessField(ClassNode from, FieldNode method, ClassNode node, FieldNode thing) {
        if (!canAccess(from, node)) return false;

        if (Modifier.isPublic(thing.access)) {
            return true;
        }
        if (Modifier.isPrivate(thing.access)) {
            return isInnerClass(node, from);
        }
        if (Modifier.isProtected(thing.access)) {
            return JObfImpl.INSTANCE.isSubclass(node.name, from.name);
        }

        return NameUtils.getPackage(from.name).equals(NameUtils.getPackage(node.name));
    }

    public static boolean canAccessMethod(ClassNode from, MethodNode method, ClassNode node, MethodNode thing) {
        if (!canAccess(from, node)) return false;


        if (Modifier.isPublic(thing.access)) {
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

    public static boolean canAccess(ClassNode from, ClassNode node) {
        if (ANONYM_CLASSES.matcher(from.name).matches() || ANONYM_CLASSES.matcher(node.name).matches()) { // Is
            return false;
        }
        if (Modifier.isPublic(node.access)) {
            return true;
        }
        if (Modifier.isPrivate(node.access)) {
            return isInnerClass(node, from);
        }
        if (Modifier.isProtected(node.access)) {
            return JObfImpl.INSTANCE.isSubclass(node.name, from.name);
        }
        return NameUtils.getPackage(from.name).equals(NameUtils.getPackage(node.name));
    }

    private static boolean isInnerClass(ClassNode node, ClassNode of) {
        return node.name.equals(of.name) || node.outerClass != null && node.outerClass.equals(of.name);
    }

    private static boolean isSuperClass(ClassNode node, ClassNode of) {
        return node.superName != null && node.superName.equals(of.name);
    }

}
