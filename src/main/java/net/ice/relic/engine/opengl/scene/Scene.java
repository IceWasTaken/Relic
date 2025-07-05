package net.ice.relic.engine.opengl.scene;

import net.ice.relic.annotations.Rewrite;
import net.ice.relic.engine.RelicApplication;
import net.ice.relic.engine.opengl.MaterialCache;
import net.ice.relic.engine.opengl.model.Model;
import net.ice.relic.engine.opengl.model.texture.TextureLoader;
import net.ice.relic.engine.opengl.scene.light.AmbientLight;
import org.joml.Vector3f;
import org.tinylog.Logger;

import java.util.HashMap;
import java.util.Map;

@Rewrite
public abstract class Scene {

    private String name;
    private final int id;
    private Lights lights;
    private Camera camera;
    private Fog fog;
    private Map<String, SceneObject> objects;
    private Map<String, Model> models;
    private RelicApplication application;
    private TextureLoader loader;
    private MaterialCache materialCache;
    private boolean loaded;
    private boolean initialized;

    private static int lastID;

    public Scene(String name) {
        this.id = genID();
        this.name = name;
        this.camera = new Camera(application);
        this.loader = new TextureLoader();
        this.models = new HashMap<>();
        this.objects = new HashMap<>();
        this.materialCache = new MaterialCache();
        this.fog = new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.005f);
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

    protected void load() {
        sceneInit();
    }

    public void resetState() {
        loaded = false;
        initialized = false;
    }

    private int genID() {
        return lastID++;
    }

    protected abstract void sceneInit();


    public void addObject(String id, SceneObject object) {
        models.put(id, object.getModel());
        objects.put(id, object);
        object.getModel().getSceneObjects().add(object);
    }

    public Map<String, SceneObject> getObjects() {
        return objects;
    }

    public Map<String, Model> getModels() {
        return models;
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

    public String getName() {
        return name;
    }

    public TextureLoader getTextureLoader() {
        return loader;
    }

    public void setApplication(RelicApplication application) {
        this.application = application;
        this.camera = new Camera(application);
    }

    public TextureLoader getLoader() {
        return loader;
    }

    public MaterialCache getMaterialCache() {
        return materialCache;
    }

    public Fog getFog() {
        return fog;
    }
}
