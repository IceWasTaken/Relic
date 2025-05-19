package net.ice.relic.engine.opengl.scene;

import net.ice.relic.engine.opengl.Camera;

public abstract class Scene implements IScene {

    private Camera camera;

    public Scene() {
        this.camera = new Camera();
    }

    public Camera getCamera() {
        return camera;
    }
}
