package net.ice.relic.engine.opengl;

import net.ice.relic.engine.RelicApplication;
import net.ice.relic.engine.Window;
import net.ice.relic.engine.opengl.model.Model;
import net.ice.relic.engine.opengl.scene.Scene;
import net.ice.rune.scenes.SceneTest;

import java.util.HashMap;

public class Relic {

    private final Window window;
    private final Scene scene;
    private final Renderer renderer;

    public Relic(RelicApplication application) {
        this.window = new Window(new Window.WindowOptions(720, 720, "Relic Demo", true), application);
        this.scene = new SceneTest();
        this.renderer = new Renderer(window);

        application.init(window, scene, renderer);
        application.update(window, scene, renderer);
        application.cleanup();
    }
}
