package net.ice.relic.core.scene.light;

import net.ice.heirloom.color.RGBColor;
import org.joml.Vector3f;

public class DirectionalLight {

    private RGBColor color;
    private Vector3f direction;

    private float intensity;

    public DirectionalLight(RGBColor color, Vector3f direction, float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }

    public RGBColor getColor() {
        return color;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setColor(RGBColor color) {
        this.color = color;
    }

    public void setColor(float r, float g, float b) {
        this.color = new RGBColor(r, g, b);
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public DirectionalLight setIntensity(float intensity) {
        this.intensity = intensity;
        return this;
    }

    public void setPosition(float x, float y, float z) {
        direction.set(x, y, z);
    }
}