package net.ice.relic.common.scene;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {

    public Vector3f position;
    public Quaternionf rotation;
    public Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);
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

    public void update() {
        transformMatrix.identity()
                .translate(position)
                .rotate(rotation)
                .scale(scale);
    }

    public Vector3f getPosition() {
        return position;
    }
    public Transform setPosition(Vector3f position) {
        this.position = position;
        return this;
    }
    public Transform setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
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
    }
    public void setScale(float x, float y, float z) {
        this.scale.set(x, y, z);
    }
    public void setScale(float scale) {
        this.scale.set(scale, scale, scale);
    }

    public Matrix4f getTransformMatrix() {
        return transformMatrix;
    }
}
