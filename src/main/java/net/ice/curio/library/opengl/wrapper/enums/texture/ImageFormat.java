package net.ice.curio.library.opengl.wrapper.enums.texture;

import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL30.GL_RG8;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;

public enum ImageFormat {

    RG8F(GL_RG8),
    RGBA8(GL_RGBA8),
    RGBA16F(GL_RGBA16F),

    RGBA(GL_RGBA);

    private final int glEnum;

    ImageFormat(int glEnum) {
        this.glEnum = glEnum;
    }

    public int getGLEnum() {
        return glEnum;
    }
}
