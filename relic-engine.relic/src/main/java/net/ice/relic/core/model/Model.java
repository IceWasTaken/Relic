package net.ice.relic.core.model;

import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.mesh.MeshData;
import net.ice.relic.core.rendering.backend.opengl.depricated.model.Animation;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private final String id;
    private List<MeshData> meshData;
    private List<Animation> animations;

    private List<Entity> entities;
    private ModelInfo modelInfo;

    public Model(String id, List<MeshData> meshData, List<Animation> animations) {
        this.id = id;
        this.entities = new ArrayList<>();
        this.meshData = meshData;
        this.animations = animations;

        int modelSize = 0;
        int indicesSize = 0;

        for (MeshData mesh : meshData) {
            modelSize += mesh.getMeshSize();
            indicesSize += mesh.getIndicesSize();
        }

        this.modelInfo = new ModelInfo(modelSize, indicesSize);
    }

    public boolean isAnimated() {
        return animations != null && !animations.isEmpty();
    }

    public List<Animation> getAnimations() {
        return animations;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public String getId() {
        return id;
    }

    public List<MeshData> getMeshData() {
        return meshData;
    }

    public ModelInfo getModelInfo() {
        return modelInfo;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Model model) {
			return model.getId().equals(this.id);
        }
        return false;
    }

    public record ModelInfo(
            int modelSize, //size of all meshes combined
            int indicesSize
    ) {}
}
