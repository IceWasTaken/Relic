package net.ice.relic.editor.scene;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.scene.Scene;
import net.ice.relic.editor.ui.RelicEditorUI;

public class DummyScene extends Scene {

	public DummyScene(String name, RelicApplication application) {
		super(name, application);
	}

	@Override
	protected void sceneInit() {
		setGUI(new RelicEditorUI(application));
	}

	@Override
	protected void sceneUpdate(float deltaTime) {

	}

	@Override
	protected void sceneDestroy() {

	}
}
