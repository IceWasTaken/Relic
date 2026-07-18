package net.ice.relic.core.rendering.backend.opengl.depricated.rendering.renderer;

import net.ice.curio.library.opengl.object.resource.GLTexture;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;
import net.ice.relic.core.rendering.backend.opengl.depricated.buffer.UniformBufferObject;
import net.ice.relic.core.rendering.backend.opengl.depricated.rendering.QuadMesh;
import net.ice.curio.library.opengl.object.framebuffer.FramebufferObject;

import static net.ice.curio.library.opengl.wrapper.enums.FramebufferTarget.FRAMEBUFFER;
import static org.lwjgl.opengl.GL11.*;

@Deprecated
public class PostRenderer implements Lifecycle {

    private final RelicApplication application;

    private FramebufferObject postBuffer;
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
        //this.postBuffer = new FramebufferObject();
        this.width = application.getWindow().getWidth();
        this.height = application.getWindow().getHeight();

        this.quadMesh = new QuadMesh();

        String postTexturePath = "__postfx__";
        //postTexture = new GLTexture(width, height);

        postBuffer.bind(FRAMEBUFFER);
        //postBuffer.framebufferTexture2D(FRAMEBUFFER, COLOR_ATTACHMENT_N, 0, TEXTURE_2D, postTexture);
    }

    @Override
    public void render() {
        if (!enabled || shaderProgram == null) return;

        postBuffer.unbind(FRAMEBUFFER);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);

        shaderProgram.bind();

        //uniforms.setUniform("screenHandle", postTexture.getBindlessHandle());
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

        //postTexture = new GLTexture(width, height);

        postBuffer.bind(FRAMEBUFFER);
        //postBuffer.framebufferTexture2D(FRAMEBUFFER, COLOR_ATTACHMENT_N, 0, TEXTURE_2D, postTexture);
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

    public FramebufferObject getPostBuffer() {
        return postBuffer;
    }
}
