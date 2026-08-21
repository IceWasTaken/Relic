package net.ice.relic.core;

import net.ice.relic.application.RelicApplication;
import org.joml.Matrix4f;

public class ProjectionMatrix {

    public static final float FOV = (float) Math.toRadians(90.0f);
    public static final float Z_FAR = 50.0f;
    public static final float Z_NEAR = 0.5f;

    private static boolean shouldResize = false;

    private final RelicApplication application;
    private final Matrix4f projectionMatrix;

    public ProjectionMatrix(RelicApplication application) {
        this.application = application;

        projectionMatrix = new Matrix4f();
        updateProjMatrix(application.getWindow().getWidth(), application.getWindow().getHeight());
    }

    public void update() {
        //updateProjMatrix(application.getWindow().getWidth(), application.getWindow().getHeight());
    }

    public Matrix4f getProjMatrix() {
        return projectionMatrix;
    }

    public void updateProjMatrix(int width, int height) {
        projectionMatrix.identity();
        projectionMatrix.perspective(FOV, (float) width / (float) height, Z_NEAR, Z_FAR, true);
    }
}
