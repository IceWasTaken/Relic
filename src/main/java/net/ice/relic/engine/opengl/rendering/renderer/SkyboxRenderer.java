package net.ice.relic.engine.opengl.rendering.renderer;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.engine.opengl.Shader;
import net.ice.relic.common.model.Material;
import net.ice.relic.engine.opengl.model.Mesh;
import net.ice.relic.engine.opengl.model.texture.Texture;
import net.ice.relic.common.cache.TextureCache;
import net.ice.relic.common.scene.Scene;
import net.ice.relic.common.scene.SceneObject;
import net.ice.relic.common.scene.Skybox;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;


public class SkyboxRenderer extends AbstractRenderer {

    private Matrix4f viewMatrix = new Matrix4f();

    public SkyboxRenderer(RelicApplication application) {
        super(application);
    }

    @Override
    protected void initShaders() {
        loadShader("skybox.vert", Shader.ShaderType.VERTEX);
        loadShader("skybox.frag", Shader.ShaderType.FRAGMENT);
    }

    @Override
    protected void initUniforms() {
        uniforms.createUniform("projectionMatrix");
        uniforms.createUniform("viewMatrix");
        uniforms.createUniform("modelMatrix");
        uniforms.createUniform("diffuse");
        uniforms.createUniform("textureHandle");
        uniforms.createUniform("hasTexture");
    }

    @Override
    protected void render() {
        Scene scene = application.getCurrentScene();
        Skybox skyBox = scene.getSkybox();
        if (skyBox == null) {
            return;
        }
        shaderProgram.bind();

        uniforms.setUniform("projectionMatrix", scene.getMatrix().getProjMatrix());
        viewMatrix.set(scene.getCamera().getViewMatrix());
        viewMatrix.m30(0);
        viewMatrix.m31(0);
        viewMatrix.m32(0);
        uniforms.setUniform("viewMatrix", viewMatrix);

        SceneObject skyBoxEntity = skyBox.getSceneObject();
        TextureCache textureCache = scene.getTextureCache();
        Material material = skyBox.getMaterial();
        Mesh mesh = skyBox.getMesh();
        Texture texture = textureCache.getTexture(material.getTexturePath());
        uniforms.setUniform("textureHandle", texture.getBindlessHandle());
        uniforms.setUniform("diffuse", material.getDiffuseColor().convertToGLVector4f());

        uniforms.setUniform("hasTexture", texture.getTexturePath().equals(TextureCache.DEFAULT_TEXTURE) ? 0 : 1);

        mesh.getVertexArrayObject().bind();

        uniforms.setUniform("modelMatrix", skyBoxEntity.getTransform().getTransformMatrix());
        glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);

        shaderProgram.unbind();



        assertNoError();
    }

    @Override
    protected void setupData() {

    }
}
