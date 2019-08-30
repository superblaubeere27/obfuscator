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
import me.superblaubeere27.jobf.utils.NameUtils;
import me.superblaubeere27.jobf.utils.values.BooleanValue;
import me.superblaubeere27.jobf.utils.values.DeprecationLevel;
import me.superblaubeere27.jobf.utils.values.EnabledValue;
import me.superblaubeere27.jobf.utils.values.StringValue;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LineNumberRemover implements IClassTransformer {
    private static final String PROCESSOR_NAME = "LineNumberRemover";
    private static Random random = new Random();
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

    private JObfImpl inst;
    private EnabledValue enabled = new EnabledValue(PROCESSOR_NAME, DeprecationLevel.GOOD, true);
    private BooleanValue renameValues = new BooleanValue(PROCESSOR_NAME, "Rename local variables", DeprecationLevel.GOOD, true);
    private BooleanValue removeLineNumbers = new BooleanValue(PROCESSOR_NAME, "Remove Line Numbers", DeprecationLevel.GOOD, true);
    private BooleanValue removeDebugNames = new BooleanValue(PROCESSOR_NAME, "Remove Debug Names", DeprecationLevel.GOOD, true);
    private BooleanValue addLocalVariables = new BooleanValue(PROCESSOR_NAME, "Add Local Variables", "Adds random local variables with wrong types. Might break some decompilers", DeprecationLevel.GOOD, true);
    private StringValue newSourceFileName = new StringValue(PROCESSOR_NAME, "New SourceFile Name", DeprecationLevel.GOOD, "");

    public LineNumberRemover(JObfImpl inst) {
        this.inst = inst;
    }

    @Override
    public void process(ProcessorCallback callback, ClassNode node) {
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
        if ((node.sourceFile == null || !node.sourceFile.contains(StringEncryptionTransformer.MAGICNUMBER_START)) && removeDebugNames.getObject()) {
            node.sourceFile = newSourceFileName.getObject().isEmpty() ? null : newSourceFileName.getObject();
        }

        inst.setWorkDone();
    }
	
    @Override
    public ObfuscationTransformer getType() {
        return ObfuscationTransformer.LINE_NUMBER_REMOVER;
    }

}
