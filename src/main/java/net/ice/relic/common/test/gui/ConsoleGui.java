package net.ice.relic.common.test.gui;

import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiColorEditFlags;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.common.console.CommandContext;
import net.ice.relic.common.console.Console;
import net.ice.relic.common.console.ConsoleItem;
import net.ice.relic.common.console.nodes.CommandNode;

import static imgui.ImGui.*;
import static imgui.flag.ImGuiCol.Text;
import static imgui.flag.ImGuiInputTextFlags.CallbackHistory;
import static imgui.flag.ImGuiStyleVar.Alpha;
import static net.ice.relic.common.console.Console.findViaPrefix;
import static net.ice.relic.common.console.Console.root;
import static net.ice.relic.common.test.gui.ConsoleGui.palette.*;

public class ConsoleGui {

    private ImBoolean autoScroll = new ImBoolean(true);
    private ImBoolean scrollToBottom = new ImBoolean(false);
    private ImBoolean filterBar = new ImBoolean(true);
    private ImBoolean timeStamps = new ImBoolean(true);
    private ImBoolean coloredOutput = new ImBoolean(true);

    private boolean wasPreviousFrameTabCompletion = false;

    private float[] windowAlpha = new float[]{1.0f};
    private int historyIndex = -1;

    private float[][] colorPalette = new float[6][4];
    private ImString buffer = new ImString(256);

    private ImBoolean open = new ImBoolean(false);

    private final CommandContext ctx;

    public ConsoleGui(RelicApplication application) {
        this.ctx = new CommandContext(application);
        defaultSettings();
    }

    public void draw() {
        if(open.get()) {
            pushStyleVar(Alpha, windowAlpha[0]);
            if(!begin("Console", open, ImGuiWindowFlags.MenuBar)) {
                popStyleVar();
                end();
                return;
            }

            setWindowSize(540, 640);
            popStyleVar();
            menuBar();

            consoleWindow();

            separator();

            inputBar();

            end();
        }
    }

    public void toggle() {
        open.set(!open.get());
    }

    private void consoleWindow() {
        final float footerHeightToReserve = ImGui.getStyle().getItemSpacingY() + ImGui.getFrameHeightWithSpacing();

        if (ImGui.beginChild("ScrollRegion##", new ImVec2(0, -footerHeightToReserve), false, ImGuiWindowFlags.AlwaysAutoResize)) {
            final float timestampWidth = ImGui.calcTextSize("00:00:00:0000 AM").x;

            ImGui.pushTextWrapPos(); // Push once for the whole child

            for (ConsoleItem consoleItem : Console.consoleItems) {
                if (coloredOutput.get()) {
                    pushStyleColor(Text, arrToImVec4(colorPalette[consoleItem.getType().ordinal()]));
                    textUnformatted(consoleItem.getData());
                    popStyleColor();
                } else {
                    textUnformatted(consoleItem.getData());
                }

                if (consoleItem.getType() == ConsoleItem.ItemType.COMMAND && timeStamps.get()) {
                    sameLine(getColumnWidth(-1) - timestampWidth);
                    pushStyleColor(Text, arrToImVec4(colorPalette[COL_TIMESTAMP.ordinal()]));
                    text(consoleItem.getTimestamp());
                    popStyleColor();
                }
            }

            ImGui.popTextWrapPos();

            if ((scrollToBottom.get() && (getScrollY() >= getScrollMaxY() || autoScroll.get()))) {
                setScrollHereY(1.0f);
            }
            scrollToBottom.set(false);

            endChild();
        }
    }

    private ImVec4 arrToImVec4(float[] arr) {
        return new ImVec4(arr[0], arr[1], arr[2], arr[3]);
    }

