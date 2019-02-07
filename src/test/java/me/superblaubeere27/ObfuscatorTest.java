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

import com.google.common.io.ByteStreams;
import me.superblaubeere27.jobf.JObfImpl;
import me.superblaubeere27.jobf.utils.values.ConfigManager;
import me.superblaubeere27.jobf.utils.values.Configuration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.junit.Assert.*;

public class ObfuscatorTest {
    private static File obfuscatedFile = null;
    private static File input;

    @BeforeClass
    public static void obfuscate() {
        try {
            input = File.createTempFile("obf_", ".jar");
//            Files.copy(new URL("https://github.com/SB27Team/JavaFeatureTest/raw/master/JavaFeatureTest.jar").openStream(), input.toPath());
            ByteStreams.copy(new URL("https://github.com/SB27Team/JavaFeatureTest/raw/master/JavaFeatureTest.jar").openStream(), new FileOutputStream(input));
//            impl.addProcessors();

            Configuration configuration = ConfigManager.loadConfig(new String(ByteStreams.toByteArray(ObfuscatorTest.class.getResourceAsStream("/config.jocfg")), StandardCharsets.UTF_8));

            configuration.setInput(input.getAbsolutePath());
            configuration.setOutput((obfuscatedFile = File.createTempFile("obf_", ".jar")).getAbsolutePath());

            JObfImpl.INSTANCE.processJar(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void removeFile() {
        if (obfuscatedFile != null && obfuscatedFile.exists()) {
            obfuscatedFile.delete();
        }
        if (input != null && input.exists()) {
            input.delete();
        }
    }

    @Test
    public void verifyFile() {
        assertNotNull(obfuscatedFile);
        assertTrue("File doesn't exist", obfuscatedFile.exists());
    }

    @Test
    public void testObfuscatedJar() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int rightValue = 704643072;
        JarFile jarFile = new JarFile(obfuscatedFile);
        Enumeration<JarEntry> e = jarFile.entries();

        URL[] urls = {new URL("jar:file:" + obfuscatedFile.getAbsolutePath() + "!/")};
        URLClassLoader cl = URLClassLoader.newInstance(urls);
        Class<?> c = null;

        while (e.hasMoreElements()) {
            JarEntry je = e.nextElement();
            if (je.isDirectory() || !je.getName().endsWith(".class")) {
                continue;
            }
            // -6 because of .class
            String className = je.getName().substring(0, je.getName().length() - 6);
            className = className.replace('/', '.');
            if (className.equals("JFT")) {
                c = cl.loadClass(className);
            }
        }

        if (c == null) {
            fail("JFT.class wasn't found");
        }

        assertEquals(((int) c.getMethod("test").invoke(null)), rightValue);
    }
}
