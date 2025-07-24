package net.ice.relic.engine.opengl.rendering.renderer;

import net.ice.relic.annotations.Rewrite;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.common.cache.MaterialCache;
import net.ice.relic.engine.opengl.*;
import net.ice.relic.common.model.Material;
import net.ice.relic.engine.opengl.model.Model;
import net.ice.relic.common.scene.SceneObject;
import net.ice.relic.engine.opengl.rendering.buffer.RenderingBuffers;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.ARBBindlessTexture.*;

@Rewrite
public class SceneRenderer extends AbstractRenderer {

    private final Map<String, Integer> objectIndexMap;

    private VertexBufferObject staticVBO;
    private VertexBufferObject animatedVBO;

    private int animationDrawCount;
    private int staticDrawCount;

    public SceneRenderer(RelicApplication application) {
        super(application);
        this.objectIndexMap = new HashMap<>();
    }

    @Override
    protected void initShaders() {
        loadShader("scene.vert", Shader.ShaderType.VERTEX);
        loadShader("scene.frag", Shader.ShaderType.FRAGMENT);

        assertNoError();
    }

    @Override
    protected void initUniforms() {
        uniforms.createUniform("projectionMatrix");
        uniforms.createUniform("viewMatrix");

        for (int i = 0; i < config.getMaxMaterials(); i++) {
            String prefix = uniforms.formatUniform("materials", i);
            uniforms.createUniform(prefix + ".diffuse");
            uniforms.createUniform(prefix + ".specular");
            uniforms.createUniform(prefix + ".reflectance");

            uniforms.createUniform(prefix + ".textureHandle");
            uniforms.createUniform(prefix + ".normalHandle");
        }

        for (int i = 0; i < config.getMaxDrawElements(); i++) {
            String name = "drawElements[" + i + "]";
            uniforms.createUniform(name + ".modelMatrixIndex");
            uniforms.createUniform(name + ".materialIndex");
        }

        for (int i = 0; i < config.getMaxSceneObjects(); i++) {
            uniforms.createUniform(uniforms.formatUniform("modelMatrices", i));
        }

        assertNoError();
    }

    @Override
    public void render() {
        geometryBuffer.bind(GL_FRAMEBUFFER);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, geometryBuffer.getWidth(), geometryBuffer.getHeight());
        glDisable(GL_BLEND);

        shaderProgram.bind();

        uniforms.setUniform("projectionMatrix", application.getCurrentScene().getMatrix().getProjMatrix());
        uniforms.setUniform("viewMatrix", application.getCurrentScene().getCamera().getViewMatrix());

        int entityIndex = 0;
        for (Model model : application.getCurrentScene().getModels().values()) {
            for (SceneObject object : model.getSceneObjects()) {
                uniforms.setUniform(uniforms.formatUniform("modelMatrices", entityIndex), object.getTransform().getTransformMatrix());
                entityIndex++;
            }
        }

        // static
        int drawElement = 0;
        for (Model model : application.getCurrentScene().getModels().values()) {
            if (model.isAnimated()) continue;
            for (RenderingBuffers.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                for (SceneObject object : model.getSceneObjects()) {
                    String name = uniforms.formatUniform("drawElements", drawElement);
                    uniforms.setUniform(name + ".modelMatrixIndex", objectIndexMap.get(object.getName()));
                    uniforms.setUniform(name + ".materialIndex", meshDrawData.materialIdx());
                    drawElement++;

                }
            }
        }

