package net.ice.relic.engine.opengl;

import net.ice.relic.engine.Window;
import net.ice.relic.engine.common.Clock;
import net.ice.relic.engine.common.Input;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    private boolean hasUpdated;

    public Matrix4f viewMatrix = new Matrix4f();
    public Matrix4f projectionMatrix = new Matrix4f();

    private final Vector3f position = new Vector3f(0,2,5);
    private final Quaternionf orientation = new Quaternionf();

    public Camera() {
        //projectionMatrix.setPerspective((float) Math.toRadians(60), (float) window.getOptions().width() / window.getOptions().height(), 0.1f, 1000.0f);
    }

    public void update(Clock clock) {
        if (!hasUpdated) {
            float speed = Input.isKeyDown(GLFW_KEY_LEFT_SHIFT) ? 10f : 2f;
            float rotateZ = 0f;
            float rotateX = 0f;
            float rotateY = 0f;

            if (Input.isKeyDown(GLFW_KEY_W)) {
                position.sub(orientation.positiveZ(new Vector3f()).mul(clock.getDeltaTime() * speed));
            }
            if (Input.isKeyDown(GLFW_KEY_S)) {
                position.add(orientation.positiveZ(new Vector3f()).mul(clock.getDeltaTime() * speed));
            }
            if (Input.isKeyDown(GLFW_KEY_A)) {
                position.add(orientation.positiveX(new Vector3f()).mul(clock.getDeltaTime() * -speed));
            }
            if (Input.isKeyDown(GLFW_KEY_D)) {
                position.add(orientation.positiveX(new Vector3f()).mul(clock.getDeltaTime() * speed));
            }
            if (Input.isKeyDown(GLFW_KEY_E)) {
                rotateZ += 1f;
            }
            if (Input.isKeyDown(GLFW_KEY_Q)) {
                rotateZ -= 1f;
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

            orientation.rotateLocalZ(rotateZ * clock.getDeltaTime() * speed);
            orientation.rotateLocalX(rotateX * clock.getDeltaTime() * speed);
            orientation.rotateLocalY(rotateY * clock.getDeltaTime() * speed);

            viewMatrix.identity()
                    .rotate(orientation)
                    .translate(new Vector3f(position).negate());

            hasUpdated = true;
        }
    }

    public void resize(Window window) {
        projectionMatrix.setPerspective((float) Math.toRadians(60), (float) window.getOptions().width() / window.getOptions().height(), 0.1f, 1000.0f);
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
}
