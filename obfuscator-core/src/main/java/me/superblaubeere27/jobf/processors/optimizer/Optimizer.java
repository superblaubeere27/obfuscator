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

import me.superblaubeere27.annotations.ObfuscationTransformer;
import me.superblaubeere27.jobf.IClassTransformer;
import me.superblaubeere27.jobf.ProcessorCallback;
import me.superblaubeere27.jobf.utils.values.BooleanValue;
import me.superblaubeere27.jobf.utils.values.DeprecationLevel;
import me.superblaubeere27.jobf.utils.values.EnabledValue;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class Optimizer implements IClassTransformer {
    private static final String PROCESSOR_NAME = "Optimizer";
    private EnabledValue enabledValue = new EnabledValue(PROCESSOR_NAME, DeprecationLevel.OK, false);
    private BooleanValue replaceEquals = new BooleanValue(PROCESSOR_NAME, "Replace String.equals()", "NOT TESTED", DeprecationLevel.OK, false);
    private BooleanValue replaceEqualsIgnoreCase = new BooleanValue(PROCESSOR_NAME, "Replace String.equalsIgnoreCase()", "Might break some comparisons with strings that contains unicode chars", DeprecationLevel.OK, false);
    private BooleanValue optimizeStringCalls = new BooleanValue(PROCESSOR_NAME, "Optimize static string calls", null, DeprecationLevel.GOOD, false);

    @Override
    public void process(ProcessorCallback callback, ClassNode node) {
        if (!enabledValue.getObject()) return;

        for (MethodNode method : node.methods) {
            if (replaceEquals.getObject() || replaceEqualsIgnoreCase.getObject())
                ComparisionReplacer.replaceComparisons(method, replaceEquals.getObject(), replaceEqualsIgnoreCase.getObject());
            if (optimizeStringCalls.getObject()) StaticStringCallOptimizer.optimize(method);
        }
    }

    @Override
    public ObfuscationTransformer getType() {
        return ObfuscationTransformer.PEEPHOLE_OPTIMIZER;
    }

}
