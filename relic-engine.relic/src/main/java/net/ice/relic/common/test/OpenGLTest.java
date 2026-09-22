package net.ice.relic.common.test;

import net.ice.curio.config.RendererConfig;
import net.ice.curio.config.enums.BackendType;
import net.ice.curio.input.Input;
import net.ice.curio.input.enums.MouseButton;
import net.ice.heirloom.application.ApplicationProperties;
import net.ice.heirloom.Version;
import net.ice.relic.RelicApplication;
import net.ice.relic.common.test.scene.TestingScene;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.scene.Camera;
import net.ice.relic.core.scene.Scene;
import net.ice.relic.core.scene.light.Light;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static net.ice.curio.input.enums.Key.*;

public class OpenGLTest extends RelicApplication {

    private Light dirLight;
    private float angleInc;
    private float lightAngle = 270;

    protected OpenGLTest(String[] arguments) {
        RendererConfig.setBackendType(BackendType.OPENGL);

        super(new ApplicationProperties(
                "Relic Application Test",
                new Version(0, 0, 1),
                new Version(0, 6, 0),
                arguments
        ));
    }

    @Override
    protected void init(RelicApplication application) {

        TestingScene testingScene = new TestingScene("Testing", this);
        this.dirLight = testingScene.getLights().getFirst();
        application.loadScene(testingScene);

        for(Entity entity : testingScene.getEntities()) {
            ((GLRenderer) application.getRenderer()).getBufferManager().loadEntity(entity);
        }
    }


    @Override
    protected void update(RelicApplication application) {
        Scene scene = application.getCurrentScene();
        float moveSpeed = application.getClock().getDeltaTime() * 4f;
        moveSpeed = Input.isKeyDown(KEY_LEFT) ? moveSpeed * 2 : moveSpeed;
        Camera camera = scene.getCamera();

        if (Input.isKeyDown(KEY_W)) {
            camera.moveForward(moveSpeed);
        } else if (Input.isKeyDown(KEY_S)) {
            camera.moveBackwards(moveSpeed);
        }
        if (Input.isKeyDown(KEY_A)) {
            camera.moveLeft(moveSpeed);
        } else if (Input.isKeyDown(KEY_D)) {
            camera.moveRight(moveSpeed);
        }
        if (Input.isKeyDown(KEY_UP)) {
            camera.moveUp(moveSpeed);
        } else if (Input.isKeyDown(KEY_DOWN)) {
            camera.moveDown(moveSpeed);
        }


        if(Input.getInstance().getMouseButtonsDown().contains(MouseButton.BUTTON_RIGHT)) {
            Vector2f deltaPos = Input.getInstance().getMouseDelta();
            camera.addRotation((float) Math.toRadians(-deltaPos.y * 0.1f),
                    (float) Math.toRadians(-deltaPos.x * 0.1f));
        }

        if (Input.isKeyDown(KEY_K)) {
            angleInc -= 0.05f;
        } else if (Input.isKeyDown(KEY_L)) {
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

    static void main(String[] args) {
        new OpenGLTest(args).run();
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
