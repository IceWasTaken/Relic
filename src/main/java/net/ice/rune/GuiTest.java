package net.ice.rune;

import imgui.*;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.*;
import imgui.type.ImString;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.gui.Gui;
import net.ice.rune.console.Console;
import net.ice.rune.console.ConsoleItem;
import net.ice.rune.console.TestCommand;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import static imgui.ImGui.*;
import static imgui.flag.ImGuiCol.Text;
import static imgui.flag.ImGuiColorEditFlags.*;
import static imgui.flag.ImGuiInputTextFlags.*;
import static imgui.flag.ImGuiStyleVar.*;
import static imgui.flag.ImGuiWindowFlags.AlwaysvarResize;
import static net.ice.rune.GuiTest.palette.*;

public class GuiTest implements Gui {

    private boolean autoScroll = true;
    private boolean scrollToBottom = false;
    private boolean coloredOutput = true;
    private boolean filterBar = true;
    private boolean timeStamps = true;

    private boolean wasPreviousFrameTabCompletion = false;

    private int windowAlpha = 1;
    private int historyIndex = -1;

    private ImVec4[] colorPalette = new ImVec4[6];
    private Console console;
    private ImString buffer = new ImString(256); // give it an explicit size

    public GuiTest(Console console) {
        this.console = console;
        console.registerCommand(new TestCommand());
        defaultSettings();
    }

    @Override
    public void draw() {
        newFrame();
        setNextWindowPos(0, 0, ImGuiCond.Always);

        ImGui.pushStyleVar(Alpha, windowAlpha);
        ImGui.popStyleVar();
        menuBar();

        consoleWindow();
        separator();
        inputBar();
        endFrame();
        render();
    }

    @Override
    public boolean input(RelicApplication relicApplication) {
        ImGuiIO imGuiIO = ImGui.getIO();
        Vector2f mousePos = relicApplication.getInput().getMousePosition();
        imGuiIO.addMousePosEvent(mousePos.x, mousePos.y);
        imGuiIO.addMouseButtonEvent(0, relicApplication.getInput().getMouseButtonsDown().contains(GLFW.GLFW_MOUSE_BUTTON_1));
        imGuiIO.addMouseButtonEvent(1, relicApplication.getInput().getMouseButtonsDown().contains(GLFW.GLFW_MOUSE_BUTTON_2));

        return imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
    }

    private void menuBar() {
        if(beginMainMenuBar()) {
            if(beginMenu("Settings")) {
                coloredOutput = checkbox("Colored Output", coloredOutput) != coloredOutput;
                sameLine();
                helpMaker("Enabled colored command output");

                autoScroll = checkbox("Auto Scroll", autoScroll) != autoScroll;
                sameLine();
                helpMaker("Automatically scroll to bottom of console log");

                filterBar = checkbox("Filter Bar", filterBar) != filterBar;
                sameLine();
                helpMaker("Enable console filter bar");

                timeStamps = checkbox("Time Stamps", timeStamps) != timeStamps;
                sameLine();
                helpMaker("Display command execution timestamps");

                if(button("Reset settings", new ImVec2(getColumnWidth(), 0))) {
                    openPopup("Reset Settings?");
                }

                if(beginPopupModal("Reset Settings?", null, AlwaysAutoResize)) {
                    text("All settings will be reset to default. \nThis operation cannot be undone! \n\n");
                    separator();

                    if(button("Reset", new ImVec2(120, 0))) {
                        defaultSettings();
                        closeCurrentPopup();
                    }

                    setItemDefaultFocus();
                    sameLine();
                    if(button("Cancel", new ImVec2(120, 0))) {
                        closeCurrentPopup();
                    }
                    endPopup();
                }
                endMenu();
            }

            if(beginMenu("Appearance")) {
                int flags = Float | AlphaPreview | NoInputs | AlphaBar;

                textUnformatted("Color Palette");
                indent();
                colorEditor("Comand##", colorPalette[COL_COMMAND.ordinal()], flags);
                colorEditor("Log##", colorPalette[COL_LOG.ordinal()], flags);
                colorEditor("Warning##", colorPalette[COL_WARNING.ordinal()], flags);
                colorEditor("Error##", colorPalette[COL_ERROR.ordinal()], flags);
                colorEditor("Info##", colorPalette[COL_INFO.ordinal()], flags);
                colorEditor("Time Stamp##", colorPalette[COL_TIMESTAMP.ordinal()], flags);
                unindent();

                separator();

                textUnformatted("Background");
                sliderFloat("Transparency##", new float[]{windowAlpha}, 0.1f, 1f);

                endMenu();
            }

            if(beginMenu("Scripts")) {
                endMenu();
            }
            endMainMenuBar();
        }
    }

    private void colorEditor(String label, ImVec4 vec4, int flags) {
        float[] data = new float[]{vec4.x, vec4.y, vec4.z, vec4.w};
        colorEdit4(label, data, flags);
        vec4.x = data[0];
        vec4.y = data[1];
        vec4.z = data[2];
        vec4.w = data[3];
    }

