package net.ice.relic.core.rendering;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.config.configs.RendererConfig;
import net.ice.relic.core.rendering.backend.opengl.GLShader;
import net.ice.relic.core.rendering.backend.opengl.rendering.buffer.GeometryBuffer;
import net.ice.relic.core.rendering.backend.opengl.ShaderProgram;
import net.ice.relic.core.rendering.backend.opengl.UniformBufferObject;
import net.ice.relic.core.rendering.backend.opengl.rendering.buffer.ReflectionBuffer;
import net.ice.relic.core.rendering.backend.opengl.rendering.buffer.RefractionBuffer;
import net.ice.relic.core.rendering.backend.opengl.rendering.buffer.RenderingBuffers;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.glGetError;

public abstract class AbstractRenderer {

    protected RelicApplication application;
    protected ShaderProgram shaderProgram;

    protected UniformBufferObject uniforms;

    protected RendererConfig config;

    protected RenderingBuffers renderingBuffer;
    protected GeometryBuffer geometryBuffer;
    protected RefractionBuffer refractionBuffer;
    protected ReflectionBuffer reflectionBuffer;

    private List<GLShader> shaders;

    public AbstractRenderer(RelicApplication application) {
        this.application = application;
        this.shaders = new ArrayList<>();
        this.config = application.getConfig().getRendererConfig();
    }

    public void init(RenderingBuffers renderingBuffer, GeometryBuffer buffer, RefractionBuffer refractionBuffer, ReflectionBuffer reflectionBuffer) {
        initShaders();
        this.shaderProgram = new ShaderProgram(shaders);
        this.uniforms = new UniformBufferObject(shaderProgram);
        this.renderingBuffer = renderingBuffer;
        this.geometryBuffer = buffer;
        this.refractionBuffer = refractionBuffer;
        this.reflectionBuffer = reflectionBuffer;
        assertNoError();
        initUniforms();
    }

    public void cleanup() {
        shaderProgram.cleanup();
    }

    protected void loadShader(String path, GLShader.ShaderType type) {
        shaders.add(GLShader.loadShader(path, type, false));

    }

    protected void assertNoError() {
        int error = glGetError();
        if(error == 0) {
            return;
        }
        throw new RuntimeException("OpenGL Error: " + error);
    }

    protected abstract void initShaders();
    protected abstract void initUniforms();
    protected abstract void render();
    protected abstract void setupData();
}
