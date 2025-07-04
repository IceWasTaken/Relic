package net.ice.relic.engine.opengl;

import net.ice.relic.engine.RelicApplication;
import org.joml.Matrix4f;

public class ProjectionMatrix {

    private static final float FOV = (float) Math.toRadians(90.0f);
    private static final float Z_FAR = 1000.f;
    private static final float Z_NEAR = 0.01f;

    private final RelicApplication application;

    private Matrix4f inverseProjectionMatrix;
    private Matrix4f projectionMatrix;

    public ProjectionMatrix(RelicApplication application) {
        this.application = application;

        projectionMatrix = new Matrix4f();
        inverseProjectionMatrix = new Matrix4f();
        updateProjMatrix(application.getWindow().getWidth(), application.getWindow().getHeight());
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
