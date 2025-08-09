package net.ice.relic.core.component;

import net.ice.relic.core.scene.SceneObject;

public abstract class Component {

    private boolean active;

    private SceneObject attachedObject;

    public Component() {
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public void setAttachedObject(SceneObject attachedObject) {
        this.attachedObject = attachedObject;
    }

    public String toString() {
        return String.format("%s: %s", getName(), attachedObject.getName());
    }
}
