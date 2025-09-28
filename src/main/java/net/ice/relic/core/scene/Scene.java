package net.ice.relic.core.scene;

import net.ice.relic.Lifecycle;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.common.annotations.Rewrite;
import net.ice.relic.common.util.ColorUtil;
import net.ice.relic.core.ProjectionMatrix;
import net.ice.relic.core.gui.Gui;
import net.ice.relic.core.rendering.backend.opengl.model.Model;
import net.ice.relic.core.rendering.backend.opengl.model.ModelLoader;
import net.ice.relic.core.scene.light.AmbientLight;
import net.ice.relic.core.scene.light.DirectionalLight;
import net.ice.relic.core.scene.light.PointLight;
import net.ice.relic.core.scene.light.SpotLight;
import org.joml.Vector3f;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Rewrite
public abstract class Scene implements Lifecycle {

    private final String name;

    private AmbientLight ambientLight;
    private DirectionalLight directionalLight;

    private List<PointLight> pointLights;
    private List<SpotLight> spotLights;

    private Map<String, SceneObject> objects;

    private ProjectionMatrix matrix;
    private Camera camera;

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
        this.modelLoader = new ModelLoader(application, application.getTextureCache(), application.getMaterialCache(), application.getModelCache());
        this.camera = new Camera(application);
        this.matrix = new ProjectionMatrix(application);
        this.objects = new HashMap<>();
        this.fog = new Fog(false, ColorUtil.ColorDefaults.WHITE.getColor(), 0.2f);

        this.ambientLight = new AmbientLight().setIntensity(10).setColor(0.3f, 0.3f, 0.3f);
        this.directionalLight = new DirectionalLight(ColorUtil.ColorDefaults.WHITE.getColor(), new Vector3f(0, 1, 0), 1);
        this.spotLights = new ArrayList<>();
        this.pointLights = new ArrayList<>();
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

    public List<SpotLight> getSpotLights() {
        return spotLights;
    }

    public List<PointLight> getPointLights() {
        return pointLights;
    }

    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }
    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
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
        this.camera = new Camera(application);
        this.matrix = new ProjectionMatrix(application).init();
    }

    public Map<String, SceneObject> getObjects() {
        return objects;
    }

    public SceneObject getObject(String id) {
        return objects.get(id);
    }
}


