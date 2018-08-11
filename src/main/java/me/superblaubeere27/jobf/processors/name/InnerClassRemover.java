package me.superblaubeere27.jobf.processors.name;

import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.util.values.DeprecationLevel;
import me.superblaubeere27.jobf.util.values.EnabledValue;
import me.superblaubeere27.jobf.utils.NameUtils;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class InnerClassRemover implements INameObfuscationProcessor {
    private static final String PROCESSOR_NAME = "InnerClassRemover";
    private static Pattern innerClasses = Pattern.compile(".*[A-Za-z0-9]+\\$[0-9]+");
    private EnabledValue enabled = new EnabledValue(PROCESSOR_NAME, DeprecationLevel.OK, false);

    @Override
    public void transformPost(JObfImpl inst, HashMap<String, ClassNode> nodes) {
        if (!enabled.getObject()) return;

        final List<ClassNode> classNodes = new ArrayList<>(JObfImpl.classes.values());

        final Map<String, ClassNode> updatedClasses = new HashMap<>();
        final CustomRemapper remapper = new CustomRemapper();

        for (ClassNode classNode : classNodes) {
            if (innerClasses.matcher(classNode.name).matches()) {
                String newName = null;

                if (classNode.name.contains("/")) {
                    String packageName = classNode.name.substring(0, classNode.name.lastIndexOf('/'));
                    newName = packageName + "/" + NameUtils.generateClassName(packageName);
                }

                if (newName == null) NameUtils.generateClassName();

                String mappedName;

                do {
                    mappedName = newName;
                } while (!remapper.map(classNode.name, mappedName));

                System.out.println(classNode.name + ": " + Modifier.toString(classNode.access) + " (" + classNode.access + ")");
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
}
