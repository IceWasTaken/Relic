package net.ice.curio.library.opengl.wrapper.enums.texture;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_1D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_3D;

public enum TextureType {

    TEXTURE_1D(GL_TEXTURE_1D),
    TEXTURE_2D(GL_TEXTURE_2D),
    TEXTURE_3D(GL_TEXTURE_3D);

    private final int glEnum;

    TextureType(int glEnum) {
        this.glEnum = glEnum;
    }

    public int getGLEnum() {
        return glEnum;
    }
}
