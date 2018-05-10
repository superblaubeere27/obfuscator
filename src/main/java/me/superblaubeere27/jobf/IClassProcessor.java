package me.superblaubeere27.jobf;

import org.objectweb.asm.tree.ClassNode;

public interface IClassProcessor {
    void process(ClassNode node, int mode);
}