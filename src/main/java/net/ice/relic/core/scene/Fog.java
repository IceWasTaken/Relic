package net.ice.relic.core.scene;

import net.ice.heirloom.color.Colors;
import net.ice.heirloom.color.RGBColor;

public class Fog {

    private RGBColor color;

    private boolean active;
    private float density;

    public Fog() {
        active = false;
        color = Colors.BLACK.getRGBColor();
    }

    public Fog(boolean active, RGBColor color, float density) {
        this.color = color;
        this.density = density;
        this.active = active;
    }

    public RGBColor getColor() {
        return color;
    }

    public float getDensity() {
        return density;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setColor(RGBColor color) {
        this.color = color;
    }

    public void setDensity(float density) {
        this.density = density;
    }
}
