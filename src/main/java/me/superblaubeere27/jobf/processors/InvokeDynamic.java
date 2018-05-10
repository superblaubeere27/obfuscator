package me.superblaubeere27.jobf.processors;

import me.superblaubeere27.jobf.IClassProcessor;
import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.utils.NameUtils;
import me.superblaubeere27.jobf.utils.NodeUtils;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.objectweb.asm.Opcodes.*;

public class InvokeDynamic implements IClassProcessor {
    private static Random random = new Random();
    private JObfImpl inst;

    public InvokeDynamic(JObfImpl inst) {
        this.inst = inst;
    }

    @Override
    public void process(ClassNode classNode, int mode) {
        final String bootstrapName = NameUtils.generateMethodName(classNode, "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/ConstantCallSite;");
//        String bootstrapName = "bt";
        final String arrayName = NameUtils.generateFieldName(classNode);

        final List<String> strings = new ArrayList<>();

        final Handle bootstrap = new Handle(Opcodes.H_INVOKESTATIC, classNode.name, bootstrapName, "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/ConstantCallSite;", false);

        for (final MethodNode method : classNode.methods) {
            for (final AbstractInsnNode abstractNode : method.instructions.toArray()) {
                if (abstractNode instanceof MethodInsnNode) {
                    final MethodInsnNode methodInsnNode = (MethodInsnNode) abstractNode;

                    if (methodInsnNode.getOpcode() != Opcodes.INVOKESTATIC && methodInsnNode.getOpcode() != Opcodes.INVOKEVIRTUAL)
                        continue;

                    method.instructions.insert(abstractNode, new InvokeDynamicInsnNode(String.valueOf(strings.size()), methodInsnNode.desc, bootstrap));
                    method.instructions.remove(abstractNode);
                    strings.add(methodInsnNode.owner.replace('/', '.') + ":" + methodInsnNode.name + ":" + methodInsnNode.desc + ":" + (methodInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL ? "virtual" : "static"));
                }
            }
        }

        final InsnList decryptList = new InsnList();

        // Create array
        decryptList.add(NodeUtils.generateIntPush(strings.size()));
        decryptList.add(new TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/String"));
        decryptList.add(new FieldInsnNode(Opcodes.PUTSTATIC, classNode.name, arrayName, "[Ljava/lang/String;"));

        int i = 0;
        for (final String string : strings) {
            decryptList.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, arrayName, "[Ljava/lang/String;"));
            decryptList.add(NodeUtils.generateIntPush(i));
            decryptList.add(new LdcInsnNode(string));
            decryptList.add(new InsnNode(Opcodes.AASTORE));
            i++;
        }

        decryptList.add(new InsnNode(Opcodes.RETURN));

        final MethodNode method = new MethodNode(Modifier.PRIVATE | Modifier.STATIC, NameUtils.generateMethodName(classNode, "()V"), "()V", null, null);

        method.instructions.add(decryptList);

        classNode.methods.add(method);

        // Add to clinit method
        MethodNode initMethod = NodeUtils.getMethod(classNode, "<clinit>");

        if (initMethod == null) {
            initMethod = new MethodNode(Modifier.STATIC, "<clinit>", "()V", null, null);
            initMethod.instructions.add(new InsnNode(Opcodes.RETURN));
            classNode.methods.add(initMethod);
        }

        initMethod.instructions.insertBefore(initMethod.instructions.getFirst(), new MethodInsnNode(Opcodes.INVOKESTATIC, classNode.name, method.name, method.desc, false));

        // Field
        classNode.fields.add(new FieldNode(ACC_PRIVATE + ACC_STATIC, arrayName, "[Ljava/lang/String;", null, null));

        // Bootstrap method
        final MethodNode bootstrapMethod = new MethodNode(ACC_PRIVATE + ACC_STATIC, bootstrapName, "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/ConstantCallSite;", null, null);

