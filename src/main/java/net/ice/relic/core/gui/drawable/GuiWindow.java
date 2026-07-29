package net.ice.relic.core.gui.drawable;

import imgui.type.ImBoolean;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.gui.GuiContext;

import static imgui.ImGui.*;

public abstract class GuiWindow {

	private final GuiInfo guiInfo;
	private final RelicApplication relicApplication;

	private final ImBoolean open = new ImBoolean(false);

	protected abstract void draw(GuiContext ctx);

	public GuiWindow(GuiInfo guiInfo, RelicApplication relicApplication) {
		this.guiInfo = guiInfo;
		this.relicApplication = relicApplication;
	}

	public void draw() {
		if(open.get()) {
			if(!begin(guiInfo.name, open, guiInfo.windowFlags)) {
				end();
				return;
			}

			draw(new GuiContext(relicApplication));

			end();
		}
	}

	public void drawToggleButton() {
		if(button(guiInfo.name)) {
			open.set(!open.get());
		}
	}


	public record GuiInfo(
		String name,
		int windowFlags
	){}
}
