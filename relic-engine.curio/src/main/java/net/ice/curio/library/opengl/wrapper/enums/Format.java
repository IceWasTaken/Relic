package net.ice.curio.library.opengl.wrapper.enums;

import static org.lwjgl.opengl.GL11.*;

public enum Format {

    BYTE(GL_BYTE),
    UNSIGNED_BYTE(GL_UNSIGNED_BYTE),
    SHORT(GL_SHORT),
    UNSIGNED_SHORT(GL_UNSIGNED_SHORT),
    INT(GL_INT),
    UNSIGNED_INT(GL_UNSIGNED_INT),
    FLOAT(GL_FLOAT),
    TWO_BYTES(GL_2_BYTES),
    THREE_BYTES(GL_3_BYTES),
    FOUR_BYTES(GL_4_BYTES),
    DOUBLE(GL_DOUBLE);

    private final int glEnum;

    Format(int glEnum) {
        this.glEnum = glEnum;
    }

    public int getGLEnum() {
        return glEnum;
    }

    public static Format fromValue(int value) {
        for(Format format : Format.values()) {
            if(format.getGLEnum() == value) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unexpected Value");
    }
}
