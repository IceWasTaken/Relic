package net.ice.relic.common.test.gui;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiTextFilter;
import imgui.type.ImBoolean;
import net.ice.curio.input.Input;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.gui.Gui;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.depricated.RenderType;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static imgui.ImGui.*;
import static imgui.flag.ImGuiCond.Always;

public class DebugGui implements Gui {

    private RelicApplication application;
    private GLRenderer glRenderer;
    private ConsoleGui consoleGui;
    private ImageViewer imageViewer;
    private SceneInfoGui sceneInfoGui;

    private boolean viewerOpen = false;
    private boolean infoOpen = false;

    private ImBoolean fileViewerOpen = new ImBoolean(false);

    private ImBoolean postRendering = new ImBoolean(false);


    private int itemSelectedIndex = 0;

    public DebugGui(RelicApplication application) {
        this.application = application;
        this.consoleGui = new ConsoleGui(application);
        this.imageViewer = new ImageViewer(application);
        this.sceneInfoGui = new SceneInfoGui();

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

        consoleGui.draw();

        if(viewerOpen) {
            imageViewer.draw();
        }

        if(infoOpen) {
            sceneInfoGui.draw(application.getCurrentScene());
        }

        endFrame();
        render();
    }


    private void debugMenu() {
        if(begin("Debug Menu")) {
            setWindowSize(420,320);
            renderCombo();

            if(button("Console")) {
                consoleGui.toggle();
            }
            if(button("Image Viewer")) {
                viewerOpen = !viewerOpen;
            }
            if(button("File Viewer")) {
                fileViewerOpen.set(!fileViewerOpen.get());
                FileBrowse.show(new ImBoolean(fileViewerOpen));
            }
            if(button("Scene Info")) {
                infoOpen = !infoOpen;

            }
            if(checkbox("Post Rendering", glRenderer.getPostRenderer().shouldRender())) {
                glRenderer.getPostRenderer().toggleRendering();
            }

            end();
        }
    }

    private void renderCombo() {
        List<String> renderTypes = new ArrayList<>();
        renderTypes.add("Normal");
        renderTypes.add("Albedo");
        renderTypes.add("Normals");
        renderTypes.add("Pos");
        renderTypes.add("Pbr");
        renderTypes.add("Depth");
        renderTypes.add("Shadow");

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
                        //glRenderer.setRenderType(getRenderType(renderTypes.get(itemSelectedIndex)));
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
        Vector2f mousePos = Input.getInstance().getMousePosition();
        imGuiIO.addMousePosEvent(mousePos.x, mousePos.y);
        imGuiIO.addMouseButtonEvent(0, Input.getInstance().getMouseButtonsDown().contains(GLFW.GLFW_MOUSE_BUTTON_1));
        imGuiIO.addMouseButtonEvent(1, Input.getInstance().getMouseButtonsDown().contains(GLFW.GLFW_MOUSE_BUTTON_2));

        return imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
    }
}
