package net.ice.relic.common.test.gui;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.gui.GuiContext;
import net.ice.relic.core.gui.drawable.GuiWindow;
import net.ice.relic.core.rendering.backend.opengl.buffer.StaticCommandBuffer;

import static imgui.ImGui.*;
import static imgui.flag.ImGuiWindowFlags.*;

public class CommandViewer extends GuiWindow {

	public CommandViewer(RelicApplication relicApplication) {
		super(new GuiInfo(
				"Command Viewer",
				NoResize | NoScrollbar | AlwaysAutoResize
		), relicApplication);
	}

	@Override
	protected void draw(GuiContext ctx) {
		int index = 0;
//		for(StaticCommandBuffer.DrawCommand command : StaticCommandBuffer.COMMANDS) {
//			if(treeNode(String.valueOf(index), "Command " + index)) {
//				drawCommandInfo(command);
//				treePop();
//			}
//			index++;
//		}
	}

	private void drawCommandInfo(StaticCommandBuffer.DrawCommand command) {
		text("Count: " + command.indexCount());
		text("InstanceCount: " + command.instanceCount());
		text("FirstIndex: " + command.firstIndex());
		text("BaseVertex: " + command.baseVertex());
		text("BaseInstance: " + command.baseInstance());
	}


}
