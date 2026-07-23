package net.ice.relic.core.rendering.backend.opengl.depricated;

import net.ice.relic.core.rendering.AbstractRenderer;
import net.ice.relic.core.rendering.backend.opengl.Uniforms;
import net.ice.relic.core.rendering.shader.IShader;
import net.ice.relic.core.rendering.shader.ShaderType;

import java.util.ArrayList;
import java.util.List;


@Deprecated
public abstract class AbstractGLRenderer extends AbstractRenderer {

    protected GLManager manager;
    protected GLShaderProgram shaderProgram;

    protected Uniforms uniforms;

    protected final List<IShader> shaders;

    public AbstractGLRenderer(GLManager manager) {
        this.manager = manager;
        this.shaders = new ArrayList<>();
    }

    public void init() {
        initShaders();
        //this.shaderProgram = new GLShaderProgram().attach(shaders);
        this.uniforms = new Uniforms(shaderProgram);
        initUniforms();
    }

    public void cleanup() {
        shaderProgram.cleanup();
    }

    protected void loadShader(String path, ShaderType type) {
        //shaders.add(new GLShader(type).load(path, type));
    }



    protected abstract void initShaders();
    protected abstract void initUniforms();
    protected abstract void render();
    protected abstract void setupData();
}
