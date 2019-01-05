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

import org.objectweb.asm.tree.MethodNode;

/**
 * Wrapper for MethodNodes.
 *
 * @author ItzSomebody
 */
public class MethodWrapper {
    /**
     * Owner of the method this MethodWrapper represents.
     */
    public ClassWrapper owner;
    /**
     * Attached MethodNode.
     */
    MethodNode methodNode;
    /**
     * Original method name;
     */
    String originalName;

    /**
     * Original method description.
     */
    String originalDescription;

    /**
     * Creates a MethodWrapper object.
     *
     * @param methodNode          the {@link MethodNode} this wrapper represents.
     * @param owner               the owner of this represented method.
     * @param originalName        the original method name.
     * @param originalDescription the original method description.
     */
    MethodWrapper(MethodNode methodNode, ClassWrapper owner, String originalName, String originalDescription) {
        this.methodNode = methodNode;
        this.owner = owner;
        this.originalName = originalName;
        this.originalDescription = originalDescription;
    }
}