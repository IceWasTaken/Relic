package net.ice.rune.scenes;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.engine.opengl.model.Model;
import net.ice.relic.core.scene.Scene;
import net.ice.relic.core.scene.SceneObject;
import net.ice.relic.core.scene.Skybox;
import net.ice.rune.GuiTest;
import org.joml.Vector3f;

import java.util.Random;

public class SceneTest extends Scene {

    private int count = 0;
    private Random random;

    public SceneTest(String name, RelicApplication application) {
        super(name, application);
    }

    @Override
    protected void sceneInit() {
        this.random = new Random();
        Skybox skybox = new Skybox("skybox/skybox.obj", modelLoader);
        skybox.getSceneObject().getTransform().setScale(500);
        setSkybox(skybox);

        Model moonModel = modelLoader.loadModel("test", "3DSMoon/moon.obj", false);
        SceneObject moonSceneObject = new SceneObject("moon", moonModel);
        moonSceneObject.getTransform().setScale(0.00001f);
        addSceneObject("test", moonSceneObject);
        //addSceneObject("terrain", new SceneObject("terrain", new Terrain(233344444, application.getMaterialCache(), application.getTextureCache(), application.getModelCache()).getModel()));


        getDirectionalLight().setIntensity(1);
        getDirectionalLight().setDirection(new Vector3f(0,1,0));
        this.setGUI(new GuiTest());
    }

    @Override
    protected void sceneUpdate(float deltaTime) {
//        if(Input.isKeyDown(GLFW_KEY_T)) {
//            addSceneObject("moon" + count, new SceneObject("moon" + count, modelLoader.loadModel("test", "3DSMoon/moon.obj", false)));
//            getObject("moon" + count).getTransform().setScale(0.0001f);
//            getObject("moon" + count).getTransform().setPosition(new Vector3f(random.nextFloat(), 0, random.nextFloat()));
//            count++;
//        }
    }

    @Override
    protected void sceneDestroy() {

    }
}
