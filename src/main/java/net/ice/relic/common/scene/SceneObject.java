package net.ice.relic.common.scene;

import net.ice.relic.common.AnimationData;
import net.ice.relic.engine.opengl.model.Model;

import java.util.List;

public class SceneObject {

    private final String name;

    private final Model model;
    private AnimationData animationData;
    private Transform transform;

    private List<SceneObject> children;
    private SceneObject parent;

    public SceneObject(String name, Model model) {
        this.name = name;
        this.model = model;
        this.transform = new Transform();
    }

    public AnimationData getAnimationData() {
        return animationData;
    }

    public void setAnimationData(AnimationData animationData) {
        this.animationData = animationData;
    }

    public String getName() {
        return name;
    }

    public Model getModel() {
        return model;
    }

    public Transform getTransform() {
        return transform;
    }
}
