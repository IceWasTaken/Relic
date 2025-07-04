package net.ice.rune.scenes;

import net.ice.relic.engine.opengl.model.ModelLoader;
import net.ice.relic.engine.opengl.registry.ModelRegistry;
import net.ice.relic.engine.opengl.scene.Scene;
import net.ice.relic.engine.opengl.scene.SceneObject;

public class SceneTest extends Scene {

    public SceneTest(String name) {
        super(name);
    }

    @Override
    protected void sceneInit() {
        addObject("test", new SceneObject("test", ModelLoader.loadModelFromFile("test", "EmissiveStrengthTest1.gltf", this.getTextureLoader(), this.getMaterialCache(), ModelRegistry.DEFAULT_FLAGS)));
    }
}
