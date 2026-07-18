package net.ice.relic.core.scene;

import net.ice.heirloom.Lifecycle;
import net.ice.heirloom.color.Colors;
import net.ice.relic.application.RelicApplication;
 import org.tinylog.Logger;
import net.ice.relic.core.ProjectionMatrix;
import net.ice.relic.core.gui.Gui;
import net.ice.relic.core.rendering.backend.opengl.depricated.model.Model;
import net.ice.relic.core.rendering.backend.opengl.depricated.model.ModelLoader;
import net.ice.relic.core.scene.light.*;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public abstract class Scene implements Lifecycle {

    private final String name;

    private AmbientLight ambientLight;

    private final List<Light> lights;

    private Map<String, SceneObject> objects;

    private ProjectionMatrix matrix;
    protected Camera camera;

    private Skybox skybox;
    private Fog fog;
    private Gui GUI;

    private boolean initialized;

    protected ModelLoader modelLoader;
    protected RelicApplication application;

    protected abstract void sceneInit();
    protected abstract void sceneUpdate(float deltaTime);
    protected abstract void sceneDestroy();

    public Scene(String name, RelicApplication application) {
        this.name = name;
        this.application = application;
        this.modelLoader = new ModelLoader(application.getTextureCache(), application.getMaterialCache(), application.getModelCache());
        this.camera = new Camera();
        this.matrix = new ProjectionMatrix(application);
        this.objects = new HashMap<>();
        this.fog = new Fog(false, Colors.WHITE.getRGBColor(), 0.2f);
        this.ambientLight = new AmbientLight().setIntensity(0.2f).setColor(Colors.WHITE.getRGBColor());
        this.lights = new ArrayList<>();

        lights.add(new Light(new Vector3f(0, -1.0f,0), true, 8, Colors.LIME.getRGBColor()));
    }

    @Override
    public void init() {
        if (initialized) {
            throw new IllegalStateException("Scene already initialized.");
        }

        sceneInit();

        Logger.info("Loaded scene: " + name);
        initialized = true;
    }

    @Override
    public void cleanup() {
        if (!initialized) {
            throw new IllegalStateException("Scene not initialized or not loaded.");
        }

        objects.clear();
    }

    @Override
    public void update(float deltaTime) {
        matrix.update();
        sceneUpdate(deltaTime);
    }

    public Map<String, Model> getModels() {
        Map<String, Model> models = new HashMap<>();
        for (SceneObject object : objects.values()) {
            models.put(object.getName(), object.getModel());
        }
        return models;
    }

    public void addSceneObject(String id, SceneObject object) {
        objects.put(id, object);
        object.getModel().getSceneObjects().add(object);

        if(initialized) {
            application.getRenderer().setupData();
        }
    }

    public void removeObject(String id) {
        SceneObject object = objects.remove(id);
        if (object != null) {
            object.getModel().getSceneObjects().remove(object);
        }
    }

    public List<Light> getLights() {
        return lights;
    }

    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    public ModelLoader getModelLoader() {
        return modelLoader;
    }

    public ProjectionMatrix getMatrix() {
        return matrix;
    }

    public Camera getCamera() {
        return camera;
    }

    public Skybox getSkybox() {
        return skybox;
    }

    public int getLightCount() {
        return lights.size();
    }

    public void setSkybox(Skybox skybox) {
        this.skybox = skybox;
    }

    public Fog getFog() {
        return fog;
    }

    public Gui getGUI() {
        return GUI;
    }

    public void setGUI(Gui GUI) {
        this.GUI = GUI;
    }


    public void setApplication(RelicApplication application) {
        this.application = application;
    }

    public Map<String, SceneObject> getObjects() {
        return objects;
    }

    public SceneObject getObject(String id) {
        return objects.get(id);
    }
}


