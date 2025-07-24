package net.ice.rune;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.common.gui.Gui;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class GuiTest implements Gui {
    @Override
    public void draw() {
        ImGui.newFrame();
        ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
        ImGui.showDemoWindow();
        ImGui.endFrame();
        ImGui.render();
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
}
