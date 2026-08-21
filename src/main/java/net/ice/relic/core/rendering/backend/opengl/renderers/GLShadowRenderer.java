package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.object.pipeline.PrimitiveType;
import net.ice.curio.graphics.object.pipeline.depth.CompareFunction;
import net.ice.curio.graphics.object.pipeline.depth.DepthState;
import net.ice.curio.graphics.object.pipeline.raster.CullMode;
import net.ice.curio.graphics.object.pipeline.raster.FrontFace;
import net.ice.curio.graphics.object.pipeline.raster.PolygonMode;
import net.ice.curio.graphics.object.pipeline.raster.RasterizationState;
import net.ice.curio.library.opengl.object.GLViewport;
import net.ice.curio.library.opengl.object.pipeline.GLPipeline;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.Shadows;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.buffer.GLCommandBuffer;

import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL45.*;

public class GLShadowRenderer implements Lifecycle {

    private final GLRenderer glRenderer;

    private GLPipeline pipeline;
    private Shadows shadows;


    public GLShadowRenderer(GLRenderer glRenderer) {
        this.glRenderer = glRenderer;
        this.shadows =  new Shadows();
    }

    @Override
    public void init() {
        this.pipeline = new GLPipeline(
                "shadow",
                null,
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
                        CompareFunction.LESS,
                        false,
                        false
                ),
                new GLViewport(
                        0,
                        0,
                        4096,
                        4096,
                        false
                )
        );


        for (int i = 0; i < 3; i++) {
            pipeline.getUniforms().createUniform("projViewMatrices[" + i + "]");
        }

    }

    @Override
    public void render() {
        GLCommandBuffer staticCommandBuffer = glRenderer.getBufferManager().getStaticCommandBuffer();
        pipeline.bindPipeline();
        shadows.update(glRenderer.getApplication().getCurrentScene());

        glClearColor(1.f, 1.f, 0f, 0f);
        glRenderer.getShadowBuffer().bindFramebuffer();
        glRenderer.getShadowBuffer().clear();

        for (int i = 0; i < Shadows.SHADOW_MAP_COUNT; i++) {
            Matrix4f shadowData = shadows.getShadowData().get(i).getProjViewMatrix();
            pipeline.getUniforms().setUniform("projViewMatrices[" + i + "]", shadowData);
        }

        staticCommandBuffer.bind();
        glRenderer.getBufferManager().getMeshBuffer().bind();
        glMemoryBarrier(GL_COMMAND_BARRIER_BIT);
        staticCommandBuffer.draw(pipeline);

//      .bindVertexBufferObject(animatedVBO, BufferTarget.DRAW_INDIRECT)
//      .bindVertexArrayObject(manager.getAnimationArrayObject())
//      .multiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, animationDrawCount, 0)

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public Shadows getShadows() {
        return shadows;
    }
}

