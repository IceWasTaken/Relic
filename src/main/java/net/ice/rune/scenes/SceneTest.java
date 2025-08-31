package net.ice.rune.scenes;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.component.physics.GravitySourceComponent;
import net.ice.relic.core.component.physics.RigidBodyComponent;
import net.ice.relic.core.rendering.backend.opengl.model.Model;
import net.ice.relic.core.scene.Scene;
import net.ice.relic.core.scene.SceneObject;
import net.ice.relic.core.scene.Skybox;
import net.ice.rune.GuiTest;
import net.ice.rune.console.Console;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
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

        //Model moonModel = modelLoader.loadModel("test", "3DSMoon/moon.obj", false);
        Model gravSrc = modelLoader.loadModel("gravSrc", "Vort/Vortigaunt/uncombined_vortigaunt.dae", false);
        SceneObject gravSrcObj = new SceneObject("gravSrc", gravSrc);
        gravSrcObj.getTransform().setPosition(0,0,0);
        gravSrcObj.getTransform().setScale(1f);
        //gravSrcObj.addComponent(new GravitySourceComponent(100f, 200f));
        addSceneObject("test1",  gravSrcObj);


        Model moonModel = modelLoader.loadModel("test", "blahaj/Shark.obj" , false);
        SceneObject moonSceneObject = new SceneObject("moon", moonModel);
        moonSceneObject.getTransform().setScale(1f);
        addSceneObject("test", moonSceneObject);
        //RigidBodyComponent component = new RigidBodyComponent();
        //component.enableGravity();
        //moonSceneObject.addComponent(component);


        //addSceneObject("terrain", new SceneObject("terrain", new Terrain(233344444, application.getMaterialCache(), application.getTextureCache(), application.getModelCache()).getModel()));


        getDirectionalLight().setIntensity(1);
        getDirectionalLight().setDirection(new Vector3f(0,1,0));
        this.setGUI(new GuiTest(new Console()));
    }

    @Override
    protected void sceneUpdate(float deltaTime) {
//        SceneObject model = getObject("test");
//        SceneObject gravSrc = getObject("test1");
//
//        RigidBodyComponent rb = (RigidBodyComponent) model.tryGetComponent(RigidBodyComponent.class);
//        GravitySourceComponent src = (GravitySourceComponent) gravSrc.tryGetComponent(GravitySourceComponent.class);
//
//        if (rb != null && src != null) {
//            List<Vector3f> accels = new ArrayList<>();
//            accels.add(src.getAcceleration(model.getTransform().getPosition()));
//            rb.updateAccelAtPos(accels);
//            rb.update(deltaTime);
//        }

        for(SceneObject sceneObject : getObjects().values()) {
            sceneObject.update(deltaTime);
        }
    }


    @Override
    protected void sceneDestroy() {

    }
}
