package net.ice.relic.core.scene;

import net.ice.heirloom.Lifecycle;
import net.ice.heirloom.color.Colors;
import net.ice.heirloom.event.EventManager;
import net.ice.relic.RelicApplication;
import net.ice.relic.common.events.EntityEvent;
import net.ice.relic.core.ProjectionMatrix;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.gui.Gui;
import net.ice.relic.core.rendering.backend.opengl.depricated.model.ModelLoader;
import net.ice.relic.core.scene.light.AmbientLight;
import net.ice.relic.core.scene.light.Light;
import org.joml.Vector3f;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene implements Lifecycle {

    protected final String name;

    protected final Entity sceneRoot;
    protected final List<Entity> entities;

    protected final List<Light> lights;
    protected final AmbientLight ambientLight;

    protected final Camera camera;
    protected final ProjectionMatrix matrix;

    protected final ModelLoader modelLoader;
    protected final RelicApplication application;

    private Gui GUI;

    protected abstract void sceneInit();
    protected abstract void sceneUpdate(float deltaTime);
    protected abstract void sceneDestroy();

    public Scene(String name, RelicApplication application) {
        this.application = application;

        this.sceneRoot = Entity.newEntity("sceneroot");

        this.name = name;
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
        sceneInit();

        Logger.info("[Scene] Loaded scene: {}", name);
    }

    @Override
    public void cleanup() {
        entities.clear();
    }

    @Override
    public void update(float deltaTime) {
        sceneUpdate(deltaTime);
    }

    public void removeEntity(Entity entity) {
        EventManager.execute(new EntityEvent.onEntityDelete(entity));

        entities.remove(entity);
    }

    public Entity createEntity(String name) {
        Entity entity = sceneRoot.newChild(name);
        entities.add(entity);

        EventManager.execute(new EntityEvent.onEntityCreate(entity));

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


    public RelicApplication getApplication() {
        return application;
    }
}


