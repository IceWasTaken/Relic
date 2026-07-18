package net.ice.relic.core.scene.light;

import net.ice.heirloom.color.RGBColor;
import net.ice.relic.core.rendering.DebugRenderable;
import org.joml.Vector3f;

public class Light implements DebugRenderable {
    private RGBColor color;
    private Vector3f position;
    private float intensity;

    private final boolean directional;

    public Light(Vector3f position, boolean directional, float intensity, RGBColor color) {
        this.position = position;
        this.directional = directional;
        this.intensity = intensity;
        this.color = color;
    }

    public RGBColor getColor() {
        return color;
    }

    public float getIntensity() {
        return intensity;
    }

    public Vector3f getPosition() {
        return position;
    }

    public boolean isDirectional() {
        return directional;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setColor(RGBColor color) {
        this.color = color;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    @Override
    public RGBColor getDebugColor() {
        return color;
    }

    @Override
    public Vector3f getDebugPos() {
        return position;
    }

    @Override
    public Vector3f getDebugScale() {
        return new Vector3f(2);
    }
}
