package net.ice.relic.engine.opengl.scene;

import net.ice.relic.annotations.Rewrite;
import net.ice.relic.engine.RelicApplication;
import net.ice.relic.engine.common.Input;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

@Rewrite
public class Camera {

    private boolean hasUpdated;

    private float pitch = 0f; // vertical rotation
    private float yaw = 0f;

    public Matrix4f viewMatrix = new Matrix4f();
    public Matrix4f projectionMatrix = new Matrix4f();
    private Vector3f position = new Vector3f(0,0,10);
    private Quaternionf orientation = new Quaternionf();
    private RelicApplication application;

    public Camera(RelicApplication application) {
        this.application = application;
    }

    public void update(float deltaTime) {
        if (!hasUpdated) {
            Input input = application.getInput();
            Vector2f currMousePos = input.getMousePosition();
            Vector2f prevMousePos = input.getPrevMousePosition();

            Vector2f delta = new Vector2f(currMousePos).sub(prevMousePos);

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

            if (Input.getMouseButtonsDown().contains(GLFW_MOUSE_BUTTON_RIGHT)) {
                float sensitivity = application.getConfig().getPlayerConfig().getMouseSensitivity();

                yaw   += delta.x * sensitivity * deltaTime;
                pitch += delta.y * sensitivity * deltaTime;

                pitch = Math.max((float)Math.toRadians(-89), Math.min((float)Math.toRadians(89), pitch));
            }

            orientation.identity()
                    .rotateY(yaw)
                    .rotateX(pitch)
                    .rotateZ(rotateZ * deltaTime * speed);

            viewMatrix.identity()
                    .rotate(orientation)
                    .translate(new Vector3f(position).negate());

            hasUpdated = true;
            input.getPrevMousePosition().set(currMousePos);
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