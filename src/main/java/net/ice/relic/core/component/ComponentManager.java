package net.ice.relic.core.component;

import net.ice.relic.core.scene.SceneObject;
import org.tinylog.Logger;

public class ComponentManager {

    public ComponentManager() {
    }

    /**
     * Add a component to a scene object.
     * @param component
     * @param sceneObject
     */
    public void addComponent(Component component, SceneObject sceneObject) {
        if(sceneObject.hasComponent(component)) {
            Logger.error("Component: " + component.getName() +" already attached to scene object.");
        }

        component.setAttachedObject(sceneObject);
        sceneObject.addComponent(component);
    }


}
