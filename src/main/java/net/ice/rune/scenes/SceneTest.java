package net.ice.rune.scenes;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.engine.opengl.model.ModelLoader;
import net.ice.relic.engine.opengl.registry.ModelRegistry;
import net.ice.relic.common.scene.Scene;
import net.ice.relic.common.scene.SceneObject;
import net.ice.relic.common.scene.Skybox;
import net.ice.relic.common.scene.light.DirectionalLight;
import net.ice.relic.engine.util.ColorUtil;
import net.ice.relic.generation.Terrain;
import net.ice.rune.GuiTest;
import org.joml.Vector3f;

public class SceneTest extends Scene {

    private float lightAngle = 45f;

    private final DirectionalLight sunLight = new DirectionalLight(ColorUtil.ColorDefaults.SUN_NOON.getColor(), new Vector3f(0, 1, 0), 1);
    private final DirectionalLight moonLight = new DirectionalLight(ColorUtil.ColorDefaults.MOON_NIGHT.getColor(), new Vector3f(0, -1, 0), 1);

    public SceneTest(String name, RelicApplication application) {
        super(name, application);
    }

    @Override
    protected void sceneInit() {
        Skybox skybox = new Skybox("skybox/skybox.obj", this.getTextureCache(), this.getMaterialCache(), this.getModelCache());
        skybox.getSceneObject().getTransform().setScale(500);
        setSkybox(skybox);

        //addSceneObject("moon", new SceneObject("moon", ModelLoader.loadModel("test", "headcrab/headcrab_classic/headcrab_classic.obj", this.getTextureCache(), this.getMaterialCache(), this.getModelCache(), ModelRegistry.DEFAULT_FLAGS)));
        addSceneObject("terrain", new SceneObject("terrain", new Terrain(233344444, this.getMaterialCache(), this.getTextureCache(), this.getModelCache()).getModel()));

        getDirectionalLight().setIntensity(1);
        getDirectionalLight().setDirection(new Vector3f(0,1,0));
        this.setGUI(new GuiTest());
    }

    @Override
    protected void sceneUpdate(float deltaTime) {

    }

    @Override
    protected void sceneDestroy() {

    }
}
