package net.ice.talisman.scene;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.scene.Scene;
import net.ice.talisman.gui.MainGui;

public class EditorScene extends Scene {

    public EditorScene(String name, RelicApplication application) {
        super(name, application);
    }

    @Override
    protected void sceneInit() {
        setGUI(new MainGui());
    }

    @Override
    protected void sceneUpdate(float deltaTime) {

    }

    @Override
    protected void sceneDestroy() {

    }
}
