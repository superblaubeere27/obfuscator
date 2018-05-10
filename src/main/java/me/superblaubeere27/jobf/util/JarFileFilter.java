package me.superblaubeere27.jobf.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class JarFileFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        String name = f.getName();
        return f.isDirectory() || name.endsWith(".jar") || name.endsWith(".zip");
    }

    @Override
    public String getDescription() {
        return "Java Archives (*.jar/*.zip)";
    }
}
