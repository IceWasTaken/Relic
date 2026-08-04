package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;
import net.ice.curio.graphics.object.Viewport;
import net.ice.curio.library.opengl.object.GLViewport;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.cache.MaterialCache;
import net.ice.relic.core.ecs.component.components.rendering.model.StaticModelComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.Material;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.Uniforms;
import net.ice.relic.core.rendering.backend.opengl.buffer.StaticCommandBuffer;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_MAP_WRITE_BIT;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL40.GL_DRAW_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL43.glMultiDrawElementsIndirect;

public class GLSceneRenderer implements Lifecycle {

    private GLShaderProgram shaderProgram;

    private final GLRenderer glRenderer;

    private Uniforms uniforms;
    private Viewport viewport;

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
        StaticCommandBuffer staticCommandBuffer = glRenderer.getBufferManager().getStaticCommandBuffer();

        shaderProgram.bind();
        viewport.bind();

        glRenderer.getGeometryBuffer().bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDisable(GL_BLEND);

        uniforms.setUniform("projectionMatrix", glRenderer.getApplication().getCurrentScene().getMatrix().getProjMatrix());
        uniforms.setUniform("viewMatrix", glRenderer.getApplication().getCurrentScene().getCamera().getViewMatrix());

        staticCommandBuffer.bind();
        glRenderer.getBufferManager().getVertexIndexArrayBuffer().bind();
        glMultiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, staticCommandBuffer.getStaticDrawCount(), 0);
        glRenderer.getGeometryBuffer().unbind();
        shaderProgram.unbind();
    }

    public void resize(int width, int height) {
        this.viewport.resize(width, height);
    }
}
