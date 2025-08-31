package net.ice.talisman.test;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.Arrays;

public class FSTest {

    public static void main(String[] args) {
        File[] paths;
        FileSystemView fsv = FileSystemView.getFileSystemView();

        paths = File.listRoots();

        for(File path : paths)
        {
            // prints file and directory paths
            System.out.println("Drive Name: "+path);
            System.out.println("Description: "+fsv.getSystemTypeDescription(path));
            System.out.println("Test:" + fsv.getSystemDisplayName(path));
            System.out.println("Test2: " + Arrays.toString(fsv.getRoots()));
            System.out.println("Test3:" + Arrays.toString(fsv.getChooserShortcutPanelFiles()));
        }
    }
}
