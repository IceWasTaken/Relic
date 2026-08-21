package net.ice.curio.library.opengl.wrapper.enums;

import static org.lwjgl.opengl.GL30.*;

public enum Attachment {

    COLOR_ATTACHMENT_N(0),
    DEPTH_ATTACHMENT(GL_DEPTH_ATTACHMENT),
    STENCIL_ATTACHMENT(GL_STENCIL_ATTACHMENT),
    DEPTH_STENCIL_ATTACHMENT(GL_DEPTH_STENCIL_ATTACHMENT);

    private final int glEnum;

    Attachment(int glEnum) {
        this.glEnum = glEnum;
    }

    public int getGLEnum(int index) {
        if(glEnum == 0) {
            return GL_COLOR_ATTACHMENT0 + index;
        }
        return glEnum;
    }

    public int getGLEnum() {
        return glEnum;
    }

    public static Attachment fromValue(int value) {
        for(Attachment attachment : Attachment.values()) {
            if(attachment.getGLEnum() == value) {
                return attachment;
            }
        }
        throw new IllegalArgumentException("Unexpected Value");
    }

}
