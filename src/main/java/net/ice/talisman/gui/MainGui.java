package net.ice.talisman.gui;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiTableFlags;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.gui.Gui;
import net.ice.talisman.io.Drive;
import net.ice.talisman.util.FileUtil;

import java.io.File;

import static imgui.ImGui.*;
import static imgui.flag.ImGuiStyleVar.Alpha;
import static imgui.flag.ImGuiTableFlags.*;

public class MainGui implements Gui {

    private final FilePickerGUI filePickerGUI;

    private boolean borders = true;
    private boolean bordersOuter = true;
    private boolean bordersOuterV = true;
    private boolean bordersOuterH = true;
    private boolean bordersH = true;
    private boolean bordersV = true;
    private boolean resizeable = true;
    private boolean noHostExtendX = true;
    private boolean sizingFixedFit = false;
    private boolean contextMenuInBody = true;

    private boolean openFilePicker = false;

    public MainGui() {
        this.filePickerGUI = new FilePickerGUI();
    }

    @Override
    public void draw() {
        newFrame();
        setNextWindowPos(0, 0, ImGuiCond.Always);

        mainBar();
        fileWindow();

        showDemoWindow();
        endFrame();
        render();
    }

    @Override
    public boolean input(RelicApplication relicApplication) {
        return false;
    }

    private void mainBar() {
        if(beginMainMenuBar()) {

            if(beginMenu("File")) {
                if(menuItem("New")) {
                }
                if(menuItem("Open")) { openFilePicker = true; }
                if(beginMenu("Open Recent")) {
                    menuItem("test.java");
                    endMenu();
                }

                if(menuItem("Save")) {}
                if(menuItem("Save As...")) {}

                endMenu();
            }
            if(beginMenu("Settings")) {
                borders = checkbox("Borders", borders) != borders;
                bordersOuter = checkbox("Borders Outer", bordersOuter) != bordersOuter;
                bordersOuterV = checkbox("Borders Outer V", bordersOuterV) != bordersOuterV;
                bordersOuterH = checkbox("Borders Outer H", bordersOuterH) != bordersOuterH;
                bordersH = checkbox("Borders H", bordersH) != bordersH;
                bordersV = checkbox("Borders V", bordersV) != bordersV;
                resizeable = checkbox("Resizeable", resizeable) != resizeable;
                noHostExtendX = checkbox("No Host Extend X", noHostExtendX) != noHostExtendX;
                sizingFixedFit = checkbox("Sizing Fixed Fit", sizingFixedFit) != sizingFixedFit;
                contextMenuInBody = checkbox("Context Menu In Body", contextMenuInBody) != contextMenuInBody;

                endMenu();
            }
            endMainMenuBar();
        }
    }

    private int calculateFlags() {
        int flags = 0;

        flags = flags | (borders ? Borders : 0);
        flags = flags | (bordersOuter ? BordersOuter : 0);
        flags = flags | (bordersOuterV ? BordersOuterV : 0);
        flags = flags | (bordersOuterH ? BordersOuterH : 0);
        flags = flags | (bordersH ? BordersH : 0);
        flags = flags | (bordersV ? BordersV : 0);
        flags = flags | (resizeable ? Resizable : 0);
        flags = flags | (noHostExtendX ? NoHostExtendX : 0);
        flags = flags | (sizingFixedFit ? SizingFixedFit : 0);
        flags = flags | (contextMenuInBody ? ContextMenuInBody : 0);

        return flags;
    }

    private void fileWindow() {
        if(openFilePicker) {
            filePickerGUI.onOpen();
            filePickerGUI.draw(calculateFlags());
        }
    }
}
