package me.superblaubeere27;

import org.objectweb.asm.tree.ClassNode;

import java.util.Collection;

public interface IPreClassProcessor {
    void process(Collection<ClassNode> node);
}
