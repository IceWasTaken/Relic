package net.ice.relic.core.rendering.backend.opengl.rendering.buffer;

import net.ice.relic.Lifecycle;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.rendering.backend.opengl.buffer.FrameBufferObject;
import net.ice.relic.core.rendering.backend.opengl.buffer.RenderBufferObject;
import net.ice.relic.core.rendering.backend.opengl.model.texture.GLTexture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL30.*;

public class ReflectionBuffer implements Lifecycle {

    private final RelicApplication application;

    private FrameBufferObject reflectionFBO;
    private RenderBufferObject depthRBO;
    private GLTexture reflectionTexture;

    private int width;
    private int height;

    public ReflectionBuffer(RelicApplication application) {
        this.application = application;
    }

    @Override
    public void init() {
        this.width = application.getWindow().getWidth();
        this.height = application.getWindow().getHeight();

        reflectionFBO = new FrameBufferObject();
        depthRBO = new RenderBufferObject();
        reflectionTexture = new GLTexture(width, height);

        reflectionFBO.bindFrameBuffer();

        reflectionFBO.framebufferTexture2D(GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, reflectionTexture.getTextureID(), 0);

        depthRBO.bind();
        depthRBO.bufferStorage(GL_DEPTH_COMPONENT24, width, height);
        reflectionFBO.setRenderBuffer(GL_DRAW_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthRBO);

        reflectionFBO.unbindFrameBuffer();
    }

    public void bind() {
        reflectionFBO.bindFrameBuffer();
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void unbind() {
        reflectionFBO.unbindFrameBuffer();
    }

    public void resize(int newWidth, int newHeight) {
        this.width = newWidth;
        this.height = newHeight;

        reflectionTexture = new GLTexture(width, height);
        depthRBO.bind();
        depthRBO.bufferStorage(GL_DEPTH_COMPONENT24, width, height);

        reflectionFBO.bindFrameBuffer();
        reflectionFBO.framebufferTexture2D(GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, reflectionTexture.getTextureID(), 0);
        reflectionFBO.setRenderBuffer(GL_DRAW_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthRBO);
        reflectionFBO.unbindFrameBuffer();
    }

    public GLTexture getTexture() {
        return reflectionTexture;
    }

    public RenderBufferObject getDepthRBO() {
        return depthRBO;
    }

    public GLTexture getReflectionTexture() {
        return reflectionTexture;
    }

    public FrameBufferObject getReflectionFBO() {
        return reflectionFBO;
    }
}
