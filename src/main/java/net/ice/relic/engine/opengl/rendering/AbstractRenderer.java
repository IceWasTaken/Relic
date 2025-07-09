package net.ice.relic.engine.opengl.rendering;

import net.ice.relic.engine.RelicApplication;
import net.ice.relic.engine.config.configs.RendererConfig;
import net.ice.relic.engine.opengl.GeometryBuffer;
import net.ice.relic.engine.opengl.Shader;
import net.ice.relic.engine.opengl.ShaderProgram;
import net.ice.relic.engine.opengl.Uniforms;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.glGetError;

public abstract class AbstractRenderer {

    protected RelicApplication application;
    protected ShaderProgram shaderProgram;
    protected Uniforms uniforms;
    protected RendererConfig config;
    protected RenderingBuffer renderingBuffer;
    protected GeometryBuffer geometryBuffer;

    private List<Shader> shaders;

    public AbstractRenderer(RelicApplication application) {
        this.application = application;
        this.shaders = new ArrayList<>();
        this.config = application.getConfig().getRendererConfig();
    }

    public void init(RenderingBuffer renderingBuffer, GeometryBuffer buffer) {
        initShaders();
        this.shaderProgram = new ShaderProgram(shaders);
        this.uniforms = new Uniforms(shaderProgram);
        this.renderingBuffer = renderingBuffer;
        this.geometryBuffer = buffer;
        initUniforms();


    }

    public void cleanup() {
        shaderProgram.cleanup();
    }

    protected void loadShader(String path, Shader.ShaderType type) {
        shaders.add(Shader.loadShader(path, type));
    }

    protected void ensureNoErrorBeforeContinue() {
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
