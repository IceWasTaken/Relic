package net.ice.relic.core.scene;

import net.ice.relic.common.annotations.Rewrite;
import net.ice.relic.core.AnimationData;
import net.ice.relic.core.component.Component;
import net.ice.relic.core.interfaces.Updatable;
import net.ice.relic.core.rendering.backend.opengl.model.Model;

import java.util.ArrayList;
import java.util.List;

@Rewrite
public class SceneObject implements Updatable {

    private final String name;

    private final Model model;
    private AnimationData animationData;
    private List<Component> components;
    private Transform transform;

    private List<SceneObject> children;
    private SceneObject parent;

    public SceneObject(String name, Model model) {
        this.name = name;
        this.model = model;
        this.transform = new Transform();
        this.components = new ArrayList<>();
    }

    @Override
    public void update(float deltaTime) {
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

    public boolean hasComponent(Component component) {
        return components.contains(component);
    }

    public void addComponent(Component component) {
        component.setAttachedObject(this);
        components.add(component);
    }

    public void removeComponent(Component component) {
        components.remove(component);
    }

    public List<Component> getComponents() {
        return components;
    }

    public Component tryGetComponent(Class<?> compClass) {
        for(Component component : components) {
            if(component.getClass() == compClass) {
                return component;
            }
        }
        return null;
    }

}
