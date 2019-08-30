/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27.jobf.processors.name;

import me.superblaubeere27.annotations.ObfuscationTransformer;
import me.superblaubeere27.jobf.IClassTransformer;
import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.ProcessorCallback;
import me.superblaubeere27.jobf.utils.NameUtils;
import me.superblaubeere27.jobf.utils.values.BooleanValue;
import me.superblaubeere27.jobf.utils.values.DeprecationLevel;
import me.superblaubeere27.jobf.utils.values.EnabledValue;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class InnerClassRemover implements INameObfuscationProcessor, IClassTransformer {
    private static final String PROCESSOR_NAME = "InnerClassRemover";
    private static Pattern innerClasses = Pattern.compile(".*[A-Za-z0-9]+\\$[0-9]+");
    private EnabledValue enabled = new EnabledValue(PROCESSOR_NAME, DeprecationLevel.GOOD, true);
    private BooleanValue remap = new BooleanValue(PROCESSOR_NAME, "Remap", DeprecationLevel.OK, false);
    private BooleanValue removeMetadata = new BooleanValue(PROCESSOR_NAME, "Remove Metadata", DeprecationLevel.GOOD, true);

    @Override
    public void transformPost(JObfImpl inst, HashMap<String, ClassNode> nodes) {
        if (!enabled.getObject() || !remap.getObject()) return;

        final List<ClassNode> classNodes = new ArrayList<>(JObfImpl.classes.values());

        final Map<String, ClassNode> updatedClasses = new HashMap<>();
        final CustomRemapper remapper = new CustomRemapper();

        for (ClassNode classNode : classNodes) {
            if (innerClasses.matcher(classNode.name).matches()) {
                String newName;

                if (classNode.name.contains("/")) {
                    String packageName = classNode.name.substring(0, classNode.name.lastIndexOf('/'));
                    newName = packageName + "/" + NameUtils.generateClassName(packageName);
                } else newName = NameUtils.generateClassName();

                String mappedName;


                do {
                    mappedName = newName;
                } while (!remapper.map(classNode.name, mappedName));
            }
        }

        for (final ClassNode classNode : classNodes) {
            JObfImpl.classes.remove(classNode.name + ".class");

            ClassNode newNode = new ClassNode();
            ClassRemapper classRemapper = new ClassRemapper(newNode, remapper);
            classNode.accept(classRemapper);

//            if (!classNode.name.equals(newNode.name))
//                Fume.fume.obfuscator.classTransforms.put(classNode.name, newNode.name);

            updatedClasses.put(newNode.name + ".class", newNode);
        }

        updatedClasses.forEach((s, classNode) -> JObfImpl.classes.put(s, classNode));
    }

    @Override
    public void process(ProcessorCallback callback, ClassNode node) {
        if (!enabled.getObject() || !removeMetadata.getObject()) return;

        node.outerClass = null;
        node.innerClasses.clear();

        node.outerMethod = null;
        node.outerMethodDesc = null;
    }

    @Override
    public ObfuscationTransformer getType() {
        return ObfuscationTransformer.INNER_CLASS_REMOVER;
    }
}
