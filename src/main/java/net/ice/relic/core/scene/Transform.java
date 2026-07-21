package net.ice.relic.core.scene;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {

    public Vector3f position;
    public Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);

    public Quaternionf rotation;

    public Matrix4f transformMatrix;


    public Transform() {
        position = new Vector3f();
        rotation = new Quaternionf();
        transformMatrix = new Matrix4f();
    }

    public Transform(Vector3f position, Quaternionf rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    private void update() {
        transformMatrix.translate(position)
                .rotate(rotation)
                .scale(scale);
    }

    public Vector3f getPosition() {
        return position;
    }
    public Transform setPosition(Vector3f position) {
        position.set(position);
        update();
        return this;
    }
    public Transform setPosition(float x, float y, float z) {
        position.set(x, y, z);
        update();
        return this;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }
    public void setScale(Vector3f scale) {
        this.scale = scale;
        update();
    }
    public void setScale(float x, float y, float z) {
        this.scale.set(x, y, z);
        update();
    }
    public void setScale(float scale) {
        this.scale.set(scale, scale, scale);
        update();
    }

    public Matrix4f getTransformMatrix() {
        return transformMatrix;
    }
}