        bootstrapMethod.visitCode();
        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();
        bootstrapMethod.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
        bootstrapMethod.visitLabel(l0);
        bootstrapMethod.visitFieldInsn(GETSTATIC, classNode.name, arrayName, "[Ljava/lang/String;");
        bootstrapMethod.visitVarInsn(ALOAD, 1);
        bootstrapMethod.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
        bootstrapMethod.visitInsn(AALOAD);
        bootstrapMethod.visitLdcInsn(":");
        bootstrapMethod.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "split", "(Ljava/lang/String;)[Ljava/lang/String;", false);
        bootstrapMethod.visitVarInsn(ASTORE, 3);
        bootstrapMethod.visitVarInsn(ALOAD, 3);
        bootstrapMethod.visitInsn(ICONST_0);
        bootstrapMethod.visitInsn(AALOAD);
        bootstrapMethod.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
        bootstrapMethod.visitVarInsn(ASTORE, 4);
        bootstrapMethod.visitVarInsn(ALOAD, 3);
        bootstrapMethod.visitInsn(ICONST_1);
        bootstrapMethod.visitInsn(AALOAD);
        bootstrapMethod.visitVarInsn(ASTORE, 5);
        bootstrapMethod.visitVarInsn(ALOAD, 3);
        bootstrapMethod.visitInsn(ICONST_2);
        bootstrapMethod.visitInsn(AALOAD);
        bootstrapMethod.visitLdcInsn(Type.getType("L" + classNode.name + ";"));
        bootstrapMethod.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;", false);
        bootstrapMethod.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType", "fromMethodDescriptorString", "(Ljava/lang/String;Ljava/lang/ClassLoader;)Ljava/lang/invoke/MethodType;", false);
        bootstrapMethod.visitVarInsn(ASTORE, 6);
        bootstrapMethod.visitInsn(ACONST_NULL);
        bootstrapMethod.visitVarInsn(ASTORE, 7);
        bootstrapMethod.visitVarInsn(ALOAD, 3);
        bootstrapMethod.visitInsn(ICONST_3);
        bootstrapMethod.visitInsn(AALOAD);
        bootstrapMethod.visitVarInsn(ASTORE, 8);
        bootstrapMethod.visitInsn(ICONST_M1);
        bootstrapMethod.visitVarInsn(ISTORE, 9);
        bootstrapMethod.visitVarInsn(ALOAD, 8);
        bootstrapMethod.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
        Label l3 = new Label();
        Label l4 = new Label();
        bootstrapMethod.visitLookupSwitchInsn(l4, new int[]{466165515}, new Label[]{l3});
        bootstrapMethod.visitLabel(l3);
        bootstrapMethod.visitFrame(Opcodes.F_FULL, 10, new Object[]{"java/lang/invoke/MethodHandles$Lookup", "java/lang/String", "java/lang/invoke/MethodType", "[Ljava/lang/String;", "java/lang/Class", "java/lang/String", "java/lang/invoke/MethodType", "java/lang/invoke/MethodHandle", "java/lang/String", Opcodes.INTEGER}, 0, new Object[]{});
        bootstrapMethod.visitVarInsn(ALOAD, 8);
        bootstrapMethod.visitLdcInsn("virtual");
        bootstrapMethod.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
        bootstrapMethod.visitJumpInsn(IFEQ, l4);
        bootstrapMethod.visitInsn(ICONST_0);
        bootstrapMethod.visitVarInsn(ISTORE, 9);
        bootstrapMethod.visitLabel(l4);
        bootstrapMethod.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        bootstrapMethod.visitVarInsn(ILOAD, 9);
        Label l5 = new Label();
        Label l6 = new Label();
        bootstrapMethod.visitLookupSwitchInsn(l6, new int[]{0}, new Label[]{l5});
        bootstrapMethod.visitLabel(l5);
        bootstrapMethod.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        bootstrapMethod.visitVarInsn(ALOAD, 0);
        bootstrapMethod.visitVarInsn(ALOAD, 4);
        bootstrapMethod.visitVarInsn(ALOAD, 5);
        bootstrapMethod.visitVarInsn(ALOAD, 6);
        bootstrapMethod.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findVirtual", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
        bootstrapMethod.visitVarInsn(ASTORE, 7);
        Label l7 = new Label();
        bootstrapMethod.visitJumpInsn(GOTO, l7);
        bootstrapMethod.visitLabel(l6);
        bootstrapMethod.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        bootstrapMethod.visitVarInsn(ALOAD, 0);
        bootstrapMethod.visitVarInsn(ALOAD, 4);
        bootstrapMethod.visitVarInsn(ALOAD, 5);
        bootstrapMethod.visitVarInsn(ALOAD, 6);
        bootstrapMethod.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findStatic", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
        bootstrapMethod.visitVarInsn(ASTORE, 7);
        bootstrapMethod.visitLabel(l7);
        bootstrapMethod.visitFrame(Opcodes.F_CHOP, 2, null, 0, null);
        bootstrapMethod.visitTypeInsn(NEW, "java/lang/invoke/ConstantCallSite");
        bootstrapMethod.visitInsn(DUP);
        bootstrapMethod.visitVarInsn(ALOAD, 7);
        bootstrapMethod.visitMethodInsn(INVOKESPECIAL, "java/lang/invoke/ConstantCallSite", "<init>", "(Ljava/lang/invoke/MethodHandle;)V", false);
        bootstrapMethod.visitLabel(l1);
        bootstrapMethod.visitInsn(ARETURN);
        bootstrapMethod.visitLabel(l2);
        bootstrapMethod.visitFrame(Opcodes.F_FULL, 3, new Object[]{"java/lang/invoke/MethodHandles$Lookup", "java/lang/String", "java/lang/invoke/MethodType"}, 1, new Object[]{"java/lang/Exception"});
        bootstrapMethod.visitVarInsn(ASTORE, 3);
        bootstrapMethod.visitVarInsn(ALOAD, 3);
        bootstrapMethod.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false);
        bootstrapMethod.visitInsn(ACONST_NULL);
        bootstrapMethod.visitInsn(ARETURN);
        bootstrapMethod.visitMaxs(4, 10);
        bootstrapMethod.visitEnd();

        classNode.methods.add(bootstrapMethod);
    }

}