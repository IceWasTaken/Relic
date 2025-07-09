package net.ice.rune.scenes;

import net.ice.relic.engine.opengl.AnimationData;
import net.ice.relic.engine.opengl.model.Model;
import net.ice.relic.engine.opengl.model.ModelLoader;
import net.ice.relic.engine.opengl.registry.ModelRegistry;
import net.ice.relic.engine.opengl.scene.Scene;
import net.ice.relic.engine.opengl.scene.SceneObject;
import net.ice.relic.engine.opengl.scene.Skybox;

public class SceneTest extends Scene {

    private AnimationData animationData;

    public SceneTest(String name) {
        super(name);
    }

    @Override
    protected void sceneInit() {
        Skybox skybox = new Skybox("skybox/skybox.obj", this.getTextureLoader(), this.getMaterialCache());
        skybox.getSceneObject().setScaleFactor(100);
        skybox.getSceneObject().update();
        setSkybox(skybox);

        //addObject("test", new SceneObject("test", ModelLoader.loadModelFromFile("test", "NormalTangentTest.gltf", this.getTextureLoader(), this.getMaterialCache(), ModelRegistry.DEFAULT_FLAGS)));
        //addObject("test1", new SceneObject("test1", ModelLoader.loadModelFromFile("test", "EmissiveStrengthTest1.gltf", this.getTextureLoader(), this.getMaterialCache(), ModelRegistry.DEFAULT_FLAGS)).setPosition(10,0,0));
        //addObject("test1", new SceneObject("test1", ModelLoader.loadModelFromFile("test", "VahRuta/ruta.dae", this.getTextureLoader(), this.getMaterialCache(), ModelRegistry.DEFAULT_FLAGS)).setPosition(10,0,0));
        //addObject("moon", new SceneObject("moon", ModelLoader.loadModelFromFile("test", "portal2GLADOS/GLaDOS.dae", this.getTextureLoader(), this.getMaterialCache(), ModelRegistry.DEFAULT_FLAGS)).setPosition(0,0,0).setScaleFactor(1));

//        addObject("skyloft1", new SceneObject("skyloft1", ModelLoader.loadModel("skyloft1", "skyloft/model0.dae", this.getTextureLoader(), this.getMaterialCache(), ModelRegistry.DEFAULT_FLAGS)).setPosition(0,0,0).setScaleFactor(1));
//        addObject("skyloft2", new SceneObject("skyloft2", ModelLoader.loadModel("skyloft2", "skyloft/model0_s.dae", this.getTextureLoader(), this.getMaterialCache(), ModelRegistry.DEFAULT_FLAGS)).setPosition(0,0,0).setScaleFactor(1));
//        addObject("skyloft3", new SceneObject("skyloft3", ModelLoader.loadModel("skyloft3", "skyloft/StageF000Blade.dae", this.getTextureLoader(), this.getMaterialCache(), ModelRegistry.DEFAULT_FLAGS)).setPosition(0,0,0).setScaleFactor(1));
//        addObject("skyloft8", new SceneObject("skyloft8", ModelLoader.loadModel("skyloft8", "skyloft/StageF000Harp.dae", this.getTextureLoader(), this.getMaterialCache(), ModelRegistry.DEFAULT_FLAGS)).setPosition(0,0,0).setScaleFactor(1));
//        addObject("skyloft4", new SceneObject("skyloft4", ModelLoader.loadModel("skyloft4", "skyloft/StageF000MallCover.dae", this.getTextureLoader(), this.getMaterialCache(), ModelRegistry.DEFAULT_FLAGS)).setPosition(0,0,0).setScaleFactor(1));
//        addObject("skyloft5", new SceneObject("skyloft5", ModelLoader.loadModel("skyloft5", "skyloft/StageF000Water0.dae", this.getTextureLoader(), this.getMaterialCache(), ModelRegistry.DEFAULT_FLAGS)).setPosition(0,0,0).setScaleFactor(1));
//        addObject("skyloft6", new SceneObject("skyloft6", ModelLoader.loadModel("skyloft6", "skyloft/StageF000Water2.dae", this.getTextureLoader(), this.getMaterialCache(), ModelRegistry.DEFAULT_FLAGS)).setPosition(0,0,0).setScaleFactor(1));
//        addObject("skyloft7", new SceneObject("skyloft7", ModelLoader.loadModel("skyloft7", "skyloft/StageF000MallCover.dae", this.getTextureLoader(), this.getMaterialCache(), ModelRegistry.DEFAULT_FLAGS)).setPosition(0,0,0).setScaleFactor(1));
//        addObject("skyloft9", new SceneObject("skyloft9", ModelLoader.loadModel("skyloft9", "skyloft/StageF000Light.obj", this.getTextureLoader(), this.getMaterialCache(), ModelRegistry.DEFAULT_FLAGS)).setPosition(0,0,0).setScaleFactor(1));

        addObject("moon", new SceneObject("moon", ModelLoader.loadModel("test", "headcrab/headcrab_classic/headcrab_classic.obj", this.getTextureLoader(), this.getMaterialCache(), ModelRegistry.DEFAULT_FLAGS)).setPosition(0,0,0).setScaleFactor(20f));
    }

    public AnimationData getAnimationData() {
        return animationData;
    }
}
