package me.superblaubeere27.jobf.utils;

import java.util.HashSet;
import java.util.Set;

public class ClassTree {
    public String thisClass;

    public Set<String> subClasses = new HashSet<>();
    public Set<String> parentClasses = new HashSet<>();

    public ClassTree(String thisClass) {
        this.thisClass = thisClass;
    }
}