package net.ice.relic.core.scene;

import net.ice.relic.common.util.ColorUtil;

public class Fog {

    private ColorUtil.Color color;

    private boolean active;
    private float density;

    public Fog() {
        active = false;
        color = ColorUtil.ColorDefaults.BLACK.getColor();
    }

    public Fog(boolean active, ColorUtil.Color color, float density) {
        this.color = color;
        this.density = density;
        this.active = active;
    }

    public ColorUtil.Color getColor() {
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

    public void setColor(ColorUtil.Color color) {
        this.color = color;
    }

    public void setDensity(float density) {
        this.density = density;
    }
}
