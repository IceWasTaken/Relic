package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.object.Viewport;
import net.ice.curio.library.opengl.object.GLViewport;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.Uniforms;
import net.ice.relic.core.rendering.backend.opengl.buffer.GLCommandBuffer;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL42.GL_COMMAND_BARRIER_BIT;
import static org.lwjgl.opengl.GL42.glMemoryBarrier;
import static org.lwjgl.opengl.GL43.glMultiDrawElementsIndirect;

public class GLSceneRenderer implements Lifecycle {

    private GLShaderProgram shaderProgram;
    private Uniforms uniforms;
    private Viewport viewport;

    private final GLRenderer glRenderer;

    public GLSceneRenderer(GLRenderer glRenderer) {
        this.glRenderer = glRenderer;
        this.viewport = new GLViewport(
                glRenderer.getApplication().getWindow().getWidth(),
                glRenderer.getApplication().getWindow().getHeight()
        );
    }

    @Override
    public void init() {
        this.shaderProgram = new GLShaderProgram("scene");

        this.uniforms = new Uniforms(shaderProgram);
        uniforms.createUniform("projectionMatrix");
        uniforms.createUniform("viewMatrix");
    }

    @Override
    public void render() {
        GLCommandBuffer staticCommandBuffer = glRenderer.getBufferManager().getStaticCommandBuffer();

        shaderProgram.bind();
        viewport.bind();

        glRenderer.getGeometryBuffer().bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDisable(GL_BLEND);

        uniforms.setUniform("projectionMatrix", glRenderer.getApplication().getCurrentScene().getMatrix().getProjMatrix());
        uniforms.setUniform("viewMatrix", glRenderer.getApplication().getCurrentScene().getCamera().getViewMatrix());

        staticCommandBuffer.bind();
        glRenderer.getBufferManager().getMeshBuffer().bind();
        glMemoryBarrier(GL_COMMAND_BARRIER_BIT);
        glMultiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, staticCommandBuffer.getDrawCount(), 0);
        glRenderer.getGeometryBuffer().unbind();
        shaderProgram.unbind();
    }

    public void resize(int width, int height) {
        this.viewport.resize(width, height);
    }
}
