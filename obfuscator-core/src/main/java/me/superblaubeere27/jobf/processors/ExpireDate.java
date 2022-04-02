package me.superblaubeere27.jobf.processors;

import me.superblaubeere27.annotations.ObfuscationTransformer;
import me.superblaubeere27.jobf.IClassTransformer;
import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.ProcessorCallback;
import me.superblaubeere27.jobf.utils.values.DeprecationLevel;
import me.superblaubeere27.jobf.utils.values.EnabledValue;
import me.superblaubeere27.jobf.utils.values.NumberValue;
import me.superblaubeere27.jobf.utils.values.StringValue;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class ExpireDate implements IClassTransformer {

    private static final String PROCESSOR_NAME = "ExpireDate";

    private JObfImpl inst;
    private EnabledValue enabled = new EnabledValue(PROCESSOR_NAME, DeprecationLevel.GOOD, true);
    private StringValue expireDate = new StringValue(PROCESSOR_NAME, "Exprire Date in format DD\\MM\\YYYY", DeprecationLevel.GOOD,
            "01\\01\\2023");
    private StringValue message = new StringValue(PROCESSOR_NAME, "Message", DeprecationLevel.GOOD,
            "Expired!");
    private NumberValue<Integer> chance = new NumberValue<>(PROCESSOR_NAME, "Insert Chance", DeprecationLevel.GOOD,
            30);
    private NumberValue<Integer> dayVariation = new NumberValue<>(PROCESSOR_NAME, "Day variation", DeprecationLevel.GOOD,
            3);

    private static Random random = new Random();

    private final List<String> exceptions = Arrays.asList(
            "java/lang/ClassNotFoundException",
            "java/lang/IllegalAccessException",
            "java/lang/IllegalArgumentException",
            "java/lang/NullPointerException",
            "java/lang/NumberFormatException",
            "java/lang/UnsupportedOperationException",
            "java/lang/ClassCastException");


    public ExpireDate(JObfImpl inst) {
        this.inst = inst;
    }

    @Override
    public void process(ProcessorCallback callback, ClassNode node) {
        if (!enabled.getObject()) return;

        for (MethodNode method: node.methods) {
            if (((method.access & Opcodes.ACC_ABSTRACT) != 0) ||
                    ((method.access & Opcodes.ACC_INTERFACE) != 0) ||
                    method.name.equals("main") ||
                    method.name.equals("<init>") ||
                    method.name.equals("<clinit>")) {
                continue;
            }

            callback.setForceComputeFrames();
            if (random.nextInt(100) < chance.getObject()) {
                return;
            }
            try {
                method.instructions.insert(getInstructions());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private InsnList getInstructions() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime((new SimpleDateFormat("dd\\MM\\yyyy")).parse(expireDate.getObject()));
        calendar.add(Calendar.DATE, random.nextInt(dayVariation.getObject()));
        long expireDateLong = calendar.getTimeInMillis();

        String exception = exceptions.get(random.nextInt(exceptions.size()));

        InsnList instructions = new InsnList();
        instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/Date"));
        instructions.add(new InsnNode(Opcodes.DUP));
        instructions.add(new LdcInsnNode(expireDateLong));
        instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/Date", "<init>", "(J)V",false));
        instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/Date"));
        instructions.add(new InsnNode(Opcodes.DUP));
        instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/Date", "<init>", "()V", false));
        instructions.add(new InsnNode(Opcodes.SWAP));
        instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Date", "after", "(Ljava/util/Date;)Z", false));
        LabelNode label = new LabelNode();
        instructions.add(new JumpInsnNode(Opcodes.IFEQ, label));

        instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/Date"));
        instructions.add(new InsnNode(Opcodes.DUP));
        instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/Date", "<init>", "()V", false));
        instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Date", "getTime", "()J", false));
        instructions.add(new LdcInsnNode(2L));
        instructions.add(new InsnNode(Opcodes.LREM));
        instructions.add(new InsnNode(Opcodes.LCONST_0));
        instructions.add(new InsnNode(Opcodes.LCMP));
        instructions.add(new JumpInsnNode(Opcodes.IFNE, label));

        instructions.add(new TypeInsnNode(Opcodes.NEW, exception));
        instructions.add(new InsnNode(Opcodes.DUP));
        instructions.add(new LdcInsnNode(message.getObject()));
        instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, exception, "<init>", "(Ljava/lang/String;)V", false));
        instructions.add(new InsnNode(Opcodes.ATHROW));
        instructions.add(label);
        return instructions;
    }

    @Override
    public ObfuscationTransformer getType() {
        return ObfuscationTransformer.EXPIREDATE;
    }


}
