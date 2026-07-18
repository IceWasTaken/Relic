package net.ice.relic.common.test;

import net.ice.curio.input.Input;
import net.ice.heirloom.ApplicationProperties;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.common.test.scene.TestingScene;
import net.ice.heirloom.Version;
import net.ice.relic.core.scene.Camera;
import net.ice.relic.core.scene.Scene;
import net.ice.relic.core.scene.light.Light;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class VulkanTest extends RelicApplication {

    private Light dirLight;
    private float angleInc;
    private float lightAngle = 270;
    protected VulkanTest() {
        super(new ApplicationProperties(
                "Relic Application Test",
                new Version(0, 0, 1),
                new Version(0, 5, 0),
                true
        ));
    }

    public static void main(String[] args) {
        new VulkanTest().run();
    }

    @Override
    protected void init(RelicApplication application) {
        TestingScene testingScene = new TestingScene("Testing", this);
        this.dirLight = testingScene.getLights().getFirst();
        application.loadScene(testingScene);
    }

    @Override
    protected void update(RelicApplication application) {
        Scene scene = application.getCurrentScene();
        float moveSpeed = application.getClock().getDeltaTime() * 4f;
        moveSpeed = Input.isKeyDown(GLFW_KEY_LEFT_SHIFT) ? moveSpeed * 2 : moveSpeed;
        Camera camera = scene.getCamera();

        if (Input.isKeyDown(GLFW_KEY_W)) {
            camera.moveForward(moveSpeed);
        } else if (Input.isKeyDown(GLFW_KEY_S)) {
            camera.moveBackwards(moveSpeed);
        }
        if (Input.isKeyDown(GLFW_KEY_A)) {
            camera.moveLeft(moveSpeed);
        } else if (Input.isKeyDown(GLFW_KEY_D)) {
            camera.moveRight(moveSpeed);
        }
        if (Input.isKeyDown(GLFW_KEY_UP)) {
            camera.moveUp(moveSpeed);
        } else if (Input.isKeyDown(GLFW_KEY_DOWN)) {
            camera.moveDown(moveSpeed);
        }

        if(Input.getInstance().getMouseButtonsDown().contains(GLFW_MOUSE_BUTTON_RIGHT)) {
            Vector2f deltaPos = Input.getInstance().getMouseDelta();
            camera.addRotation((float) Math.toRadians(-deltaPos.y * 0.1f),
                    (float) Math.toRadians(-deltaPos.x * 0.1f));
        }

        if (Input.isKeyDown(GLFW_KEY_K)) {
            angleInc -= 0.05f;
        } else if (Input.isKeyDown(GLFW_KEY_L)) {
            angleInc += 0.05f;
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
        Vector3f lightDirection = dirLight.getPosition();
        lightDirection.x = 0;
        lightDirection.y = yValue;
        lightDirection.z = zValue;
        lightDirection.normalize();
    }


}
