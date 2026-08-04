package net.ice.relic.core.model;

import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.render.ModelRenderInfo;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.depricated.model.Animation;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private final String id;
    private List<Animation> animations;
    private List<Entity> entities;
    private List<Mesh> meshes;
    private ModelInfo modelInfo;

    private int instanceCount;

    public Model(String id, List<Mesh> meshes, List<Animation> animations) {
        this.id = id;
        this.entities = new ArrayList<>();
        this.meshes = meshes;
        this.animations = animations;

        int modelSize = 0;
        int indicesSize = 0;

        for (Mesh mesh : meshes) {
            modelSize += mesh.getMeshSize();
            indicesSize += mesh.getIndicesSize();
        }

        this.modelInfo = new ModelInfo(modelSize, indicesSize);
    }

    public boolean isAnimated() {
        return animations != null && !animations.isEmpty();
    }

    public void newInstance() {
        instanceCount++;
    }

    public void deleteInstance() {
        instanceCount--;
    }

    public int getInstanceCount() {
        return instanceCount;
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

    public List<Mesh> getMeshes() {
        return meshes;
    }

    public ModelInfo getModelInfo() {
        return modelInfo;
    }

    public boolean renderInfoCheck(List<ModelRenderInfo> infos) {
        for (ModelRenderInfo info : infos) {
            if(info.getAssociatedModel().equals(this)) {
                return true;
            }
        }
        return false;
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
