package net.ice.relic.engine.opengl.scene;

import net.ice.relic.annotations.Rewrite;
import net.ice.relic.engine.RelicApplication;
import net.ice.relic.engine.common.Input;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

@Rewrite
public class Camera {

    private boolean hasUpdated;

    public Matrix4f viewMatrix = new Matrix4f();
    public Matrix4f projectionMatrix = new Matrix4f();
    private Vector3f position = new Vector3f(0,0,0);
    private Quaternionf orientation = new Quaternionf();
    private RelicApplication application;

    public Camera(RelicApplication application) {
        this.application = application;
    }

    public void update(float deltaTime) {
        if (!hasUpdated) {
            float speed = Input.isKeyDown(GLFW_KEY_LEFT_SHIFT) ? 10f : 2f;
            float rotateZ = 0f;
            float rotateX = 0f;
            float rotateY = 0f;

            if (Input.isKeyDown(GLFW_KEY_W)) {
                position.sub(orientation.positiveZ(new Vector3f()).mul(deltaTime * speed));
            }
            if (Input.isKeyDown(GLFW_KEY_S)) {
                position.add(orientation.positiveZ(new Vector3f()).mul(deltaTime * speed));
            }
            if (Input.isKeyDown(GLFW_KEY_A)) {
                position.add(orientation.positiveX(new Vector3f()).mul(deltaTime * -speed));
            }
            if (Input.isKeyDown(GLFW_KEY_D)) {
                position.add(orientation.positiveX(new Vector3f()).mul(deltaTime * speed));
            }
            if (Input.isKeyDown(GLFW_KEY_Q)) {
                rotateZ -= 1f;
            }
            if (Input.isKeyDown(GLFW_KEY_E)) {
                rotateZ += 1f;
            }
            if (Input.isKeyDown(GLFW_KEY_UP)) {
                rotateX -= 1f;
            }
            if (Input.isKeyDown(GLFW_KEY_DOWN)) {
                rotateX += 1f;
            }
            if (Input.isKeyDown(GLFW_KEY_LEFT)) {
                rotateY -= 1f;
            }
            if (Input.isKeyDown(GLFW_KEY_RIGHT)) {
                rotateY += 1f;
            }

            orientation.rotateLocalZ(rotateZ * deltaTime * speed);
            orientation.rotateLocalX(rotateX * deltaTime * speed);
            orientation.rotateLocalY(rotateY * deltaTime * speed);

            viewMatrix.identity()
                    .rotate(orientation)
                    .translate(new Vector3f(position).negate());

            hasUpdated = true;
        }
    }

    public void resize() {
        projectionMatrix.setPerspective((float) Math.toRadians(90), (float) application.getWindow().getWidth() / application.getWindow().getHeight(), 0.1f, 1000.0f);
    }

    public void newFrame() {
        hasUpdated = false;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Quaternionf getOrientation() {
        return orientation;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setOrientation(Quaternionf orientation) {
        this.orientation = orientation;
    }
}
