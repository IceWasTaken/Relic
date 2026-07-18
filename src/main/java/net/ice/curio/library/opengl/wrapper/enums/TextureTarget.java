package net.ice.curio.library.opengl.wrapper.enums;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public enum TextureTarget {

    TEXTURE_2D(GL_TEXTURE_2D);

    private final int glEnum;

    TextureTarget(int glEnum) {
        this.glEnum = glEnum;
    }

    public int getGLEnum() {
        return glEnum;
    }

    public static TextureTarget fromValue(int value) {
        for(TextureTarget textureTarget : TextureTarget.values()) {
            if(textureTarget.getGLEnum() == value) {
                return textureTarget;
            }
        }
        throw new IllegalArgumentException("Unexpected Value");
    }
}
