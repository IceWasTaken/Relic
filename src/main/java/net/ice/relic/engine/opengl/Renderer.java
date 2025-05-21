package net.ice.relic.engine.opengl;

import net.ice.relic.engine.Window;
import net.ice.relic.engine.opengl.scene.Scene;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL43.*;

public class Renderer {

    private SceneRenderer sceneRenderer;

    public Renderer(Window window) {
        createCapabilities();
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


        this.sceneRenderer = new SceneRenderer();
    }

    public void render(Window window, Scene scene) {
        glClearColor(0.6f, 0.7f, 0.8f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        sceneRenderer.render(scene);

    }





    public void cleanup() {
        sceneRenderer.cleanup();
    }
}
