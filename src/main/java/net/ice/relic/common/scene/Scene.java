package net.ice.relic.common.scene;

import net.ice.relic.annotations.Rewrite;
import net.ice.relic.RelicApplication;
import net.ice.relic.common.cache.MaterialCache;
import net.ice.relic.common.gui.Gui;
import net.ice.relic.engine.opengl.ProjectionMatrix;
import net.ice.relic.engine.opengl.ShaderProgram;
import net.ice.relic.engine.opengl.model.Model;
import net.ice.relic.common.cache.TextureCache;
import net.ice.relic.common.scene.light.AmbientLight;
import org.joml.Vector3f;
import org.tinylog.Logger;

import java.util.HashMap;
import java.util.Map;

@Rewrite
public abstract class Scene {

    private String name;
    private Lights lights;
    private Camera camera;
    private Fog fog;
    private Map<String, SceneObject> objects;
    private TextureCache loader;
    private MaterialCache materialCache;
    private Skybox skybox;
    private ProjectionMatrix matrix;
    private Gui GUI;
    private boolean loaded;
    private boolean initialized;

    protected RelicApplication application;

    public Scene(String name) {
        this.name = name;
        this.camera = new Camera(application);
        this.matrix = new ProjectionMatrix(application);
        this.loader = new TextureCache();
        this.objects = new HashMap<>();
        this.materialCache = new MaterialCache();
        this.fog = new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.2f);
        resetState();

        Lights lights1 = new Lights();

        AmbientLight ambientLight = lights1.getAmbientLight();
        ambientLight.setIntensity(0.5f);
        ambientLight.setColor(0.3f, 0.3f, 0.3f);

        this.setLights(lights1);
    }

    public void init() {
        if(initialized) {
            throw new IllegalStateException("Scene already initialized.");
        }

        load();

        Logger.info("Loaded scene: " + name);
        loaded = true;
    }

    public void destroy() {
        if(!initialized || !loaded) {
            throw new IllegalStateException("Scene not initialized or not loaded.");
        }

        objects.clear();
    }

    public void update(float deltaTime) {
//        if(!initialized || !loaded) {
//            throw new IllegalStateException("Scene not initialized or not loaded.");
//        }

        sceneUpdate(deltaTime);
    }

    protected void load() {
        sceneInit();
    }

    public void resetState() {
        loaded = false;
        initialized = false;
    }

    protected abstract void sceneInit();
    protected abstract void sceneUpdate(float deltaTime);

    public Map<String, Model> getModels() {
        Map<String, Model> models = new HashMap<>();
        for(SceneObject object : objects.values()) {
            models.put(object.getId(), object.getModel());
        }
        return models;
    }

    public void addObject(String id, SceneObject object) {
        objects.put(id, object);
        object.getModel().getSceneObjects().add(object);
    }

    public void removeObject(String id) {
        SceneObject object = objects.remove(id);
        if(object != null) {
            object.getModel().getSceneObjects().remove(object);
        }
    }

    public void loadShader(ShaderProgram shaderProgram) {
        application.getRenderer().enablePostShader(shaderProgram);
    }

    public void unloadShader() {
        application.getRenderer().disablePostShader();
    }

    public Map<String, SceneObject> getObjects() {
        return objects;
    }

    public Camera getCamera() {
        return camera;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public Lights getLights() {
        return lights;
    }

    public void setLights(Lights lights) {
        this.lights = lights;
    }

    public void setGUI(Gui GUI) {
        this.GUI = GUI;
    }

    public String getName() {
        return name;
    }

    public TextureCache getTextureLoader() {
        return loader;
    }

    public void setApplication(RelicApplication application) {
        this.application = application;
        this.camera = new Camera(application);
        this.matrix = new ProjectionMatrix(application).init();
    }

    public TextureCache getLoader() {
        return loader;
    }

    public ProjectionMatrix getMatrix() {
        return matrix;
    }

    public MaterialCache getMaterialCache() {
        return materialCache;
    }

    public Fog getFog() {
        return fog;
    }

    public void setSkybox(Skybox skybox) {
        this.skybox = skybox;
    }

    public Skybox getSkybox() {
        return skybox;
    }

    public Gui getGUI() {
        return GUI;
    }
}

