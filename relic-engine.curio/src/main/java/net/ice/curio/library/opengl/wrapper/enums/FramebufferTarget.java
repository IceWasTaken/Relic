package net.ice.curio.library.opengl.wrapper.enums;

import static org.lwjgl.opengl.GL30.*;

public enum FramebufferTarget {

    FRAMEBUFFER(GL_FRAMEBUFFER),
    READ_FRAMEBUFFER(GL_READ_FRAMEBUFFER),
    DRAW_FRAMEBUFFER(GL_DRAW_FRAMEBUFFER);

    private final int glEnum;

    FramebufferTarget(int glEnum) {
        this.glEnum = glEnum;
    }

    public int getGlEnum() {
        return glEnum;
    }
}
