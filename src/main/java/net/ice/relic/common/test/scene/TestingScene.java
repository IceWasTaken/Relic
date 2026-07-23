package net.ice.relic.common.test.scene;

import net.ice.heirloom.color.Colors;
import net.ice.relic.application.RelicApplication;
import net.ice.heirloom.io.resource.Resource;
import net.ice.relic.common.test.gui.DebugGui;
import net.ice.relic.core.scene.Scene;
import net.ice.relic.core.scene.SceneObject;
import net.ice.relic.core.scene.light.Light;
import org.joml.Vector3f;

public class TestingScene extends Scene {

    public TestingScene(String name, RelicApplication application) {
        super(name, application);
    }

    @Override
    protected void sceneInit() {
        //setSkybox(new Skybox("assets/models/skybox4/skybox.glb", getModelLoader()));

        SceneObject sceneObject = new SceneObject("sponza", getModelLoader().loadModel(Resource.getResource("relic", "assets/models/sponza/Sponza.gltf"), false));
        //SceneObject sceneObject1 = new SceneObject("blahaj", getModelLoader().loadModel(Resource.getResource("relic", "resources/assets/models/san_miguel/san-miguel.obj"), false));
        sceneObject.getTransform().setPosition(0,0,0);
        //sceneObject1.getTransform().setPosition(0,0,0);
        //sceneObject1.getTransform().setScale(200f);

        addSceneObject("sponza", sceneObject);
        //addSceneObject("blahaj", sceneObject1);

        getLights().add(new Light(new Vector3f(1, -1, 2), false, 2, Colors.GRAY.getRGBColor()));
        setGUI(new DebugGui(application));

        camera.setPosition(-5, 3, 0);
        camera.setRotation((float) Math.toRadians(20.0f), (float) Math.toRadians(90));
    }

    @Override
    protected void sceneUpdate(float deltaTime) {
    }

    @Override
    protected void sceneDestroy() {

    }

}
