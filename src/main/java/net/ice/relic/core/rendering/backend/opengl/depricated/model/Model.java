package net.ice.relic.core.rendering.backend.opengl.depricated.model;

import net.ice.relic.core.model.mesh.MeshData;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLManager;
import net.ice.relic.core.scene.SceneObject;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private final String id;
    private List<Animation> animations;
    private List<SceneObject> sceneObjects;
    private List<MeshData> meshData;
    private List<GLRenderer.MeshDrawData> meshDrawData;

    public Model(String id, List<MeshData> meshData, List<Animation> animations) {
        this.id = id;
        this.sceneObjects = new ArrayList<>();
        this.meshData = meshData;
        this.animations = animations;
        this.meshDrawData = new ArrayList<>();
    }

    public boolean isAnimated() {
        return animations != null && !animations.isEmpty();
    }

    //setters/getters

    public List<Animation> getAnimations() {
        return animations;
    }

    public List<SceneObject> getSceneObjects() {
        return sceneObjects;
    }

    public String getId() {
        return id;
    }

    public List<MeshData> getMeshData() {
        return meshData;
    }

    public List<GLRenderer.MeshDrawData> getMeshDrawData() {
        return meshDrawData;
    }
}
