package net.ice.talisman.io;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class Drive {

    private File drive;

    public Drive(File file) {
        this.drive = file;
    }

    public Drive(String path) {
        this(new File(path));
    }

    public String getDriveName() {
        return FileSystemView.getFileSystemView().getSystemDisplayName(drive);
    }

    public File getRootDirectory() {
        return drive.getAbsoluteFile();
    }

    public boolean isAccessible() {
        return drive.exists() && drive.canRead();
    }

    public long getTotalSpace() {
        return drive.getTotalSpace();
    }

    public long getFreeSpace() {
        return drive.getFreeSpace();
    }

    public long getUsableSpace() {
        return drive.getUsableSpace();
    }

    public String getDriveType() {
        if (drive.exists()) {
            if (drive.canRead()) {
                return "Local Disk";
            } else {
                return "Unreachable Drive";
            }
        } else {
            return "Invalid Drive";
        }
    }

    public boolean isFloppy() {
        return drive.getAbsolutePath().equals("/dev/fd0");
    }

    @Override
    public String toString() {
        return getDriveName() + " (" + drive.getAbsolutePath() + ")";
    }
}
