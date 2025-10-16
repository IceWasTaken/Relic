package net.ice.relic.core.rendering.backend.opengl.rendering.renderer;

import net.ice.relic.core.ShadowData;
import net.ice.relic.core.Shadows;
import net.ice.relic.core.cache.MaterialCache;
import net.ice.relic.core.model.Material;
import net.ice.relic.core.rendering.backend.opengl.AbstractGLRenderer;
import net.ice.relic.core.rendering.backend.opengl.GLManager;
import net.ice.relic.core.rendering.backend.opengl.buffer.ShaderStorageBufferObject;
import net.ice.relic.core.rendering.backend.opengl.buffer.VertexBufferObject;
import net.ice.relic.core.rendering.backend.opengl.enums.DrawType;
import net.ice.relic.core.rendering.backend.opengl.model.Model;
import net.ice.relic.core.rendering.backend.opengl.rendering.buffer.ShadowBuffer;
import net.ice.relic.core.rendering.shader.ShaderType;
import net.ice.relic.core.scene.SceneObject;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.ice.relic.core.rendering.backend.opengl.GLUtil.assertNoError;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL40.GL_DRAW_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL43.glMultiDrawElementsIndirect;

public class ShadowRenderer extends AbstractGLRenderer {

    private int animationDrawCount;
    private int staticDrawCount;

    private VertexBufferObject staticVBO;
    private VertexBufferObject animatedVBO;
    private ShaderStorageBufferObject shaderStorage;

    private ShadowBuffer shadowBuffer;
    private Shadows shadows;
    private Map<String, Integer> objectIndexMap;

    public ShadowRenderer(GLManager glManager) {
        super(glManager);

        this.objectIndexMap = new HashMap<>();
        this.shadows = new Shadows();
    }

    @Override
    public void init() {
        super.init();

        this.shadowBuffer = new ShadowBuffer();

    }

    @Override
    protected void initShaders() {
        loadShader("shadow.vert", ShaderType.VERTEX);
        loadShader("shadow.geom", ShaderType.GEOMETRY);
        loadShader("shadow.frag", ShaderType.FRAGMENT);
    }

    @Override
    protected void initUniforms() {
        for (int i = 0; i < config.getMaxDrawElements(); i++) {
            uniforms.createUniform(uniforms.formatUniform("drawElements", i) + ".modelMatrix");
            uniforms.createUniform(uniforms.formatUniform("drawElements", i) + ".materialIndex");
        }
    }

    @Override
    public void render() {
        Shadows.update(shadows, manager.getApplication().getCurrentScene());

        shadowBuffer.getShadowMapFBO().bindFrameBuffer();
        shadowBuffer.shadowViewport();
        shaderProgram.bind();

        int entityIndex = 0;
        for(Model model : manager.getApplication().getCurrentScene().getModels().values()) {
            List<SceneObject> objects = model.getSceneObjects();
            for(SceneObject object : objects) {
                uniforms.setUniform(uniforms.formatUniform("drawElements", entityIndex) + ".modelMatrix", object.getTransform().getTransformMatrix());
                entityIndex++;
            }
        }

        for (int i = 0; i < Shadows.SHADOW_MAP_COUNT; i++) {
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowBuffer.getShadowMapArrayTexture().getIds()[i], 0);
            glClear(GL_DEPTH_BUFFER_BIT);
        }

