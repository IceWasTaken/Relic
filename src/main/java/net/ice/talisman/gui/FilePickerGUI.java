package net.ice.talisman.gui;

import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import net.ice.talisman.io.Drive;
import net.ice.talisman.util.FileUtil;

import java.io.File;
import java.util.List;

import static imgui.ImGui.*;
import static imgui.flag.ImGuiCond.FirstUseEver;
import static imgui.flag.ImGuiTableFlags.*;

public class FilePickerGUI {

    private File selectedFile = null;
    private File currentDirectory = new File(System.getProperty("user.home"));

    public void onOpen() {
        selectedFile = null;
    }

    public void draw(int flags) {
        setNextWindowSize(new ImVec2(750, 440));
        if (begin("Choose A File", new ImBoolean(true))) {
            if(beginChild("left pane", new ImVec2(150, 0))) {
                if (beginTable("table1", 1, flags)) {
                    tableSetupColumn("Drives");
                    tableHeadersRow();

                    for (Drive drive : FileUtil.getDrives()) {
                        tableNextRow();
                        tableSetColumnIndex(0);

                        if (menuItem(drive.getDriveName())) {
                            currentDirectory = drive.getRootDirectory();
                        }
                    }

                    endTable();
                }

                endChild();
            }

            sameLine();

            if (currentDirectory.exists() && currentDirectory.isDirectory()) {

                if(beginChild("files")) {
                    if (beginTable("fileTable", 1, flags | ImGuiWindowFlags.HorizontalScrollbar)) {
                        tableSetupColumn("Files");
                        tableHeadersRow();

                        displayFilesInDirectory(currentDirectory);

                        endTable();
                    }

                    endChild();
                }



            }

            if (currentDirectory != null && currentDirectory.getParentFile() != null) {
                if (button("Back")) {
                    currentDirectory = currentDirectory.getParentFile();
                }
            }

            if (selectedFile != null) {
                text("Selected File: " + selectedFile.getName());
            }
            end();
        }


    }

    private void displayFilesInDirectory(File currentDirectory) {
        for (File file : currentDirectory.listFiles()) {
            if (file != null && !file.isHidden()) {
                tableNextRow();
                tableSetColumnIndex(0);
                if (menuItem(file.getName())) {
                    if (file.isDirectory()) {
                        this.currentDirectory = file;
                    } else {
                        this.selectedFile = file;
                    }
                }
            }
        }
    }
}

