package net.ice.relic.engine.opengl.model;

import net.ice.relic.common.model.MeshData;
import net.ice.relic.engine.opengl.rendering.buffer.RenderingBuffers;
import net.ice.relic.common.scene.SceneObject;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private final String id;
    private List<Animation> animations;
    private List<SceneObject> sceneObjects;
    private List<MeshData> meshData;
    private List<RenderingBuffers.MeshDrawData> meshDrawData;

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

    public List<RenderingBuffers.MeshDrawData> getMeshDrawData() {
        return meshDrawData;
    }
}
