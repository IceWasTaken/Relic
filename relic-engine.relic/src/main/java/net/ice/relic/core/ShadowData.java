package net.ice.relic.core;

import org.joml.Matrix4f;

public class ShadowData {

    private final Matrix4f projViewMatrix;
    private float splitDistance;

    public ShadowData() {
        projViewMatrix = new Matrix4f();
    }

    public Matrix4f getProjViewMatrix() {
        return projViewMatrix;
    }

    public float getSplitDistance() {
        return splitDistance;
    }

    public void setProjViewMatrix(Matrix4f projViewMatrix) {
        this.projViewMatrix.set(projViewMatrix);
    }

    public void setSplitDistance(float splitDistance) {
        this.splitDistance = splitDistance;
    }
}
