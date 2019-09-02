/*
 * Copyright (c) 2017-2019 superblaubeere27, Sam Sun, MarcoMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.superblaubeere27.jobf;

import me.superblaubeere27.jobf.processors.name.ClassWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ObfuscatorClassLoader extends ClassLoader {
    public static ObfuscatorClassLoader INSTANCE = new ObfuscatorClassLoader();

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String internalName = name.replace('.', '/');

        if (JObfImpl.INSTANCE.getClassPath().containsKey(internalName)) {
            ClassWrapper classWrapper = JObfImpl.INSTANCE.getClassPath().get(internalName);

            if (classWrapper == null || classWrapper.originalClass == null)
                throw new ClassNotFoundException(name);

            try {
                return defineClass(name, classWrapper.originalClass, 0, classWrapper.originalClass.length);
            } catch (ClassFormatError classFormatError) {
                classFormatError.printStackTrace();
                try {
                    Files.write(new File("A:/invalid.class").toPath(), classWrapper.originalClass);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return super.findClass(name);
    }
}
