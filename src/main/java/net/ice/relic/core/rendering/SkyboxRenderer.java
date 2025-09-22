package net.ice.relic.core.rendering;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.rendering.backend.opengl.GLShader;
import net.ice.relic.core.model.Material;
import net.ice.relic.core.rendering.backend.opengl.model.Mesh;
import net.ice.relic.core.rendering.backend.opengl.model.texture.GLTexture;
import net.ice.relic.core.cache.TextureCache;
import net.ice.relic.core.scene.Scene;
import net.ice.relic.core.scene.SceneObject;
import net.ice.relic.core.scene.Skybox;
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
        loadShader("skybox.vert", GLShader.ShaderType.VERTEX);
        loadShader("skybox.frag", GLShader.ShaderType.FRAGMENT);
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
        TextureCache textureCache = scene.getModelLoader().getTextureCache();
        Material material = skyBox.getMaterial();
        Mesh mesh = skyBox.getMesh();
        GLTexture texture = textureCache.getTexture(material.getTexturePath());
        uniforms.setUniform("textureHandle", texture.getBindlessHandle());
        uniforms.setUniform("diffuse", material.getDiffuseColor().convertToGLVector4f());
        uniforms.setUniform("hasTexture", texture.getBindlessHandle() != 0 && !texture.equals(textureCache.getTexture(TextureCache.DEFAULT_TEXTURE.getPath()))  ? 1 : 0);

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
