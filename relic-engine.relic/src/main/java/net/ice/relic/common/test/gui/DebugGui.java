package net.ice.relic.common.test.gui;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.type.ImBoolean;
import net.ice.curio.input.Input;
import net.ice.relic.RelicApplication;
import net.ice.relic.core.gui.Gui;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import static imgui.ImGui.*;
import static imgui.flag.ImGuiCond.Always;

public class DebugGui implements Gui {

    private RelicApplication application;
    //private GLRenderer glRenderer;
    private ConsoleGui consoleGui;
    private ImageViewer imageViewer;
    private SceneInfoGui sceneInfoGui;
    private ECSGui ecsGui;
    private CommandViewer commandViewer;

    private boolean infoOpen = false;

    private ImBoolean fileViewerOpen = new ImBoolean(false);

    private ImBoolean postRendering = new ImBoolean(false);


    private int itemSelectedIndex = 0;

    public DebugGui(RelicApplication application) {
        this.application = application;
        this.consoleGui = new ConsoleGui(application);
        this.imageViewer = new ImageViewer(application);
        this.sceneInfoGui = new SceneInfoGui();
        this.ecsGui = new ECSGui(application);
        this.commandViewer = new CommandViewer(application);

//        if(application.getRenderer() instanceof GLRenderer glRenderer) {
//            this.glRenderer = glRenderer;
//        } else {
//            throw new RuntimeException();
//        }
    }

    @Override
    public void draw() {
        newFrame();

        setNextWindowPos(0,0, Always);
        debugMenu();

        consoleGui.draw();
        imageViewer.draw();


        if(infoOpen) {
            sceneInfoGui.draw(application.getCurrentScene());
        }
        ecsGui.draw();
        commandViewer.draw();

        endFrame();
        render();
    }


    private void debugMenu() {
        if(begin("Debug Menu")) {
            setWindowSize(420,320);
            if(button("Console")) {
                consoleGui.toggle();
            }
            imageViewer.drawToggleButton();
            if(button("File Viewer")) {
                fileViewerOpen.set(!fileViewerOpen.get());
                FileBrowse.show(new ImBoolean(fileViewerOpen));
            }
            if(button("Scene Info")) {
                infoOpen = !infoOpen;
            }
            if(button("Crash")) {
                end();
                endFrame();
                throw new RuntimeException("[DebugGui]: Pressed the red button");
            }
            ecsGui.drawToggleButton();
            commandViewer.drawToggleButton();

//            if(checkbox("Post Rendering", glRenderer.getPostRenderer().shouldRender())) {
//                glRenderer.getPostRenderer().toggleRendering();
//            }

            end();
        }
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
