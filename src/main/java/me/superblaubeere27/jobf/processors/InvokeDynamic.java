package me.superblaubeere27.jobf.processors;

import me.superblaubeere27.jobf.IClassProcessor;
import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.utils.NameUtils;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.util.Random;

import static org.objectweb.asm.Opcodes.H_INVOKESTATIC;

public class InvokeDynamic implements IClassProcessor {
    private static Random random = new Random();
    private JObfImpl inst;

    public InvokeDynamic(JObfImpl inst) {
        this.inst = inst;
    }

    private static MethodNode bootstrap(ClassNode node) {
        MethodNode mv;
        {
            mv = new MethodNode(Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC, NameUtils.generateMethodName(node.name, "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"), "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", null, new String[]{"java/lang/NoSuchMethodException", "java/lang/IllegalAccessException"});
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
            mv.visitLabel(l0);
            mv.visitLineNumber(21, l0);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitLdcInsn(":");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "split", "(Ljava/lang/String;)[Ljava/lang/String;", false);
            mv.visitVarInsn(Opcodes.ASTORE, 3);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitLineNumber(22, l3);
            mv.visitVarInsn(Opcodes.ALOAD, 3);
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitInsn(Opcodes.AALOAD);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            mv.visitVarInsn(Opcodes.ASTORE, 4);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitLineNumber(23, l4);
            mv.visitVarInsn(Opcodes.ALOAD, 3);
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitInsn(Opcodes.AALOAD);
            mv.visitVarInsn(Opcodes.ASTORE, 5);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitLineNumber(24, l5);
            mv.visitVarInsn(Opcodes.ALOAD, 3);
            mv.visitInsn(Opcodes.ICONST_2);
            mv.visitInsn(Opcodes.AALOAD);
            mv.visitLdcInsn(Type.getType("L" + node.name + ";"));
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitLineNumber(25, l6);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;", false);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitLineNumber(24, l7);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/invoke/MethodType", "fromMethodDescriptorString", "(Ljava/lang/String;Ljava/lang/ClassLoader;)Ljava/lang/invoke/MethodType;", false);
            mv.visitVarInsn(Opcodes.ASTORE, 6);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitLineNumber(26, l8);
            mv.visitVarInsn(Opcodes.ALOAD, 3);
            mv.visitInsn(Opcodes.ICONST_3);
            mv.visitInsn(Opcodes.AALOAD);
            mv.visitVarInsn(Opcodes.ASTORE, 7);
            Label l9 = new Label();
            mv.visitLabel(l9);
            mv.visitLineNumber(27, l9);
            mv.visitInsn(Opcodes.ACONST_NULL);
            mv.visitVarInsn(Opcodes.ASTORE, 8);
            Label l10 = new Label();
            mv.visitLabel(l10);
            mv.visitLineNumber(33, l10);
            mv.visitVarInsn(Opcodes.ALOAD, 7);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
            mv.visitInsn(Opcodes.ICONST_1);
            Label l11 = new Label();
            mv.visitJumpInsn(Opcodes.IF_ICMPLE, l11);
            Label l12 = new Label();
            mv.visitLabel(l12);
            mv.visitLineNumber(34, l12);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.ALOAD, 4);
            mv.visitVarInsn(Opcodes.ALOAD, 5);
            mv.visitVarInsn(Opcodes.ALOAD, 6);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findVirtual", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "asType", "(Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(Opcodes.ASTORE, 8);
            Label l13 = new Label();
            mv.visitJumpInsn(Opcodes.GOTO, l13);
            mv.visitLabel(l11);
            mv.visitLineNumber(36, l11);
            mv.visitFrame(Opcodes.F_FULL, 9, new Object[]{"java/lang/invoke/MethodHandles$Lookup", "java/lang/String", "java/lang/invoke/MethodType", "[Ljava/lang/String;", "java/lang/Class", "java/lang/String", "java/lang/invoke/MethodType", "java/lang/String", "java/lang/invoke/MethodHandle"}, 0, new Object[]{});
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.ALOAD, 4);
            mv.visitVarInsn(Opcodes.ALOAD, 5);
            mv.visitVarInsn(Opcodes.ALOAD, 6);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findStatic", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(Opcodes.ASTORE, 8);
            mv.visitLabel(l13);
            mv.visitLineNumber(38, l13);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/invoke/ConstantCallSite");
            mv.visitInsn(Opcodes.DUP);
            mv.visitVarInsn(Opcodes.ALOAD, 8);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/invoke/ConstantCallSite", "<init>", "(Ljava/lang/invoke/MethodHandle;)V", false);
            mv.visitLabel(l1);
            mv.visitInsn(Opcodes.ARETURN);
            mv.visitLabel(l2);
            mv.visitLineNumber(39, l2);
            mv.visitFrame(Opcodes.F_FULL, 3, new Object[]{"java/lang/invoke/MethodHandles$Lookup", "java/lang/String", "java/lang/invoke/MethodType"}, 1, new Object[]{"java/lang/Exception"});
            mv.visitVarInsn(Opcodes.ASTORE, 3);
            Label l14 = new Label();
            mv.visitLabel(l14);
            mv.visitLineNumber(40, l14);
            mv.visitVarInsn(Opcodes.ALOAD, 3);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false);
            Label l15 = new Label();
            mv.visitLabel(l15);
            mv.visitLineNumber(41, l15);
            mv.visitInsn(Opcodes.ACONST_NULL);
            mv.visitInsn(Opcodes.ARETURN);
            mv.visitMaxs(4, 9);
            mv.visitEnd();
        }
        return mv;
    }

    @Override
    public void process(ClassNode classNode, int mode) {
        if (Modifier.isInterface(classNode.access)) {
            return;
        }
        MethodNode bootstrap = bootstrap(classNode);
        Handle bootstrapMethod = new Handle(H_INVOKESTATIC, classNode.name, bootstrap.name,
                MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class,
                        MethodType.class).toMethodDescriptorString(), false);

        for (MethodNode method : classNode.methods) {
            for (AbstractInsnNode abstractInsnNode : method.instructions.toArray()) {
                if (abstractInsnNode instanceof MethodInsnNode) {
                    MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;

//                    if (methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
//                        method.instructions.insert(methodInsnNode, new InvokeDynamicInsnNode(methodInsnNode.owner.replace('/', '.') + ":" + methodInsnNode.name + ":" + methodInsnNode.desc + ":" + NameUtils.generateSpaceString(2), methodInsnNode.desc, bootstrapMethod));
//                        method.instructions.remove(methodInsnNode);
//                    }
                    if (methodInsnNode.getOpcode() == Opcodes.INVOKESTATIC) {
                        method.instructions.insert(methodInsnNode, new InvokeDynamicInsnNode(methodInsnNode.owner.replace('/', '.') + ":" + methodInsnNode.name + ":" + methodInsnNode.desc + ":" + NameUtils.unicodeString(1), methodInsnNode.desc, bootstrapMethod));
                        method.instructions.remove(methodInsnNode);
                    }
                }
            }
        }

        classNode.methods.add(bootstrap);
    }

}