package net.ice.rune.scenes;

import net.ice.relic.common.Input;
import net.ice.relic.engine.opengl.AnimationData;
import net.ice.relic.engine.opengl.Shader;
import net.ice.relic.engine.opengl.ShaderProgram;
import net.ice.relic.engine.opengl.model.ModelLoader;
import net.ice.relic.engine.opengl.registry.ModelRegistry;
import net.ice.relic.common.scene.Scene;
import net.ice.relic.common.scene.SceneObject;
import net.ice.relic.common.scene.Skybox;
import net.ice.relic.common.scene.light.DirLight;
import net.ice.rune.GuiTest;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class SceneTest extends Scene {

    private AnimationData animationData;
    private ShaderProgram negativeShader;
    private float lightAngle = 45f;
    private DirLight dirLight;

    public SceneTest(String name) {
        super(name);
    }

    @Override
    protected void sceneInit() {
        Skybox skybox = new Skybox("skybox/skybox.obj", this.getTextureLoader(), this.getMaterialCache());
        skybox.getSceneObject().setScaleFactor(500);
        skybox.getSceneObject().update();
        setSkybox(skybox);

        List<Shader> shaders = new ArrayList<>();
        shaders.add(Shader.loadShader("post/fxaa.vert", Shader.ShaderType.VERTEX, true));
        shaders.add(Shader.loadShader("post/fxaa.frag", Shader.ShaderType.FRAGMENT, true));
        negativeShader = new ShaderProgram(shaders);

        addObject("moon", new SceneObject("moon", ModelLoader.loadModel("test", "headcrab/headcrab_classic/headcrab_classic.obj", this.getTextureLoader(), this.getMaterialCache(), ModelRegistry.DEFAULT_FLAGS)).setPosition(0,0,0).setScaleFactor(20f));

        this.dirLight = this.getLights().getDirLight();
        this.getLights().getDirLight().setIntensity(1).setDirection(new Vector3f(0,1,0));
        this.setLights(this.getLights());
        this.setGUI(new GuiTest());
    }

    @Override
    protected void sceneUpdate(float deltaTime) {
        //loadShader(negativeShader);

        if (Input.isKeyDown(GLFW_KEY_K)) {
            lightAngle -= 2.5f;

        } else if (Input.isKeyDown(GLFW_KEY_L)) {
            lightAngle += 2.5f;
        }

        double angRad = Math.toRadians(lightAngle);
        dirLight.getDirection().z = (float) Math.sin(angRad);
        dirLight.getDirection().y = (float) Math.cos(angRad);
    }

    public AnimationData getAnimationData() {
        return animationData;
    }
}
