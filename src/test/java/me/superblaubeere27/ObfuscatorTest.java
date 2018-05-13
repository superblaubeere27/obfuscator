package me.superblaubeere27;

import com.google.common.io.ByteStreams;
import me.superblaubeere27.jobf.JObfImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.junit.Assert.*;

public class ObfuscatorTest {
    private static File obfuscatedFile = null;

    @BeforeClass
    public static void obfuscate() {
        try {
            File input = File.createTempFile("obf_", ".jar");
//            Files.copy(new URL("https://github.com/SB27Team/JavaFeatureTest/raw/master/JavaFeatureTest.jar").openStream(), input.toPath());
            ByteStreams.copy(new URL("https://github.com/SB27Team/JavaFeatureTest/raw/master/JavaFeatureTest.jar").openStream(), new FileOutputStream(input));
            JObfImpl impl = new JObfImpl();
            impl.addProcessors();
            impl.processJar(input.getAbsolutePath(), (obfuscatedFile = File.createTempFile("obf_", ".jar")).getAbsolutePath(), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void removeFile() {
        if (obfuscatedFile != null && obfuscatedFile.exists()) {
            obfuscatedFile.delete();
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
        Class c = null;

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
