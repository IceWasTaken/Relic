package net.ice.talisman.io;

import org.tinylog.Logger;

import java.io.File;
import java.util.List;

public class FileExplorer {

    private List<File> drives;

    public FileExplorer() {
        this.drives = List.of(File.listRoots());

    }

    public List<File> getFilesInDirectory(String s) {
        File dir = new File(s);

        if(!dir.isDirectory()) {
            Logger.warn("String provided is not a directory: " + s);
            return List.of();
        }

        return List.of(dir.listFiles());
    }
}
