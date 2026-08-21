package net.ice.curio.library.opengl.wrapper.enums.texture.parameter;

import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;

//https://learnopengl.com/Getting-started/Textures
public enum WrapParameter {

    REPEAT(GL_REPEAT),
    MIRRORED_REPEAT(GL_MIRRORED_REPEAT),
    CLAMP_TO_EDGE(GL_CLAMP_TO_EDGE),
    CLAMP_TO_BORDER(GL_CLAMP_TO_BORDER);

    private final int glEnum;

    WrapParameter(int glEnum) {
        this.glEnum = glEnum;
    }

    public int getGLEnum() {
        return glEnum;
    }
}
