package net.ice.relic.common.test;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.common.test.scene.TestingScene;
import net.ice.relic.core.Input;
import net.ice.relic.core.Version;
import net.ice.relic.core.config.Config;
import net.ice.relic.core.scene.light.DirectionalLight;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class VulkanTest extends RelicApplication {

    private DirectionalLight dirLight;
    private float angleInc;
    private float lightAngle = 270;

    protected VulkanTest(Config config, Version applicationVersion) {
        super(config, applicationVersion);
    }

    public static void main(String[] args) {
        VulkanTest relicTest = new VulkanTest(new Config(), new Version(0,0,1));
        relicTest.run();
    }

    @Override
    protected void init(RelicApplication application) {
        TestingScene testingScene = new TestingScene("Testing", this);
        this.dirLight = testingScene.getDirectionalLight();
        application.loadScene(testingScene);


    }

    @Override
    protected void update(RelicApplication application) {
        if (Input.isKeyDown(GLFW_KEY_K)) {
            angleInc -= 0.05f * application.getClock().getDeltaTime();
        } else if (Input.isKeyDown(GLFW_KEY_L)) {
            angleInc += 0.05f * application.getClock().getDeltaTime();
        } else {
            angleInc = 0;
        }

        if (angleInc != 0.0) {
            lightAngle += angleInc;
            if (lightAngle < 240) {
                lightAngle = 240;
            } else if (lightAngle > 300) {
                lightAngle = 300;
            }
            updateDirLight();
        }
    }

    @Override
    protected void render(RelicApplication application) {

    }

    @Override
    protected void cleanup(RelicApplication application) {

    }

    private void updateDirLight() {
        float zValue = (float) Math.cos(Math.toRadians(lightAngle));
        float yValue = (float) Math.sin(Math.toRadians(lightAngle));
        Vector3f lightDirection = dirLight.getDirection();
        lightDirection.x = 0;
        lightDirection.y = yValue;
        lightDirection.z = zValue;
        lightDirection.normalize();
    }
}
