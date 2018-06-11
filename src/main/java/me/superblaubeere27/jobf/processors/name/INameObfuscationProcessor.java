package me.superblaubeere27.jobf.processors.name;

import me.superblaubeere27.jobf.JObfImpl;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;

public interface INameObfuscationProcessor {
    void transformPost(JObfImpl inst, HashMap<String, ClassNode> nodes);
}
