package net.ice.relic.common.test.scene;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.common.test.gui.DebugGui;
import net.ice.relic.core.resource.Resource;
import net.ice.relic.core.scene.Scene;
import net.ice.relic.core.scene.SceneObject;
import net.ice.relic.core.scene.Skybox;
import net.ice.relic.core.scene.light.PointLight;
import org.joml.Vector3f;

public class TestingScene extends Scene {

    public TestingScene(String name, RelicApplication application) {
        super(name, application);
    }

    @Override
    protected void sceneInit() {
        setSkybox(new Skybox("assets/models/skybox4/skybox.glb", getModelLoader()));

        SceneObject sceneObject = new SceneObject("blahaj", getModelLoader().loadModel("blahaj", Resource.getResourceWithDefaultNamespace("/assets/models/sponza/sponza.gltf"), false));
        sceneObject.getTransform().setScale(0.01f);
        addSceneObject("blahaj", sceneObject);

        //getPointLights().add(new PointLight(new Vector3f(0,1,0), new Vector3f(0,2,0), 10));

        setGUI(new DebugGui(application));

    }

    @Override
    protected void sceneUpdate(float deltaTime) {

    }

    @Override
    protected void sceneDestroy() {

    }
}