        int drawElement = 0;
        for (Model model: manager.getApplication().getCurrentScene().getModels().values()) {
            if (model.isAnimated()) {
                continue;
            }
            List<SceneObject> entities = model.getSceneObjects();
            for (GLManager.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                for (SceneObject entity : entities) {
                    uniforms.setUniform(uniforms.formatUniform("drawElements", drawElement) + ".materialIndex", objectIndexMap.get(entity.getName()));
                    drawElement++;
                }
            }
        }
        staticVBO.bind(GL_DRAW_INDIRECT_BUFFER);
        manager.getStaticArrayObject().bind();
        for (int i = 0; i < Shadows.SHADOW_MAP_COUNT; i++) {
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowBuffer.getShadowMapArrayTexture().getIds()[i], 0);
            ShadowData shadow = shadows.getShadowData().get(i);
            glMultiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, staticDrawCount, 0);
        }

        drawElement = 0;
        for (Model model: manager.getApplication().getCurrentScene().getModels().values()) {
            if (!model.isAnimated()) {
                continue;
            }
            for (GLManager.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                GLManager.AnimMeshDrawData animMeshDrawData = meshDrawData.animMeshDrawData();
                SceneObject entity = animMeshDrawData.entity();
                uniforms.setUniform("drawElements" + drawElement + ".modelMatrix", objectIndexMap.get(entity.getName()));
                drawElement++;
            }
        }
        animatedVBO.bind(GL_DRAW_INDIRECT_BUFFER);
        manager.getAnimationArrayObject().bind();
        for (int i = 0; i < Shadows.SHADOW_MAP_COUNT; i++) {
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowBuffer.getShadowMapArrayTexture().getIds()[i], 0);
            ShadowData shadow = shadows.getShadowData().get(i);
            glMultiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, animationDrawCount, 0);
        }

        glBindVertexArray(0);
        assertNoError();
    }

    @Override
    public void setupData() {
        setupEntitiesData();
        setupStaticCommandBuffer();
        setupAnimatedCommandBuffer();
        setupMaterialUniforms(manager.getApplication().getCurrentScene().getModelLoader().getMaterialCache());
    }

    private void setupEntitiesData() {
        objectIndexMap.clear();
        int entityIdx = 0;
        for (Model model : manager.getApplication().getCurrentScene().getModels().values()) {
            List<SceneObject> entities = model.getSceneObjects();
            for (SceneObject entity : entities) {
                objectIndexMap.put(entity.getName(), entityIdx);
                entityIdx++;
            }
        }
    }

    private void setupStaticCommandBuffer() {
        List<Model> modelList = manager.getApplication().getCurrentScene().getModels().values().stream().filter(m -> !m.isAnimated()).toList();
        int numMeshes = 0;
        for (Model model : manager.getApplication().getCurrentScene().getModels().values()) {
            numMeshes += model.getMeshDrawData().size();
        }

        int firstIndex = 0;
        int baseInstance = 0;
        ByteBuffer commandBuffer = MemoryUtil.memAlloc(numMeshes * config.getCommandSize());
        for (Model model : modelList) {
            List<SceneObject> entities = model.getSceneObjects();
            int numEntities = entities.size();
            for (GLManager.MeshDrawData meshDrawData : model.getMeshDrawData()) {
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
        staticVBO.bufferData(GL_DRAW_INDIRECT_BUFFER, commandBuffer, DrawType.DYNAMIC);

        MemoryUtil.memFree(commandBuffer);
    }

    private void setupAnimatedCommandBuffer() {
        List<Model> modelList = manager.getApplication().getCurrentScene().getModels().values().stream().filter(Model::isAnimated).toList();
        int numMeshes = 0;
        for (Model model : modelList) {
            numMeshes += model.getMeshDrawData().size();
        }

        int firstIndex = 0;
        int baseInstance = 0;
        ByteBuffer commandBuffer = MemoryUtil.memAlloc(numMeshes * config.getCommandSize());
        for (Model model : modelList) {
            for (GLManager.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                GLManager.AnimMeshDrawData animMeshDrawData = meshDrawData.animMeshDrawData();
                SceneObject entity = animMeshDrawData.entity();

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
        animatedVBO.bufferData(GL_DRAW_INDIRECT_BUFFER, commandBuffer, DrawType.DYNAMIC);

        MemoryUtil.memFree(commandBuffer);
    }

    @Override
    public void cleanup() {
        super.cleanup();

        shadowBuffer.cleanup();
        staticVBO.delete();
        animatedVBO.delete();
    }

    public void setupMaterialUniforms(MaterialCache materialCache) {
        List<Material> materialList = materialCache.getMaterialsList();

        ShaderStorageBufferObject.Builder shaderStorageBuilder = new ShaderStorageBufferObject.Builder();
        shaderProgram.bind();

        for (Material material : materialList) {
            shaderStorageBuilder.addVec4f(material.getDiffuseColor().convertToGLVector4f())
                    .addVec4f(material.getSpecularColor().convertToGLVector4f())
                    .addFloat(material.getReflectance())
                    .addFloat(0)
                    .addFloat(0)
                    .addFloat(0)
                    .addLong(material.hasTexture() ? material.getTextureHandle() : 0L)
                    .addLong(material.hasNormalMap() ? material.getNormalHandle() : 0L);
        }
        shaderStorage = shaderStorageBuilder.build();
        shaderStorage.bind();
        shaderStorage.bindBase(5);
        shaderStorage.bufferData(GL_STATIC_DRAW);
    }

    public Shadows getShadows() {
        return shadows;
    }

    public ShadowBuffer getShadowBuffer() {
        return shadowBuffer;
    }
}
