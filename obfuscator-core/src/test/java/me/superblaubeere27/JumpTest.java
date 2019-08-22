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

import com.google.common.io.Files;
import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.processors.flowObfuscation.FlowObfuscator;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.ModifiedClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JumpTest {
    private Class<?> generatedClass = null;

    @Before
    public void generateClass() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, IOException {
        Class.forName(JObfImpl.class.getCanonicalName());
        InsnList methodInsns = new InsnList();

        ClassNode classNode = new ClassNode();
        classNode.name = "Test";
        classNode.access = Opcodes.ACC_PUBLIC;
        classNode.version = 52;
        classNode.superName = "java/lang/Object";

        MethodNode methodNode = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "test", "()V", null, new String[0]);

        for (int i1 = 0; i1 < 10; i1++) {
            for (int i = 0; i <= 13; i++) {
                LabelNode end = new LabelNode(new Label());
                methodInsns.add(FlowObfuscator.generateIfGoto(i, end));
                methodInsns.add(new TypeInsnNode(Opcodes.NEW, "java/lang/RuntimeException"));
                methodInsns.add(new InsnNode(Opcodes.DUP));
                methodInsns.add(new LdcInsnNode("JumpGenerator row=" + i1 + " id=" + i + " failed"));
                methodInsns.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", false));
                methodInsns.add(new InsnNode(Opcodes.ATHROW));
                methodInsns.add(end);
//            methodInsns.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
            }
        }
        methodInsns.add(new InsnNode(Opcodes.RETURN));


        methodNode.instructions = methodInsns;
        methodNode.visitEnd();


        classNode.methods.add(methodNode);

        ModifiedClassWriter writer = new ModifiedClassWriter(ModifiedClassWriter.COMPUTE_FRAMES);

        classNode.accept(writer);

        writer.visitEnd();

        byte[] bytes = writer.toByteArray();

        Files.write(bytes, File.createTempFile("JumpTestClass", ".class"));

        Class<?> classLoaderClass = ClassLoader.class;

        Method defineClass = classLoaderClass.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
        defineClass.setAccessible(true);

        generatedClass = (Class) defineClass.invoke(getClass().getClassLoader(), bytes, 0, bytes.length);
    }

    @Test
    public void testFlowJumps() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        generatedClass.getMethod("test").invoke(null);
    }

}
