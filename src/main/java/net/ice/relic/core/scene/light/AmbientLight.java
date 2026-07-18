package net.ice.relic.core.scene.light;

import net.ice.heirloom.color.RGBColor;

public class AmbientLight {

    private RGBColor color;

    private float intensity;

    public AmbientLight(float intensity, RGBColor color) {
        this.intensity = intensity;
        this.color = color;
    }

    public AmbientLight() {
        this(1.0f, new RGBColor(0.3f, 0.3f, 0.3f).mult());
    }

    public RGBColor getColor() {
        return color;
    }

    public float getIntensity() {
        return intensity;
    }

    public AmbientLight setColor(RGBColor color) {
        this.color = color;
        return this;
    }

    public AmbientLight setColor(float r, float g, float b) {
        this.color = new RGBColor(r, g, b);
        return this;
    }

    public AmbientLight setIntensity(float intensity) {
        this.intensity = intensity;
        return this;
    }
}