        glBindBuffer(GL_DRAW_INDIRECT_BUFFER, staticVBO.getId());
        glBindVertexArray(renderingBuffer.getStaticArrayObject().getId());
        glMultiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, staticDrawCount, 0);

        // animated
        drawElement = 0;
        for (Model model : application.getCurrentScene().getModels().values()) {
            if (!model.isAnimated()) continue;
            for (RenderingBuffers.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                SceneObject object = meshDrawData.animMeshDrawData().entity();
                String name = uniforms.formatUniform("drawElements", drawElement);
                uniforms.setUniform(name + ".modelMatrixIndex", objectIndexMap.get(object.getName()));
                uniforms.setUniform(name + ".materialIndex", meshDrawData.materialIdx());
                drawElement++;
            }
        }

        glBindBuffer(GL_DRAW_INDIRECT_BUFFER, animatedVBO.getId());
        glBindVertexArray(renderingBuffer.getAnimationArrayObject().getId());
        glMultiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, animationDrawCount, 0);

        glBindVertexArray(0);
        glEnable(GL_BLEND);
        shaderProgram.unbind();

        assertNoError();
    }

    @Override
    protected void setupData() {
        setupObjectData();
        setupStaticCommandBuffer();
        setupAnimationCommandBuffer();
        setupMaterialUniforms(application.getCurrentScene().getMaterialCache());
    }

    private void setupObjectData() {
        objectIndexMap.clear();
        int objectIndex = 0;
        for (Model model : application.getCurrentScene().getModels().values()) {
            for (SceneObject object : model.getSceneObjects()) {
                objectIndexMap.put(object.getName(), objectIndex);
                objectIndex++;
            }
        }
    }

    private void setupStaticCommandBuffer() {
        List<Model> models = application.getCurrentScene().getModels().values().stream().filter(m -> !m.isAnimated()).toList();

        int numMeshes = 0;
        int firstIndex = 0;
        int baseInstance = 0;

        for (Model model : models) {
            numMeshes += model.getMeshDrawData().size();
        }

        ByteBuffer commandBuffer = MemoryUtil.memAlloc(numMeshes * config.getCommandSize());
        for (Model model : models) {
            List<SceneObject> entities = model.getSceneObjects();
            int numEntities = entities.size();
            for (RenderingBuffers.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                // count
                commandBuffer.putInt(meshDrawData.vertices());
                // instanceCount
                commandBuffer.putInt(numEntities);
                commandBuffer.putInt(firstIndex);
                // baseVertex
                commandBuffer.putInt(meshDrawData.offset());
                commandBuffer.putInt(baseInstance);

                firstIndex += meshDrawData.vertices();
                baseInstance += entities.size();
            }
        }

        commandBuffer.flip();
        staticDrawCount = commandBuffer.remaining() / config.getCommandSize();

        staticVBO = new VertexBufferObject();
        staticVBO.bind(GL_DRAW_INDIRECT_BUFFER);
        staticVBO.bufferData(GL_DRAW_INDIRECT_BUFFER, commandBuffer, GL_DYNAMIC_DRAW);

        MemoryUtil.memFree(commandBuffer);
    }

    private void setupAnimationCommandBuffer() {
        List<Model> models = application.getCurrentScene().getModels().values().stream()
                .filter(Model::isAnimated).toList();

        int meshCount = models.stream().mapToInt(m -> m.getMeshDrawData().size()).sum();
        ByteBuffer commandBuffer = MemoryUtil.memAlloc(meshCount * config.getCommandSize());

        int firstIndex = 0, baseInstance = 0;
        for (Model model : models) {
            for (RenderingBuffers.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                commandBuffer.putInt(meshDrawData.vertices());
                commandBuffer.putInt(1);
                commandBuffer.putInt(firstIndex);
                commandBuffer.putInt(meshDrawData.offset());
                commandBuffer.putInt(baseInstance);

                firstIndex += meshDrawData.vertices();
                baseInstance++;
            }
        }

        commandBuffer.flip();
        animationDrawCount = commandBuffer.remaining() / config.getCommandSize();

        animatedVBO = new VertexBufferObject();
        animatedVBO.bind(GL_DRAW_INDIRECT_BUFFER);
        animatedVBO.bufferData(GL_DRAW_INDIRECT_BUFFER, commandBuffer, GL_DYNAMIC_DRAW);
        MemoryUtil.memFree(commandBuffer);
    }

    public void setupMaterialUniforms(MaterialCache materialCache) {
        List<Material> materialList = materialCache.getMaterialsList();
        int materialCount = materialList.size();
        shaderProgram.bind();

        for (int i = 0; i < materialCount; i++) {
            Material material = materialList.get(i);
            String prefix = uniforms.formatUniform("materials", i);

            uniforms.setUniform(prefix + ".diffuse", material.getDiffuseColor().convertToGLVector4f());
            uniforms.setUniform(prefix + ".specular", material.getSpecularColor().convertToGLVector4f());
            uniforms.setUniform(prefix + ".reflectance", material.getReflectance());

            long texHandle = material.hasTexture() ? material.getTextureHandle() : 0L;
            long normalHandle = material.hasNormalMap() ? material.getNormalHandle() : 0L;

            if (texHandle != 0L && !material.getTexture().isResident()) glMakeTextureHandleResidentARB(texHandle);
            if (normalHandle != 0L && !material.getNormalMap().isResident()) glMakeTextureHandleResidentARB(normalHandle);

            uniforms.setUniform(prefix + ".textureHandle", texHandle);
            uniforms.setUniform(prefix + ".normalHandle", normalHandle);

            System.out.println("Texture handle for terrain: " + material.getTextureHandle());
            System.out.println("Texture path for terrain: " + material.getTexturePath());
            System.out.println("Normal handle for terrain: " + material.getNormalHandle());
            System.out.println("Normal path for terrain: " + material.getNormalMapPath());
        }
        shaderProgram.unbind();
    }
}
