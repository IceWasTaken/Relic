package net.ice.talisman.util;

import net.ice.talisman.io.Drive;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//windows only for now
public class FileUtil {

    public static List<Drive> getDrives() {
        List<Drive> drives = new ArrayList<>();
        File[] paths = File.listRoots();

        for (File path : paths) {
            drives.add(new Drive(path));
        }

        return drives;
    }

    public static void listDrives() {
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();

        for(Path file : FileSystems.getDefault().getRootDirectories()) {
            System.out.println(file);
        }

    }


}
