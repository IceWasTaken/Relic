package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.object.Viewport;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.Shadows;
import net.ice.relic.core.ecs.component.components.rendering.model.StaticModelComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.Uniforms;
import net.ice.relic.core.rendering.backend.opengl.buffer.StaticCommandBuffer;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;
import net.ice.relic.core.rendering.backend.opengl.framebuffers.ShadowBuffer;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_LOWER_LEFT;
import static org.lwjgl.opengl.GL43.glMultiDrawElementsIndirect;
import static org.lwjgl.opengl.GL45.*;

public class GLShadowRenderer implements Lifecycle {

    private GLShaderProgram shaderProgram;
    private final GLRenderer glRenderer;

    private final Viewport viewport;

    private Shadows shadows;

    private Uniforms uniforms;


    public GLShadowRenderer(GLRenderer glRenderer) {
        this.glRenderer = glRenderer;
        this.viewport = glRenderer.getApplication().getCurio().getGraphicsContext().createViewport(4096, 4096);
        this.shadows =  new Shadows();
    }

    @Override
    public void init() {
        this.shaderProgram = new GLShaderProgram("shadow");

        this.uniforms = new Uniforms(shaderProgram);

        for (int i = 0; i < 3; i++) {
            uniforms.createUniform("projViewMatrices[" + i + "]");
        }

    }

    @Override
    public void render() {
        StaticCommandBuffer staticCommandBuffer = glRenderer.getStaticCommandBuffer();

        shaderProgram.bind();
        shadows.update(glRenderer.getApplication().getCurrentScene());
        glRenderer.getShadowBuffer().bindFramebuffer();
        glClipControl(GL_LOWER_LEFT, GL_NEGATIVE_ONE_TO_ONE);
        glDepthRange(-1, 1);
        glDepthFunc(GL_LESS);
        glClearDepthf(1.0f);
        glViewport(0, 0, 4096, 4096);

        glClearColor(1.f, 1.f, 0f, 0f);
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

        for (int i = 0; i < Shadows.SHADOW_MAP_COUNT; i++) {
            Matrix4f shadowData = shadows.getShadowData().get(i).getProjViewMatrix();
            uniforms.setUniform("projViewMatrices[" + i + "]", shadowData);
        }

        staticCommandBuffer.bind();
        glRenderer.getStaticArrayObject().bind();
        glMultiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, staticCommandBuffer.getStaticDrawCount(), 0);

//      .bindVertexBufferObject(animatedVBO, BufferTarget.DRAW_INDIRECT)
//      .bindVertexArrayObject(manager.getAnimationArrayObject())
//      .multiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, animationDrawCount, 0)

        glBindVertexArray(0);
        ShadowBuffer.unbindFramebuffer();

        shaderProgram.unbind();
    }

    public Shadows getShadows() {
        return shadows;
    }
}

