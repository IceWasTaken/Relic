package net.ice.relic.engine.opengl.scene;

import net.ice.relic.engine.opengl.MaterialCache;
import net.ice.relic.engine.opengl.model.*;
import net.ice.relic.engine.opengl.model.texture.TextureLoader;

public class Skybox {

    private Material material;
    private Mesh mesh;
    private SceneObject sceneObject;
    private Model model;

    public Skybox(String modelPath, TextureLoader loader, MaterialCache cache) {
        model = ModelLoader.loadModel("skybox", modelPath, loader, cache, false);
        System.out.println("Loaded skybox model: " + model.getMeshData().size() + " meshes");
        MeshData meshData = model.getMeshData().getFirst();
        material = cache.getMaterial(meshData.getMaterialIndex());
        mesh = new Mesh(meshData);
        model.getMeshData().clear();
        sceneObject = new SceneObject("skybox", model);
    }

    public Skybox scale(float factor) {
        sceneObject.setScaleFactor(factor);
        return this;
    }

    public Model getModel() {
        return model;
    }

    public Material getMaterial() {
        return material;
    }

    public SceneObject getSceneObject() {
        return sceneObject;
    }

    public Mesh getMesh() {
        return mesh;
    }
}
