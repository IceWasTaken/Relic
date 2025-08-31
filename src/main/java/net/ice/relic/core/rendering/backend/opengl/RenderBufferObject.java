package net.ice.relic.core.rendering.backend.opengl;

import static org.lwjgl.opengl.GL30.*;

public class RenderBufferObject {

    private final int bufferHandle;

    public RenderBufferObject() {
        this.bufferHandle = glGenRenderbuffers();
    }

    public void bind() {
        glBindRenderbuffer(GL_RENDERBUFFER, bufferHandle);
    }

    public void bufferStorage(int format, int width, int height) {
        glRenderbufferStorage(GL_RENDERBUFFER, format, width, height);
    }

    public void unbind() {
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
    }

    public void delete() {
        glDeleteRenderbuffers(bufferHandle);
    }

    public int getBufferHandle() {
        return bufferHandle;
    }
}
