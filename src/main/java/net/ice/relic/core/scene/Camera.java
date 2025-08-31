package net.ice.relic.core.scene;

import net.ice.relic.common.annotations.Rewrite;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.Input;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

@Rewrite
public class Camera {

    private boolean hasUpdated;

    public Matrix4f viewMatrix = new Matrix4f();
    private Vector3f position = new Vector3f(0,0,0);
    private Quaternionf orientation = new Quaternionf();

    public Camera(RelicApplication application) {
    }

    public void update(float deltaTime) {
        if (!hasUpdated) {
            float speed = Input.isKeyDown(GLFW_KEY_LEFT_SHIFT) ? 20f : 5f;
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

            orientation.rotateLocalZ(rotateZ * deltaTime * 2);
            orientation.rotateLocalX(rotateX * deltaTime * 2);
            orientation.rotateLocalY(rotateY * deltaTime * 2);

            viewMatrix.identity()
                    .rotate(orientation)
                    .translate(new Vector3f(position).negate());

            hasUpdated = true;
        }
    }

    public void newFrame() {
        hasUpdated = false;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
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