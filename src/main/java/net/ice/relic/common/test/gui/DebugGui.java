package net.ice.relic.common.test.gui;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiTextFilter;
import imgui.type.ImInt;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.gui.Gui;
import net.ice.relic.core.rendering.backend.opengl.rendering.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.rendering.enums.RenderType;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static imgui.ImGui.*;
import static imgui.flag.ImGuiCond.Always;

public class DebugGui implements Gui {

    private RelicApplication application;
    private GLRenderer glRenderer;

    private int itemSelectedIndex = 0;

    public DebugGui(RelicApplication application) {
        this.application = application;

        if(application.getRenderer() instanceof GLRenderer glRenderer) {
            this.glRenderer = glRenderer;
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void draw() {
        newFrame();
        setNextWindowPos(0,0, Always);
        debugMenu();
        endFrame();
        render();
    }

    private void debugMenu() {
        if(begin("Debug Menu")) {
            setWindowSize(420,620);
            renderCombo();


            end();
        }
    }

    private void renderCombo() {
        List<String> renderTypes = new ArrayList<>();
        renderTypes.add("Normal");
        renderTypes.add("No_Lighting");
        renderTypes.add("Normal_Maps");

        if(beginCombo("Render Type", renderTypes.get(itemSelectedIndex), 0)) {
            ImGuiTextFilter filter = new ImGuiTextFilter();
            if(isWindowAppearing()) {
                setKeyboardFocusHere();
                filter.clear();
            }
            filter.draw("##Filter", 300);
            for (int i = 0; i < renderTypes.size(); i++) {
                boolean selected = itemSelectedIndex == i;
                if(filter.passFilter(renderTypes.get(i))) {
                    if(selectable(renderTypes.get(i), selected)) {
                        itemSelectedIndex = i;
                        glRenderer.setRenderType(getRenderType(renderTypes.get(itemSelectedIndex)));
                    }
                }
            }
            endCombo();
        }
    }

    private RenderType getRenderType(String str) {
        for(RenderType renderType : RenderType.values()) {
            if(renderType.toString().equalsIgnoreCase(str)) {
                return renderType;
            }
        }
        return null;
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
