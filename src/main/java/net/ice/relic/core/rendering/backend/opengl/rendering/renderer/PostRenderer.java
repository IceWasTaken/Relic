package net.ice.relic.core.rendering.backend.opengl.rendering.renderer;

import net.ice.relic.Lifecycle;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.rendering.backend.opengl.GLShaderProgram;
import net.ice.relic.core.rendering.backend.opengl.buffer.FrameBufferObject;
import net.ice.relic.core.rendering.backend.opengl.buffer.UniformBufferObject;
import net.ice.relic.core.rendering.backend.opengl.model.texture.GLTexture;
import net.ice.relic.core.rendering.backend.opengl.rendering.QuadMesh;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;

public class PostRenderer implements Lifecycle {

    private final RelicApplication application;

    private FrameBufferObject postBuffer;
    private GLShaderProgram shaderProgram;
    private GLTexture postTexture;
    private QuadMesh quadMesh;
    private UniformBufferObject uniforms;

    private int width;
    private int height;

    private boolean enabled = false;

    public PostRenderer(RelicApplication application) {
        this.application = application;
    }

    private void initUniforms() {
        uniforms.createUniform("screenHandle");
        uniforms.createUniform("resolution");
        uniforms.createUniform("projectionMatrix");
    }

    @Override
    public void init() {
        this.postBuffer = new FrameBufferObject();
        this.width = application.getWindow().getWidth();
        this.height = application.getWindow().getHeight();

        this.quadMesh = new QuadMesh();

        String postTexturePath = "__postfx__";
        postTexture = new GLTexture(width, height);

        postBuffer.bindFrameBuffer();
        postBuffer.framebufferTexture2D(GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, postTexture.getTextureID(), 0);
    }

    @Override
    public void render() {
        if (!enabled || shaderProgram == null) return;

        postBuffer.unbindFrameBuffer();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);

        shaderProgram.bind();

        uniforms.setUniform("screenHandle", postTexture.getBindlessHandle());
        uniforms.setUniform("resolution", application.getWindow().getSize());
        uniforms.setUniform("projectionMatrix", application.getCurrentScene().getMatrix().getProjMatrix());
        quadMesh.getMeshVAO().bind();
        glDrawElements(GL_TRIANGLES, quadMesh.getVertexCount(), GL_UNSIGNED_INT, 0);

        shaderProgram.unbind();

        glEnable(GL_DEPTH_TEST);
    }

    public void resize() {
        this.width = application.getWindow().getWidth();
        this.height = application.getWindow().getHeight();

        postTexture = new GLTexture(width, height);

        postBuffer.bindFrameBuffer();
        postBuffer.framebufferTexture2D(GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, postTexture.getTextureID(), 0);
    }

    public void loadShader(GLShaderProgram shaderProgram) {
        enabled = true;
        this.shaderProgram = shaderProgram;
        this.uniforms = new UniformBufferObject(shaderProgram);
        initUniforms();
    }

    public void unloadShader() {
        this.shaderProgram = null;
        this.uniforms = null;
        enabled = false;
    }

    public FrameBufferObject getPostBuffer() {
        return postBuffer;
    }
}
