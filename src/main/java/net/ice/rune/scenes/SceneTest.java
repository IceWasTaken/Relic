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
        //addObject("test", new SceneObject("test", ModelLoader.loadModelFromFile("test", "NormalTangentTest.gltf", this.getTextureLoader(), this.getMaterialCache(), ModelRegistry.DEFAULT_FLAGS)));
        //addObject("test1", new SceneObject("test1", ModelLoader.loadModelFromFile("test", "EmissiveStrengthTest1.gltf", this.getTextureLoader(), this.getMaterialCache(), ModelRegistry.DEFAULT_FLAGS)).setPosition(10,0,0));
        addObject("test1", new SceneObject("test1", ModelLoader.loadModelFromFile("test", "VahRuta/ruta.dae", this.getTextureLoader(), this.getMaterialCache(), ModelRegistry.DEFAULT_FLAGS)).setPosition(10,0,0));
    }
}