    public void inputBar() {
        int flags = ImGuiInputTextFlags.EnterReturnsTrue | ImGuiInputTextFlags.CallbackHistory;

        boolean reclaimFocus = false;

        ImGui.pushItemWidth(-ImGui.getStyle().getItemSpacingX() * 7);
        if (ImGui.inputTextWithHint("##consoleInput", "Enter command...", buffer, flags, consoleCallback)) {
            if (!buffer.isEmpty()) {
                String input = buffer.get();
                console.runCommand(input);
                console.getCommandHistory().add(input);
                scrollToBottom = true;
            }

            reclaimFocus = true;
            buffer.clear();
            historyIndex = -1; // reset history navigation after enter
        }
        ImGui.popItemWidth();

        wasPreviousFrameTabCompletion = false;

        ImGui.setItemDefaultFocus();
        if (reclaimFocus) {
            ImGui.setKeyboardFocusHere(-1); // keep focus in input box
        }
    }

    private void defaultSettings() {
        autoScroll = true;
        scrollToBottom = false;
        coloredOutput = true;
        filterBar = true;
        timeStamps = true;
        colorPalette[COL_COMMAND.ordinal()] = new ImVec4(1f, 1f, 1f, 1f);
        colorPalette[COL_LOG.ordinal()] = new ImVec4(1f, 1f, 1f, 0.5f);
        colorPalette[COL_WARNING.ordinal()] = new ImVec4(1f, 0.87f, 0.37f, 1f);
        colorPalette[COL_ERROR.ordinal()] = new ImVec4(1f, 0.365f, 0.365f, 1f);
        colorPalette[COL_INFO.ordinal()] = new ImVec4(0.46f, 0.96f, 0.46f, 1f);
        colorPalette[COL_TIMESTAMP.ordinal()] = new ImVec4(1f, 1f, 1f, 0.5f);
    }

    private void helpMaker(String desc) {
        ImGui.textDisabled("(?)");
        if(ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.pushTextWrapPos(ImGui.getFontSize() * 35.0f);
            ImGui.textUnformatted(desc);
            ImGui.popTextWrapPos();
            ImGui.endTooltip();
        }
    }

    private void consoleWindow() {
        final float footerHeightToReserve = ImGui.getStyle().getItemSpacingY() + ImGui.getFrameHeightWithSpacing();

        if (ImGui.beginChild("ScrollRegion##", new ImVec2(0, -footerHeightToReserve), false, 0)) {
            final float timestampWidth = ImGui.calcTextSize("00:00:00:0000").x;

            ImGui.pushTextWrapPos(); // Push once for the whole child

            for (ConsoleItem consoleItem : console.getConsoleItems()) {
                if (coloredOutput) {
                    pushStyleColor(Text, colorPalette[consoleItem.getType().ordinal()]);
                    textUnformatted(consoleItem.getData());
                    popStyleColor();
                } else {
                    textUnformatted(consoleItem.getData());
                }

                if (consoleItem.getType() == ConsoleItem.ItemType.COMMAND && timeStamps) {
                    sameLine(getColumnWidth(-1) - timestampWidth);
                    pushStyleColor(Text, colorPalette[COL_TIMESTAMP.ordinal()]);
                    text(String.format("%02d:%02d:%02d:%04d",
                            ((consoleItem.getTimestamp() / 1000 / 3600) % 24),
                            ((consoleItem.getTimestamp() / 1000 / 60) % 60),
                            ((consoleItem.getTimestamp() / 1000) % 60),
                            consoleItem.getTimestamp() % 1000));
                    popStyleColor();
                }
            }

            ImGui.popTextWrapPos();

            if ((scrollToBottom && (getScrollY() >= getScrollMaxY() || autoScroll))) {
                setScrollHereY(1.0f);
            }
            scrollToBottom = false;

            endChild();
        }
    }

    enum palette {
        COL_COMMAND,
        COL_LOG,
        COL_WARNING,
        COL_ERROR,
        COL_INFO,
        COL_TIMESTAMP,
        COL_COUNT
    }



    private final ImGuiInputTextCallback consoleCallback = new ImGuiInputTextCallback() {
        @Override
        public void accept(ImGuiInputTextCallbackData data) {
            switch (data.getEventFlag()) {
                case ImGuiInputTextFlags.CallbackHistory:
                    if (data.getEventKey() == ImGuiKey.UpArrow) {
                        // Previous command
                        if (historyIndex == -1) {
                            historyIndex = console.getCommandHistory().getAll().size() - 1;
                        } else if (historyIndex > 0) {
                            historyIndex--;
                        }
                        if (historyIndex >= 0) {
                            buffer.set(console.getCommandHistory().getAll().get(historyIndex));
                            data.setBuf(buffer.get());
                        }
                    } else if (data.getEventKey() == ImGuiKey.DownArrow) {
                        // Next command
                        if (historyIndex != -1) {
                            historyIndex++;
                            if (historyIndex < console.getCommandHistory().getAll().size()) {
                                buffer.set(console.getCommandHistory().getAll().get(historyIndex));
                                data.setBuf(buffer.get());
                            } else {
                                buffer.clear();
                                historyIndex = -1;
                                data.setBuf("");
                            }
                        }
                    }
                    break;
            }
        }
    };
}
