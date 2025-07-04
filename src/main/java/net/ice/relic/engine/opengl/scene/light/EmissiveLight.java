package net.ice.relic.engine.opengl.scene.light;

import org.joml.Vector3f;

public class EmissiveLight {

    private final Vector3f position;
    private final Vector3f color;
    private final float intensity;

    public EmissiveLight(Vector3f position, Vector3f color, float intensity) {
        this.position = position;
        this.color = color;
        this.intensity = intensity;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getColor() {
        return color;
    }

    public float getIntensity() {
        return intensity;
    }

}
