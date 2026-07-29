package net.ice.relic.core.model;

import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.depricated.model.Animation;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private final String id;
    private List<Animation> animations;
    private List<Entity> entities;
    private List<Mesh> meshData;
    private List<GLRenderer.MeshDrawData> meshDrawData;

    public Model(String id, List<Mesh> meshData, List<Animation> animations) {
        this.id = id;
        this.entities = new ArrayList<>();
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

    public List<Entity> getSceneObjects() {
        return entities;
    }

    public String getId() {
        return id;
    }

    public List<Mesh> getMeshData() {
        return meshData;
    }

    public List<GLRenderer.MeshDrawData> getMeshDrawData() {
        return meshDrawData;
    }
}
