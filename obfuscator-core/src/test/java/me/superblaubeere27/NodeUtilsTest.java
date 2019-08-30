/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27;

import me.superblaubeere27.jobf.utils.NameUtils;
import me.superblaubeere27.jobf.utils.NodeUtils;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class NodeUtilsTest {
    @Test
    public void test_getPackage() {
        assertEquals(NameUtils.getPackage("className"), "");
        assertEquals(NameUtils.getPackage("package1/className"), "package1");
        assertEquals(NameUtils.getPackage("package1/package2/className"), "package1/package2");

        try {
            NameUtils.getPackage("/className");
            fail();
        } catch (Exception ignored) {
        }
    }

    @Test
    public void test_getWrapperMethod() {
        AbstractInsnNode wrapperMethod = NodeUtils.getWrapperMethod(Type.INT_TYPE);

        if (wrapperMethod instanceof MethodInsnNode) {
            MethodInsnNode method = (MethodInsnNode) wrapperMethod;
            assertEquals("valueOf", method.name);
            assertEquals(method.desc, "(I)Ljava/lang/Integer;");
            assertEquals(method.owner, "java/lang/Integer");
        } else {
            fail();
        }

        assertEquals(NodeUtils.getWrapperMethod(Type.VOID_TYPE).getOpcode(), Opcodes.NOP);
        assertEquals(NodeUtils.getWrapperMethod(Type.getType("Ljava/lang/Object;")).getOpcode(), Opcodes.NOP);
    }
}
