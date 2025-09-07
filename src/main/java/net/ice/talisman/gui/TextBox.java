package net.ice.talisman.gui;

import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.type.ImString;

import static imgui.flag.ImGuiInputTextFlags.CallbackResize;

public class TextBox {

    private ImString buffer;

    public TextBox() {
        this.buffer = new ImString("");
    }

    public TextBox(ImString buffer) {
        this.buffer = buffer;
    }

    public boolean draw(String label) {
        return ImGui.inputText(label, buffer, CallbackResize, consoleCallback);
    }

    private final ImGuiInputTextCallback consoleCallback = new ImGuiInputTextCallback() {
        @Override
        public void accept(ImGuiInputTextCallbackData data) {
            data.setBuf(buffer.get());
        }
    };
}
