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
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class CrasherTransformer implements IClassTransformer {
    private static final String EMPTY_STRINGS;

    static {
        StringBuilder stringBuilder = new StringBuilder();

        for (int j = 0; j < 50000; j++) {
            stringBuilder.append("\n");
        }

        EMPTY_STRINGS = stringBuilder.toString();
    }

    private EnabledValue enabled = new EnabledValue("Crasher", DeprecationLevel.GOOD, false);
    private BooleanValue invalidSignatures = new BooleanValue("Crasher", "Invalid Signatures", "Adds invalid signatures", DeprecationLevel.GOOD, true);
    private BooleanValue emptyAnnotation = new BooleanValue("Crasher", "Empty annotation spam", "Adds annotations which are repeated newline", DeprecationLevel.GOOD, true);
    private JObfImpl inst;

    public CrasherTransformer(JObfImpl inst) {
        this.inst = inst;
    }

    @Override
    public void process(ProcessorCallback callback, ClassNode node) {
        if (Modifier.isInterface(node.access)) return;
        if (!enabled.getObject()) return;

        if (invalidSignatures.getObject()) {
            /*
             * By ItzSomebody
             */
            if (node.signature == null) {
                node.signature = NameUtils.crazyString(10);
            }
        }

        if (emptyAnnotation.getObject()) {
            node.methods.forEach(method -> {

                if (method.invisibleAnnotations == null)
                    method.invisibleAnnotations = new ArrayList<>();

                for (int i = 0; i < 50; i++) {
                    method.invisibleAnnotations.add(new AnnotationNode(EMPTY_STRINGS));
                }
            });
        }

        inst.setWorkDone();
    }

    @Override
    public ObfuscationTransformer getType() {
        return ObfuscationTransformer.CRASHER;
    }


}