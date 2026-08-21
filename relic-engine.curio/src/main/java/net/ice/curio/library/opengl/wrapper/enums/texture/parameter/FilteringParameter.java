package net.ice.curio.library.opengl.wrapper.enums.texture.parameter;

import static org.lwjgl.opengl.GL11.*;

//https://learnopengl.com/Getting-started/Textures
public enum FilteringParameter {

    NEAREST(GL_NEAREST),
    LINEAR(GL_LINEAR),
    NEAREST_MIPMAP_NEAREST(GL_NEAREST_MIPMAP_NEAREST),
    LINEAR_MIPMAP_NEAREST(GL_LINEAR_MIPMAP_NEAREST),
    NEAREST_MIPMAP_LINEAR(GL_NEAREST_MIPMAP_LINEAR),
    LINEAR_MIPMAP_LINEAR(GL_LINEAR_MIPMAP_LINEAR);

    private final int glEnum;

    FilteringParameter(int glEnum) {
        this.glEnum = glEnum;
    }

    public int getGLEnum() {
        return glEnum;
    }
}
