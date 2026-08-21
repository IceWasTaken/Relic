package net.ice.curio.library.opengl.wrapper.enums;

import static org.lwjgl.opengl.GL15.*;

public enum Usage {

    STREAM_DRAW(GL_STREAM_DRAW),
    STREAM_READ(GL_STREAM_READ),
    STREAM_COPY(GL_STREAM_COPY),

    STATIC_DRAW(GL_STATIC_DRAW),
    STATIC_READ(GL_STATIC_READ),
    STATIC_COPY(GL_STATIC_COPY),

    DYNAMIC_DRAW(GL_DYNAMIC_DRAW),
    DYNAMIC_READ(GL_DYNAMIC_READ),
    DYNAMIC_COPY(GL_DYNAMIC_COPY);

    private final int glEnum;

    Usage(int glEnum) {
        this.glEnum = glEnum;
    }

    public int getGLEnum() {
        return glEnum;
    }

    public static Usage fromValue(int value) {
        for(Usage usage : Usage.values()) {
            if(usage.getGLEnum() == value) {
                return usage;
            }
        }
        throw new IllegalArgumentException("Unexpected Value");
    }
}
