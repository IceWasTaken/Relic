package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.context.GraphicsContext;
import net.ice.curio.graphics.object.pipeline.PrimitiveType;
import net.ice.curio.graphics.object.pipeline.depth.CompareFunction;
import net.ice.curio.graphics.object.pipeline.depth.DepthState;
import net.ice.curio.graphics.object.pipeline.raster.CullMode;
import net.ice.curio.graphics.object.pipeline.raster.FrontFace;
import net.ice.curio.graphics.object.pipeline.raster.PolygonMode;
import net.ice.curio.graphics.object.pipeline.raster.RasterizationState;
import net.ice.curio.library.opengl.object.GLViewport;
import net.ice.curio.library.opengl.object.pipeline.GLPipeline;
import net.ice.relic.core.Shadows;
import net.ice.relic.core.rendering.backend.opengl.buffer.GLCommandBuffer;

import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30C.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30C.glBindFramebuffer;
import static org.lwjgl.opengl.GL42C.GL_COMMAND_BARRIER_BIT;
import static org.lwjgl.opengl.GL42C.glMemoryBarrier;

public class GLShadowRenderer {

    private final GraphicsContext context;

    private GLPipeline pipeline;
    private Shadows shadows;


    public GLShadowRenderer(GraphicsContext context) {
        this.context = context;
        this.shadows =  new Shadows();
    }

    public void init() {
        this.pipeline = new GLPipeline(
                context,
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

    public void render(GLRenderer renderer) {
        GLCommandBuffer staticCommandBuffer = renderer.getBufferManager().getStaticCommandBuffer();
        pipeline.bindPipeline();
        shadows.update(renderer.getApplication().getCurrentScene());

        glClearColor(1.f, 1.f, 0f, 0f);
        renderer.getShadowBuffer().bindFramebuffer();
        renderer.getShadowBuffer().clear();

        for (int i = 0; i < Shadows.SHADOW_MAP_COUNT; i++) {
            Matrix4f shadowData = shadows.getShadowData().get(i).getProjViewMatrix();
            pipeline.getUniforms().setUniform("projViewMatrices[" + i + "]", shadowData);
        }

        staticCommandBuffer.bind();
        renderer.getBufferManager().getMeshBuffer().bind();
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

