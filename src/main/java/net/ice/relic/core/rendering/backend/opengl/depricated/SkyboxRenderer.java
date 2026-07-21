package net.ice.relic.core.rendering.backend.opengl.depricated;

import net.ice.relic.core.cache.TextureCache;
import net.ice.relic.core.model.Material;
import net.ice.relic.core.rendering.backend.opengl.depricated.model.Mesh;
import net.ice.relic.core.rendering.shader.ShaderType;
import net.ice.relic.core.scene.Scene;
import net.ice.relic.core.scene.SceneObject;
import net.ice.relic.core.scene.Skybox;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

@Deprecated
public class SkyboxRenderer extends AbstractGLRenderer {

    private Matrix4f viewMatrix = new Matrix4f();

    public SkyboxRenderer(GLManager manager) {
        super(manager);
    }

    @Override
    protected void initShaders() {
        loadShader("skybox.vert", ShaderType.VERTEX);
        loadShader("skybox.frag", ShaderType.FRAGMENT);
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
    public void render() {
        Scene scene = manager.getApplication().getCurrentScene();
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
//        BindlessTexture texture = textureCache.getTexture(material.getTexturePath().getAsPath());
//        uniforms.setUniform("textureHandle", texture == null ? 0 : texture.getBindlessHandle());
//        uniforms.setUniform("diffuse", material.getDiffuseColor().convertToGLVector4f());
//        uniforms.setUniform("hasTexture", texture == null ? 0 : texture.getBindlessHandle() != 0 && !texture.equals(textureCache.getTexture(TextureCache.DEFAULT_TEXTURE.getPath(), TextureCache.TextureMapType.ALBEDO))  ? 1 : 0);

        uniforms.setUniform("modelMatrix", skyBoxEntity.getTransform().getTransformMatrix());
        glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);

        shaderProgram.unbind();
    }

    @Override
    protected void setupData() {

    }
}
