package net.ice.relic.engine.opengl.rendering.renderer;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.config.configs.RendererConfig;
import net.ice.relic.engine.opengl.rendering.buffer.GeometryBuffer;
import net.ice.relic.engine.opengl.Shader;
import net.ice.relic.engine.opengl.ShaderProgram;
import net.ice.relic.engine.opengl.UniformBufferObject;
import net.ice.relic.engine.opengl.rendering.buffer.ReflectionBuffer;
import net.ice.relic.engine.opengl.rendering.buffer.RefractionBuffer;
import net.ice.relic.engine.opengl.rendering.buffer.RenderingBuffers;

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

    private List<Shader> shaders;

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
        initUniforms();
    }

    public void cleanup() {
        shaderProgram.cleanup();
    }

    protected void loadShader(String path, Shader.ShaderType type) {
        shaders.add(Shader.loadShader(path, type, false));
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
