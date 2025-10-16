package net.ice.relic.core.rendering.backend.opengl.enums;

import static org.lwjgl.opengl.GL15.*;

public enum DrawType {

    STATIC(GL_STATIC_DRAW),
    DYNAMIC(GL_DYNAMIC_DRAW),
    STREAM(GL_STREAM_DRAW);

    private int raw;

    DrawType(int raw) {
        this.raw = raw;
    }

    public int raw() {
        return raw;
    }
}
