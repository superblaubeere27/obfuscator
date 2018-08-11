package me.superblaubeere27.jobf.processors;

import me.superblaubeere27.jobf.IClassProcessor;
import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.util.values.BooleanValue;
import me.superblaubeere27.jobf.util.values.DeprecationLevel;
import me.superblaubeere27.jobf.util.values.EnabledValue;
import me.superblaubeere27.jobf.util.values.StringValue;
import me.superblaubeere27.jobf.utils.NameUtils;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LineNumberRemover implements IClassProcessor {
    private static Random random = new Random();
    private JObfImpl inst;
    private static final String PROCESSOR_NAME = "LineNumberRemover";
    private static ArrayList<String> TYPES = new ArrayList<>();

    static {
        TYPES.add("Z");
        TYPES.add("C");
        TYPES.add("B");
        TYPES.add("S");
        TYPES.add("I");
        TYPES.add("F");
        TYPES.add("J");
        TYPES.add("D");
        TYPES.add("Ljava/lang/Exception;");
        TYPES.add("Ljava/lang/String;");
    }

    private EnabledValue enabled = new EnabledValue(PROCESSOR_NAME, DeprecationLevel.GOOD, false);
    private BooleanValue renameValues = new BooleanValue(PROCESSOR_NAME, "Rename local variables", DeprecationLevel.GOOD, true);
    private BooleanValue removeLineNumbers = new BooleanValue(PROCESSOR_NAME, "Remove Line Numbers", DeprecationLevel.GOOD, true);
    private BooleanValue removeDebugNames = new BooleanValue(PROCESSOR_NAME, "Remove Debug Names", DeprecationLevel.GOOD, true);
    private BooleanValue addLocalVariables = new BooleanValue(PROCESSOR_NAME, "Add Local Variables", DeprecationLevel.GOOD, true);
    private StringValue newSourceFileName = new StringValue(PROCESSOR_NAME, "New SourceFile Name", DeprecationLevel.GOOD, "");

    public LineNumberRemover(JObfImpl inst) {
        this.inst = inst;
    }

    @Override
    public void process(ClassNode node) {
        if (!enabled.getObject()) return;

        for (MethodNode method : node.methods) {
            LabelNode firstLabel = null;
            LabelNode lastLabel = null;
            HashMap<Integer, String> varMap = new HashMap<>();

            for (AbstractInsnNode abstractInsnNode : method.instructions.toArray()) {
                if (abstractInsnNode instanceof LineNumberNode && removeLineNumbers.getObject()) {
                    LineNumberNode insnNode = (LineNumberNode) abstractInsnNode;
                    method.instructions.remove(insnNode);
                }

                if (abstractInsnNode instanceof VarInsnNode) {
                    VarInsnNode insnNode = (VarInsnNode) abstractInsnNode;

                    if (!varMap.containsKey(insnNode.var)) {
                        varMap.put(insnNode.var, TYPES.get(random.nextInt(TYPES.size())));
                    }
                }
                if (abstractInsnNode instanceof LabelNode) {
                    LabelNode insnNode = (LabelNode) abstractInsnNode;

                    if (firstLabel == null) {
                        firstLabel = insnNode;
                    }

                    lastLabel = insnNode;
                }
            }

            if (firstLabel != null && addLocalVariables.getObject()) {
                if (method.localVariables == null) method.localVariables = new ArrayList<>();

                for (Map.Entry<Integer, String> integerStringEntry : varMap.entrySet()) {
                    method.localVariables.add(new LocalVariableNode(NameUtils.generateLocalVariableName(), integerStringEntry.getValue(), null, firstLabel, lastLabel, integerStringEntry.getKey()));
                }
            }

            if (method.parameters != null && renameValues.getObject()) {
                for (ParameterNode parameter : method.parameters) {
                    parameter.name = NameUtils.generateLocalVariableName();
                }
            }
            if (method.localVariables != null && renameValues.getObject()) {
                for (LocalVariableNode parameter : method.localVariables) {
                    parameter.name = NameUtils.generateLocalVariableName();
                }
            }
        }
        if ((node.sourceFile == null || !node.sourceFile.contains(StringEncryptionProcessor.MAGICNUMBER_START)) && removeDebugNames.getObject()) {
            node.sourceFile = newSourceFileName.getObject().isEmpty() ? null : newSourceFileName.getObject();
        }

        inst.setWorkDone();
    }

}