package net.ice.relic.core.scene;

import net.ice.heirloom.Lifecycle;
import net.ice.heirloom.color.Colors;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.ProjectionMatrix;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.gui.Gui;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.opengl.BufferManager;
import net.ice.relic.core.rendering.backend.opengl.depricated.model.ModelLoader;
import net.ice.relic.core.scene.light.AmbientLight;
import net.ice.relic.core.scene.light.Light;
import org.joml.Vector3f;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Scene implements Lifecycle {

    protected final Entity sceneRoot;

    protected final String name;

    protected final List<Light> lights;
    protected final AmbientLight ambientLight;

    private final List<Entity> entities;

    protected final Camera camera;
    protected final ProjectionMatrix matrix;

    private Gui GUI;

    private boolean initialized;

    protected ModelLoader modelLoader;
    protected RelicApplication application;

    protected abstract void sceneInit();
    protected abstract void sceneUpdate(float deltaTime);
    protected abstract void sceneDestroy();

    public Scene(String name, RelicApplication application) {
        this.sceneRoot = Entity.newEntity("sceneroot");

        this.name = name;
        this.application = application;
        this.modelLoader = new ModelLoader(application.getTextureCache(), application.getMaterialCache(), application.getModelCache());
        this.camera = new Camera();
        this.matrix = new ProjectionMatrix(application);
        this.entities = new ArrayList<>();
        this.ambientLight = new AmbientLight().setIntensity(0.1f).setColor(Colors.WHITE.getRGBColor());
        this.lights = new ArrayList<>();

        lights.add(new Light(new Vector3f(0, -1.0f,0), true, 8, Colors.WHITE.getRGBColor()));
    }

    @Override
    public void init() {
        if (initialized) {
            Logger.error("[Scene] Initialization called while already initialized");
            return;
        }

        sceneInit();

        Logger.info("[Scene] Loaded scene: {}", name);
        initialized = true;

        application.getRenderer().setupData();
    }

    @Override
    public void cleanup() {
        if (!initialized) {
            Logger.error("[Scene] Cleanup called while not initialized");
            return;
        }

        entities.clear();
    }

    @Override
    public void update(float deltaTime) {
        matrix.update();
        sceneUpdate(deltaTime);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public void removeEntity(int i) {
        entities.remove(i);
    }

    public Entity createEntity(String name) {
        Entity entity = sceneRoot.newChild(name);
        entities.add(entity);
        BufferManager.entityLoadingQueue.add(entity);
        return entity;
    }

    public Entity getEntity(String name) {
        for(Entity entity : entities) {
            if(entity.getName().equals(name)) {
                return entity;
            }
        }
        return null;
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

    public int getLightCount() {
        return lights.size();
    }

    public Gui getGUI() {
        return GUI;
    }

    public void setGUI(Gui GUI) {
        this.GUI = GUI;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public Entity getEntityRoot() {
        return sceneRoot;
    }

    public void setApplication(RelicApplication application) {
        this.application = application;
    }

    public RelicApplication getApplication() {
        return application;
    }
}


