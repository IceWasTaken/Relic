package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.context.GraphicsContext;
import net.ice.curio.graphics.object.pipeline.framebuffer.GLFramebuffer;
import net.ice.curio.library.opengl.object.pipeline.GLPipeline;
import net.ice.relic.core.rendering.backend.opengl.buffer.GLCommandBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL42.GL_COMMAND_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.glMemoryBarrier;

public class GLSceneRenderer {

    private GLPipeline renderPipeline;
    private final GraphicsContext graphicsContext;

    public GLSceneRenderer(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;

    }

    public void init() {
        this.renderPipeline = new GLPipeline(
                graphicsContext,
                "scene",
                new GLFramebuffer(
                        graphicsContext,
                        graphicsContext.getCurio().getWindow().getFramebufferSize(),
                        GL_TEXTURE_2D,
                        4,
                        GL_RGBA16F
                ),
                true
        );

        renderPipeline.getUniforms().createUniform("projectionMatrix");
        renderPipeline.getUniforms().createUniform("viewMatrix");
    }

    public void render(GLRenderer renderer) {
        GLCommandBuffer staticCommandBuffer = renderer.getBufferManager().getStaticCommandBuffer();

        renderPipeline.bindPipeline();

        renderPipeline.getUniforms().setUniform("projectionMatrix", renderer.getApplication().getCurrentScene().getMatrix().getProjMatrix());
        renderPipeline.getUniforms().setUniform("viewMatrix", renderer.getApplication().getCurrentScene().getCamera().getViewMatrix());

        staticCommandBuffer.bind();
        renderer.getBufferManager().getMeshBuffer().bind();
        glMemoryBarrier(GL_COMMAND_BARRIER_BIT);
        staticCommandBuffer.draw(renderPipeline);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void resize(int width, int height) {
        renderPipeline.resize(width, height);
    }

    public GLPipeline getPipeline() {
        return renderPipeline;
    }
}
