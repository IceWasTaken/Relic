package net.ice.relic.core.scene;

import net.ice.relic.core.model.Material;
import net.ice.relic.core.model.mesh.MeshData;
import net.ice.relic.core.model.Model;

@Deprecated
public class Skybox {

    private Material material;
    private MeshData mesh;
    //private SceneObject sceneObject;
    private Model model;

//    public Skybox(String modelPath, ModelLoader loader) {
//        model = loader.loadModel(Resource.getResource("relic", modelPath), false);
//        Mesh meshData = model.getMeshes().getFirst();
//        material = loader.getMaterialCache().getMaterial(meshData.getMaterialIndex());
//        //mesh = new Mesh(meshData);
//        model.getMeshes().clear();
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

    public MeshData getMesh() {
        return mesh;
    }
}
