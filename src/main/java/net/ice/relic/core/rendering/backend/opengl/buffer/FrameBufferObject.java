package net.ice.relic.core.rendering.backend.opengl.buffer;

import static org.lwjgl.opengl.GL30.*;

public class FrameBufferObject {

    private final int fboID;

    public FrameBufferObject() {
        this.fboID = glGenFramebuffers();
    }

    public void bind(int target) {
        glBindFramebuffer(target, fboID);
    }

    public void bindFrameBuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
    }

    public void unbindFrameBuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void framebufferTexture2D(int attachment, int textarget, int texture, int level) {
        glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, textarget, texture, level);
    }

    public void setRenderBuffer(int target, int attachment, RenderBufferObject renderbuffer) {
        glFramebufferRenderbuffer(target, attachment, GL_RENDERBUFFER, renderbuffer.getBufferHandle());
    }

    public void assertComplete() {
        int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (status != GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalStateException("FBO incomplete. Status: " + status);
        }
    }

    public void delete() {
        glDeleteFramebuffers(fboID);
    }

    public int getFboID() {
        return fboID;
    }
}
