/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27.jobf.utils;

import me.superblaubeere27.jobf.processors.name.ClassWrapper;

import java.util.HashSet;
import java.util.Set;

/**
 * Used to keep information on the hierarchy of all loaded classes.
 *
 * @author ItzSomebody
 */
public class ClassTree {
    /**
     * Attached ClassWrapper.
     */
    public ClassWrapper classWrapper;

    /**
     * Names of classes this represented class inherits from.
     */
    public Set<String> parentClasses = new HashSet<>();

    /**
     * Names of classes this represented class is inherited by.
     */
    public Set<String> subClasses = new HashSet<>();

    /**
     * If one of the super-classes is missing this is set to true.
     * It prevents methods from being remapped.
     */
    public boolean missingSuperClass;

    /**
     * Creates a ClassTree object.
     *
     * @param classWrapper the ClassWraper attached to this ClassTree.
     */
    public ClassTree(ClassWrapper classWrapper) {
        this.classWrapper = classWrapper;
    }
}