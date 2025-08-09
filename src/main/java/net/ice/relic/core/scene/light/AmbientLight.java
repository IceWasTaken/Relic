package net.ice.relic.core.scene.light;

import net.ice.relic.engine.util.ColorUtil;
import org.joml.Vector3f;

public class AmbientLight {

    private ColorUtil.Color color;

    private float intensity;

    public AmbientLight(float intensity, Vector3f color) {
        this.intensity = intensity;
        this.color = ColorUtil.glVectorToColor(color);
    }

    public AmbientLight(float intensity, ColorUtil.Color color) {
        this.intensity = intensity;
        this.color = color;
    }

    public AmbientLight() {
        this(1.0f, new Vector3f(0.3f, 0.3f, 0.3f));
    }

    public ColorUtil.Color getColor() {
        return color;
    }

    public float getIntensity() {
        return intensity;
    }

    public AmbientLight setColor(ColorUtil.Color color) {
        this.color = color;
        return this;
    }

    public AmbientLight setColor(Vector3f color) {
        this.color = ColorUtil.glVectorToColor(color);
        return this;
    }

    public AmbientLight setColor(float r, float g, float b) {
        this.color = new ColorUtil.Color(r, g, b);
        return this;
    }

    public AmbientLight setIntensity(float intensity) {
        this.intensity = intensity;
        return this;
    }
}