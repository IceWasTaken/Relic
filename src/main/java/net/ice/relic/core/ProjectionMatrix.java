package net.ice.relic.core;

import net.ice.relic.application.RelicApplication;
import org.joml.Matrix4f;

public class ProjectionMatrix {

    private static final float FOV = (float) Math.toRadians(90.0f);
    private static final float Z_FAR = 10000.f;
    private static final float Z_NEAR = 0.01f;

    private final RelicApplication application;

    private final Matrix4f inverseProjectionMatrix;
    private final Matrix4f projectionMatrix;

    public ProjectionMatrix(RelicApplication application) {
        this.application = application;

        projectionMatrix = new Matrix4f();
        inverseProjectionMatrix = new Matrix4f();
    }

    public ProjectionMatrix init() {
        updateProjMatrix(application.getWindow().getWidth(), application.getWindow().getHeight());
        return this;
    }

    public Matrix4f getInvProjMatrix() {
        return inverseProjectionMatrix;
    }

    public Matrix4f getProjMatrix() {
        return projectionMatrix;
    }

    public void updateProjMatrix(int width, int height) {
        projectionMatrix.setPerspective(FOV, (float) width / height, Z_NEAR, Z_FAR);
        inverseProjectionMatrix.set(projectionMatrix).invert();
    }
}
