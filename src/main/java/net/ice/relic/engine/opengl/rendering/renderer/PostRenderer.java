package net.ice.relic.engine.opengl.rendering.renderer;

import net.ice.relic.Lifecycle;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.engine.opengl.FrameBufferObject;
import net.ice.relic.engine.opengl.rendering.QuadMesh;
import net.ice.relic.engine.opengl.ShaderProgram;
import net.ice.relic.engine.opengl.UniformBufferObject;
import net.ice.relic.engine.opengl.model.texture.Texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;

public class PostRenderer implements Lifecycle {

    private final RelicApplication application;

    private FrameBufferObject postBuffer;
    private ShaderProgram shaderProgram;
    private Texture postTexture;
    private QuadMesh quadMesh;
    private UniformBufferObject uniforms;

    private int width;
    private int height;

    private boolean enabled = false;

    public PostRenderer(RelicApplication application) {
        this.application = application;
    }

    private void initUniforms() {
        uniforms.createUniformUnsafe("screenHandle");
        uniforms.createUniformUnsafe("resolution");
        uniforms.createUniformUnsafe("projectionMatrix");
    }

    @Override
    public void init() {
        this.postBuffer = new FrameBufferObject();
        this.width = application.getWindow().getWidth();
        this.height = application.getWindow().getHeight();

        this.quadMesh = new QuadMesh();

        String postTexturePath = "__postfx__";
        postTexture = new Texture(width, height);

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
        uniforms.setUniform("resolution", application.getWindow().getWindowSize());
        uniforms.setUniform("projectionMatrix", application.getCurrentScene().getMatrix().getProjMatrix());
        quadMesh.getMeshVAO().bind();
        glDrawElements(GL_TRIANGLES, quadMesh.getVertexCount(), GL_UNSIGNED_INT, 0);

        shaderProgram.unbind();

        glEnable(GL_DEPTH_TEST);
    }

    public void resize() {
        this.width = application.getWindow().getWidth();
        this.height = application.getWindow().getHeight();

        postTexture = new Texture(width, height);

        postBuffer.bindFrameBuffer();
        postBuffer.framebufferTexture2D(GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, postTexture.getTextureID(), 0);
    }

    public void loadShader(ShaderProgram shaderProgram) {
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
