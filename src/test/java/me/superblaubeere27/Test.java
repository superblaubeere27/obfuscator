package me.superblaubeere27;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Test {

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        File obfuscatedFile;
        JarFile jarFile = new JarFile(obfuscatedFile = new File("helloWorld-obf.jar"));
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
            System.out.println(className);
            if (className.equals("Test")) {
                c = cl.loadClass(className);
            }
        }
        Objects.requireNonNull(c);
    }

}
