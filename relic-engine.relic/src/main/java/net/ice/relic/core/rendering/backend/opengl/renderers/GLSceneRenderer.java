package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.object.pipeline.PrimitiveType;
import net.ice.curio.graphics.object.pipeline.depth.DepthState;
import net.ice.curio.graphics.object.pipeline.framebuffer.GLFramebuffer;
import net.ice.curio.graphics.object.pipeline.raster.CullMode;
import net.ice.curio.graphics.object.pipeline.raster.FrontFace;
import net.ice.curio.graphics.object.pipeline.raster.PolygonMode;
import net.ice.curio.graphics.object.pipeline.raster.RasterizationState;
import net.ice.curio.library.opengl.object.GLViewport;
import net.ice.curio.library.opengl.object.pipeline.GLPipeline;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.buffer.GLCommandBuffer;

import static net.ice.curio.graphics.object.pipeline.depth.CompareFunction.GREATER;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL42.GL_COMMAND_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.glMemoryBarrier;
import static org.lwjgl.opengl.GL43.glMultiDrawElementsIndirect;

public class GLSceneRenderer implements Lifecycle {

    private GLPipeline renderPipeline;
    private final GLRenderer glRenderer;

    public GLSceneRenderer(GLRenderer glRenderer) {
        this.glRenderer = glRenderer;

    }

    @Override
    public void init() {
        int width = glRenderer.getApplication().getWindow().getWidth();
        int height = glRenderer.getApplication().getWindow().getHeight();

        this.renderPipeline = new GLPipeline(
                "scene",
                new GLFramebuffer(
                        GL_TEXTURE_2D,
                        width,
                        height,
                        4,
                        GL_RGBA16F
                ),
                PrimitiveType.TRIANGLE,
                new RasterizationState(
                        PolygonMode.FILL,
                        FrontFace.COUNTER_CLOCKWISE,
                        CullMode.BACK,
                        true,
                        1.0f
                ),
                new DepthState(
                        true,
                        true,
                        GREATER,
                        false,
                        false
                ),
                new GLViewport(
                        0,
                        0,
                        width,
                        height,
                        true
                )
        );

        renderPipeline.getUniforms().createUniform("projectionMatrix");
        renderPipeline.getUniforms().createUniform("viewMatrix");
    }

    @Override
    public void render() {
        GLCommandBuffer staticCommandBuffer = glRenderer.getBufferManager().getStaticCommandBuffer();

        renderPipeline.bindPipeline();

        renderPipeline.getUniforms().setUniform("projectionMatrix", glRenderer.getApplication().getCurrentScene().getMatrix().getProjMatrix());
        renderPipeline.getUniforms().setUniform("viewMatrix", glRenderer.getApplication().getCurrentScene().getCamera().getViewMatrix());

        staticCommandBuffer.bind();
        glRenderer.getBufferManager().getMeshBuffer().bind();
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
