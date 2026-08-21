package net.ice.relic.editor.ui;

import imgui.ImGui;
import imgui.ImGuiIO;
import net.ice.curio.input.Input;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.gui.Gui;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import static imgui.ImGui.*;

public class RelicEditorUI implements Gui {

	private final RelicApplication application;

	public RelicEditorUI(RelicApplication application) {
		this.application = application;
	}

	@Override
	public void draw() {
		newFrame();
		dockSpaceOverViewport(ImGui.getMainViewport());

		menuBar();

		endFrame();
		render();
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

	public void menuBar() {
		if(beginMainMenuBar()) {
			if(beginMenu("File")) {
				button("New");
				separator();
				button("Open");
				button("Open Recent");
				separator();
				button("Save");
				button("Save As...");
				separator();
				button("Import");
				button("Export");
				separator();
				button("Exit");

				endMenu();
			}

			if(beginMenu("Edit")) {
				button("Undo");
				button("Redo");
				separator();
				button("Preferences");
				endMenu();
			}
			endMainMenuBar();

			if(begin("test")) {
				end();
			}

			if(begin("te2st")) {
				end();
			}

			if(begin("te3st")) {
				end();
			}
		}
//		if(begin("dasgf", MenuBar)) {
//			if(beginMenuBar()) {
//				if(beginMenu("File")) {
//					text("test");
//					endMenu();
//				}
//
//				if(beginMenu("Edit")) {
//					endMenu();
//				}
//
//				endMenuBar();
//			}
//			end();
//		}
	}
}
