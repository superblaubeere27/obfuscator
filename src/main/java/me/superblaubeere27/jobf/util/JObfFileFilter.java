package me.superblaubeere27.jobf.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class JObfFileFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        return true;
    }

    @Override
    public String getDescription() {
        return "JObf files (*.jocfg)";
    }
}