    private void menuBar() {
        if(ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("Settings")) {
                // Colored output
                ImGui.checkbox("Colored Output", coloredOutput);
                ImGui.sameLine();
                helpMaker("Enable colored command output");

                ImGui.checkbox("Auto Scroll", autoScroll);
                ImGui.sameLine();
                helpMaker("Automatically scroll to bottom of console log");

                ImGui.checkbox("Filter Bar", filterBar);
                ImGui.sameLine();
                helpMaker("Enable console filter bar");

                ImGui.checkbox("Time Stamps", timeStamps);
                ImGui.sameLine();
                helpMaker("Display command execution timestamps");

                if (ImGui.button("Reset settings", new ImVec2(ImGui.getColumnWidth(), 0))) {
                    ImGui.openPopup("Reset Settings?");
                }

                // Confirmation
                if (ImGui.beginPopupModal("Reset Settings?", null, ImGuiWindowFlags.AlwaysAutoResize))
                {
                    ImGui.text("All settings will be reset to default.\nThis operation cannot be undone!\n\n");
                    ImGui.separator();

                    if (ImGui.button("Reset", new ImVec2(120, 0)))
                    {
                        defaultSettings();
                        ImGui.closeCurrentPopup();
                    }

                    ImGui.setItemDefaultFocus();
                    ImGui.sameLine();
                    if (ImGui.button("Cancel", new ImVec2(120, 0))) {
                        ImGui.closeCurrentPopup();
                    }
                    ImGui.endPopup();
                }

                ImGui.endMenu();
            }

            if (beginMenu("Appearance")){
                // Logging Colors
                int flags = ImGuiColorEditFlags.Float | ImGuiColorEditFlags.AlphaPreview | ImGuiColorEditFlags.NoInputs | ImGuiColorEditFlags.AlphaBar;

                textUnformatted("Color Palette");
                indent();
                colorEdit4("Command##", colorPalette[COL_COMMAND.ordinal()], flags);
                colorEdit4("Log##", colorPalette[COL_LOG.ordinal()], flags);
                colorEdit4("Warning##", colorPalette[COL_WARNING.ordinal()], flags);
                colorEdit4("Error##", colorPalette[COL_ERROR.ordinal()], flags);
                colorEdit4("Info##", colorPalette[COL_INFO.ordinal()], flags);
                colorEdit4("Time Stamp##", colorPalette[COL_TIMESTAMP.ordinal()], flags);
                unindent();

                separator();

                // Window transparency.
                textUnformatted("Background");
                sliderFloat("Transparency##", windowAlpha, 0.1f, 1.f);

                endMenu();
            }
            endMenuBar();
        }
    }

    private void helpMaker(String desc)
    {
        ImGui.textDisabled("(?)");
        if (ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            ImGui.pushTextWrapPos(ImGui.getFontSize() * 35.0f);
            ImGui.textUnformatted(desc);
            ImGui.popTextWrapPos();
            ImGui.endTooltip();
        }
    }

    private void defaultSettings() {
        autoScroll.set(true);
        scrollToBottom.set(false);
        coloredOutput.set(true);
        filterBar.set(true);
        timeStamps.set(true);
        colorPalette[COL_COMMAND.ordinal()] = new float[]{1f, 1f, 1f, 1f};
        colorPalette[COL_LOG.ordinal()] = new float[]{1f, 1f, 1f, 0.5f};
        colorPalette[COL_WARNING.ordinal()] = new float[]{1f, 0.87f, 0.37f, 1f};
        colorPalette[COL_ERROR.ordinal()] = new float[]{1f, 0.365f, 0.365f, 1f};
        colorPalette[COL_INFO.ordinal()] = new float[]{0.46f, 0.96f, 0.46f, 1f};
        colorPalette[COL_TIMESTAMP.ordinal()] = new float[]{1f, 1f, 1f, 0.5f};
    }

    public void inputBar() {
        int flags = ImGuiInputTextFlags.EnterReturnsTrue | CallbackHistory;

        boolean reclaimFocus = false;

        ImGui.pushItemWidth(-ImGui.getStyle().getItemSpacingX() * 7);
        if (ImGui.inputTextWithHint("##consoleInput", "Enter command...", buffer, flags, consoleCallback)) {
            if (!buffer.isEmpty()) {
                String input = buffer.get();
                Console.runCommand(input, ctx);
                Console.commandHistory.add(input);
                scrollToBottom.set(true);
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

    private final ImGuiInputTextCallback consoleCallback = new ImGuiInputTextCallback() {
        @Override
        public void accept(ImGuiInputTextCallbackData data) {
            if (data.getBufTextLen() == 0 && data.getEventFlag() != ImGuiInputTextFlags.CallbackHistory) {
                return;
            }

            switch (data.getEventFlag()) {
                case ImGuiInputTextFlags.CallbackHistory: {

                    data.deleteChars(0, data.getBufTextLen());

                    String prevCommand = buffer.get();
                    if (data.getEventKey() == ImGuiKey.UpArrow) {
                        prevCommand = Console.commandHistory.getPrevious();
                    }
                    if(data.getEventKey() == ImGuiKey.DownArrow) {
                        prevCommand = Console.commandHistory.getNext();
                    }
                    if(data.getEventKey() == ImGuiKey.Tab) {
                        String[] tokens = buffer.get().trim().split("\\s+");
                        CommandNode current = root;

                        for(String token : tokens) {
                            CommandNode next = current.getChild(token);
                            if(next == null) {
                                if(findViaPrefix(token, current).size() == 1) {
                                    tokens[tokens.length - 1] = findViaPrefix(token, current).getFirst().getKey();
                                    prevCommand = String.join(" ", tokens);
                                    break;
                                }
                                continue;
                            }
                            current = next;
                        }
                    }

                    data.insertChars(data.getCursorPos(), prevCommand);

                    break;
                }

                case ImGuiInputTextFlags.CallbackCharFilter:
                case ImGuiInputTextFlags.CallbackAlways:
                default:
                    break;
            }
        }
    };

    enum palette {
        COL_COMMAND,
        COL_LOG,
        COL_WARNING,
        COL_ERROR,
        COL_INFO,
        COL_TIMESTAMP,
        COL_COUNT
    }
}
