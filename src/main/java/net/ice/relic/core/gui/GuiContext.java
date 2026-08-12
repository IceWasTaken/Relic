package net.ice.relic.core.gui;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.scene.Scene;

public class GuiContext {

	private final RelicApplication application;


	public GuiContext(RelicApplication relicApplication) {
		this.application = relicApplication;
	}

	public Scene getCurrentScene() {
		return application.getCurrentScene();
	}

	public RelicApplication getApplication() {
		return application;
	}
}
