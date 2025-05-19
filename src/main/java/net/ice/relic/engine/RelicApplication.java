package net.ice.relic.engine;

import net.ice.relic.engine.opengl.Renderer;
import net.ice.relic.engine.opengl.scene.Scene;

public interface RelicApplication {

    void init(Window window, Scene scene, Renderer renderer);

    void update(Window window, Scene scene, Renderer renderer);

    void cleanup();
}
