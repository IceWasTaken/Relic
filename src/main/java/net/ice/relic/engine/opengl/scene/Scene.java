package net.ice.relic.engine.opengl.scene;

import net.ice.relic.engine.opengl.Camera;
import net.ice.relic.engine.opengl.model.Model;

import java.util.HashMap;
import java.util.Map;

public abstract class Scene {

    private Camera camera;
    private Map<String, SceneObject> objects;


    public Scene() {
        this.camera = new Camera();
        this.objects = new HashMap<>();

        initObjects();
    }

    public abstract void initObjects();

    public void addObject(String id, SceneObject object) {
        objects.put(id, object);
    }

    public Map<String, SceneObject> getObjects() {
        return objects;
    }

    public Camera getCamera() {
        return camera;
    }
}
