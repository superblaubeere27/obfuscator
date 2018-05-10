package me.superblaubeere27.jobf.processors;

import me.superblaubeere27.jobf.IClassProcessor;
import me.superblaubeere27.jobf.JObfImpl;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collections;
import java.util.Random;

public class ShuffleMembersProcessor implements IClassProcessor {
    private static Random random = new Random();
    private JObfImpl inst;

    public ShuffleMembersProcessor(JObfImpl inst) {
        this.inst = inst;
    }

    @Override
    public void process(ClassNode node, int mode) {
        Collections.shuffle(node.methods, random);
        Collections.shuffle(node.fields, random);
        Collections.shuffle(node.innerClasses, random);
        Collections.shuffle(node.interfaces, random);
        if (node.invisibleAnnotations != null) Collections.shuffle(node.invisibleAnnotations, random);
        if (node.visibleAnnotations != null) Collections.shuffle(node.visibleAnnotations, random);
        if (node.invisibleTypeAnnotations != null) Collections.shuffle(node.invisibleTypeAnnotations, random);

        for (Object o : node.methods.toArray()) {
            if (o instanceof MethodNode) {
                MethodNode method = (MethodNode) o;
                if (method.invisibleAnnotations != null) Collections.shuffle(method.invisibleAnnotations, random);
                if (method.invisibleLocalVariableAnnotations != null)
                    Collections.shuffle(method.invisibleLocalVariableAnnotations, random);
                if (method.invisibleTypeAnnotations != null)
                    Collections.shuffle(method.invisibleTypeAnnotations, random);
                if (method.visibleAnnotations != null) Collections.shuffle(method.visibleAnnotations, random);
                if (method.visibleLocalVariableAnnotations != null)
                    Collections.shuffle(method.visibleLocalVariableAnnotations, random);
                if (method.visibleTypeAnnotations != null) Collections.shuffle(method.visibleTypeAnnotations, random);

                Collections.shuffle(method.exceptions, random);
                if (method.localVariables != null) {
                    Collections.shuffle(method.localVariables, random);
                }
                if (method.parameters != null) Collections.shuffle(method.parameters, random);
            }
        }
        for (Object o : node.methods.toArray()) {
            if (o instanceof FieldNode) {
                FieldNode method = (FieldNode) o;
                if (method.invisibleAnnotations != null) Collections.shuffle(method.invisibleAnnotations, random);
                if (method.invisibleTypeAnnotations != null)
                    Collections.shuffle(method.invisibleTypeAnnotations, random);
                if (method.visibleAnnotations != null) Collections.shuffle(method.visibleAnnotations, random);
                if (method.visibleTypeAnnotations != null) Collections.shuffle(method.visibleTypeAnnotations, random);
            }
        }
        inst.setWorkDone();
    }

}