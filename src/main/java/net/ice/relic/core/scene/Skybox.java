package net.ice.relic.core.scene;

import net.ice.heirloom.io.resource.Resource;
import net.ice.relic.core.model.Material;
import net.ice.relic.core.model.Mesh;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.opengl.depricated.model.ModelLoader;
import org.joml.Vector3f;

@Deprecated
public class Skybox {

    private Material material;
    private Mesh mesh;
    //private SceneObject sceneObject;
    private Model model;

//    public Skybox(String modelPath, ModelLoader loader) {
//        model = loader.loadModel(Resource.getResource("relic", modelPath), false);
//        Mesh meshData = model.getMeshData().getFirst();
//        material = loader.getMaterialCache().getMaterial(meshData.getMaterialIndex());
//        //mesh = new Mesh(meshData);
//        model.getMeshData().clear();
//        sceneObject = new SceneObject("skybox", model);
//    }

//    public Skybox scale(Vector3f factor) {
//        sceneObject.getTransform().setScale(factor);
//        return this;
//    }

    public Model getModel() {
        return model;
    }

    public Material getMaterial() {
        return material;
    }

//    public SceneObject getSceneObject() {
//        return sceneObject;
//    }

    public Mesh getMesh() {
        return mesh;
    }
}
