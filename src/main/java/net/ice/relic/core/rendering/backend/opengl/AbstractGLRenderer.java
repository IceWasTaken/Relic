package net.ice.relic.core.rendering.backend.opengl;

import net.ice.relic.core.config.configs.RendererConfig;
import net.ice.relic.core.rendering.AbstractRenderer;
import net.ice.relic.core.rendering.backend.opengl.buffer.UniformBufferObject;
import net.ice.relic.core.rendering.shader.ShaderType;

import java.util.ArrayList;
import java.util.List;

import static net.ice.relic.core.rendering.backend.opengl.GLUtil.assertNoError;


public abstract class AbstractGLRenderer extends AbstractRenderer {

    protected GLManager manager;
    protected ShaderProgram shaderProgram;

    protected UniformBufferObject uniforms;

    protected RendererConfig config;

    private final List<GLShader> shaders;

    public AbstractGLRenderer(GLManager manager) {
        this.manager = manager;
        this.shaders = new ArrayList<>();
        this.config = manager.getRendererConfig();
    }

    public void init() {
        initShaders();
        this.shaderProgram = new ShaderProgram(shaders);
        this.uniforms = new UniformBufferObject(shaderProgram);
        assertNoError();
        initUniforms();
    }

    public void cleanup() {
        shaderProgram.cleanup();
    }

    protected void loadShader(String path, ShaderType type) {
        shaders.add(GLShader.loadShader(path, type, false));
    }



    protected abstract void initShaders();
    protected abstract void initUniforms();
    protected abstract void render();
    protected abstract void setupData();
}
