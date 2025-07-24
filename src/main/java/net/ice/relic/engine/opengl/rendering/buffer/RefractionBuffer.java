package net.ice.relic.engine.opengl.rendering.buffer;

import net.ice.relic.Lifecycle;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.engine.opengl.FrameBufferObject;
import net.ice.relic.engine.opengl.RenderBufferObject;
import net.ice.relic.engine.opengl.model.texture.Texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL30.*;

public class RefractionBuffer implements Lifecycle {

    private final RelicApplication application;

    private FrameBufferObject refractionFBO;
    private RenderBufferObject depthRBO;
    private Texture refractionTexture;

    private int width;
    private int height;

    public RefractionBuffer(RelicApplication application) {
        this.application = application;
    }

    @Override
    public void init() {
        this.width = application.getWindow().getWidth();
        this.height = application.getWindow().getHeight();

        refractionFBO = new FrameBufferObject();
        depthRBO = new RenderBufferObject();
        refractionTexture = new Texture(width, height);

        refractionFBO.bindFrameBuffer();
        refractionFBO.framebufferTexture2D(GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, refractionTexture.getTextureID(), 0);

        depthRBO.bind();
        depthRBO.bufferStorage(GL_DEPTH_COMPONENT24, width, height);
        refractionFBO.setRenderBuffer(GL_DRAW_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthRBO);

        refractionFBO.unbindFrameBuffer();
    }

    public void bind() {
        refractionFBO.bindFrameBuffer();
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void unbind() {
        refractionFBO.unbindFrameBuffer();
    }

    public void resize(int newWidth, int newHeight) {
        this.width = newWidth;
        this.height = newHeight;

        refractionTexture = new Texture(width, height);
        depthRBO.bind();
        depthRBO.bufferStorage(GL_DEPTH_COMPONENT24, width, height);

        refractionFBO.bindFrameBuffer();
        refractionFBO.framebufferTexture2D(GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, refractionTexture.getTextureID(), 0);
        refractionFBO.setRenderBuffer(GL_DRAW_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthRBO);
        refractionFBO.unbindFrameBuffer();
    }

    public Texture getTexture() {
        return refractionTexture;
    }

    public FrameBufferObject getRefractionFBO() {
        return refractionFBO;
    }

    public RenderBufferObject getDepthRBO() {
        return depthRBO;
    }
}
