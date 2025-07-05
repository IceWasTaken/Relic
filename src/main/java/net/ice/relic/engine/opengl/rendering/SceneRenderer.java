package net.ice.relic.engine.opengl.rendering;

import net.ice.relic.engine.RelicApplication;
import net.ice.relic.engine.opengl.*;
import net.ice.relic.engine.opengl.model.Material;
import net.ice.relic.engine.opengl.model.Model;
import net.ice.relic.engine.opengl.model.texture.Texture;
import net.ice.relic.engine.opengl.model.texture.TextureLoader;
import net.ice.relic.engine.opengl.scene.SceneObject;
import org.lwjgl.system.MemoryUtil;
import org.tinylog.Logger;

import java.nio.ByteBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL43.*;

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
    }

    @Override
    protected void initUniforms() {
        uniforms.createUniform("projectionMatrix");
        uniforms.createUniform("viewMatrix");

        for (int i = 0; i < config.getMaxTextures(); i++) {
            uniforms.createUniform(uniforms.formatUniform("textureSampler", i));
        }

        for (int i = 0; i < config.getMaxMaterials(); i++) {
            uniforms.createUniform(uniforms.formatUniform("materials", i) + ".diffuse");
            uniforms.createUniform(uniforms.formatUniform("materials", i) + ".specular");
            uniforms.createUniform(uniforms.formatUniform("materials", i) + ".reflectance");
            uniforms.createUniform(uniforms.formatUniform("materials", i) + ".normalMapIndex");
            uniforms.createUniform(uniforms.formatUniform("materials", i) + ".textureIndex");
            uniforms.createUniform(uniforms.formatUniform("materials", i) + ".ormMapIndex");
        }

        for (int i = 0; i < config.getMaxDrawElements(); i++) {
            uniforms.createUniform(uniforms.formatUniform("drawElements", i) + ".modelMatrixIndex");
            uniforms.createUniform(uniforms.formatUniform("drawElements", i) + ".materialIndex");
        }

        for (int i = 0; i < config.getMaxSceneObjects(); i++) {
            uniforms.createUniform(uniforms.formatUniform("modelMatrices", i));
        }
    }

    @Override
    public void render(RenderingBuffer renderingBuffer, GeometryBuffer buffer) {
        buffer.bind(GL_FRAMEBUFFER);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        application.getWindow().refreshSize();
        glDisable(GL_BLEND);

        shaderProgram.bind();

        uniforms.setUniform("projectionMatrix", application.getCurrentScene().getCamera().getProjectionMatrix());
        uniforms.setUniform("viewMatrix", application.getCurrentScene().getCamera().getViewMatrix());

        TextureLoader textureLoader = application.getCurrentScene().getTextureLoader();
        List<Texture> textures = textureLoader.getTextures().stream().toList();

        int textureCount = textures.size();

        if (textureCount > config.getMaxTextures()) {
            Logger.warn("Too many textures loaded. Max: " + config.getMaxTextures() + ", Loaded: " + textureCount);
        }

        for (int i = 0; i < Math.min(config.getMaxTextures(), textureCount); i++) {
            uniforms.setUniform(uniforms.formatUniform("textureSampler", i), i);
            Texture texture = textures.get(i);
            glActiveTexture(GL_TEXTURE0 + i);
            texture.bind();
        }

        int entityIndex = 0;
        for (Model model : application.getCurrentScene().getModels().values()) {
            List<SceneObject> objects = model.getSceneObjects();
            for (SceneObject object : objects) {
                uniforms.setUniform(uniforms.formatUniform("modelMatrices", entityIndex), object.getModelMatrix());
                entityIndex++;
            }
        }

        //static
        int drawElement = 0;
        for (Model model : application.getCurrentScene().getModels().values()) {
            if (model.isAnimated()) {
                continue;
            }
            List<SceneObject> objects = model.getSceneObjects();
            for (RenderingBuffer.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                for (SceneObject object : objects) {
                    String name = uniforms.formatUniform("drawElements", drawElement);

                    uniforms.setUniform(name + ".modelMatrixIndex", objectIndexMap.get(object.getId()));
                    uniforms.setUniform(name + ".materialIndex", meshDrawData.materialIdx());
                    drawElement++;
                }
            }
        }

        glBindBuffer(GL_DRAW_INDIRECT_BUFFER, staticVBO.getId());
        glBindVertexArray(renderingBuffer.getStaticArrayObject().getId());
        glMultiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, staticDrawCount, 0);

        drawElement = 0;
        for (Model model : application.getCurrentScene().getModels().values()) {
            if (!model.isAnimated()) {
                continue;
            }
            for (RenderingBuffer.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                RenderingBuffer.AnimMeshDrawData animMeshDrawData = meshDrawData.animMeshDrawData();
                SceneObject entity = animMeshDrawData.entity();
                String name = uniforms.formatUniform("drawElements", drawElement);
                uniforms.setUniform(name + ".modelMatrixIndex", objectIndexMap.get(entity.getId()));
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

        ensureNoErrorBeforeContinue();
    }

    @Override
    protected void setupData() {
        setupObjectData();
        setupStaticCommandBuffer();
        setupAnimationCommandBuffer();
        setupMaterialUniforms(application.getCurrentScene().getTextureLoader(), application.getCurrentScene().getMaterialCache());
    }

    private void setupObjectData() {
        objectIndexMap.clear();
        int objectIndex = 0;
        for (Model model : application.getCurrentScene().getModels().values()) {
            List<SceneObject> objects = model.getSceneObjects();
            for (SceneObject object : objects) {
                objectIndexMap.put(object.getId(), objectIndex);
                objectIndex++;
            }
        }
    }

    private void setupStaticCommandBuffer() {
        List<Model> models = application.getCurrentScene().getModels().values().stream().filter(model -> !model.isAnimated()).toList();

        int meshCount = 0;
        int firstIndex = 0;
        int baseInstance = 0;

        for (Model model : models) {
            meshCount += model.getMeshDrawData().size();
        }

        ByteBuffer commandBuffer = MemoryUtil.memAlloc(meshCount * config.getCommandSize());

        for (Model model : models) {
            List<SceneObject> entities = model.getSceneObjects();
            int objCount = entities.size();
            for (RenderingBuffer.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                commandBuffer.putInt(meshDrawData.vertices());
                commandBuffer.putInt(objCount);
                commandBuffer.putInt(firstIndex);
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
        List<Model> models = application.getCurrentScene().getModels().values().stream().filter(Model::isAnimated).toList();

        int meshCount = 0;
        int firstIndex = 0;
        int baseInstance = 0;

        for(Model model : models) {
            meshCount += model.getMeshDrawData().size();
        }

        ByteBuffer commandBuffer = MemoryUtil.memAlloc(meshCount * config.getCommandSize());

        for(Model model : models) {
            for(RenderingBuffer.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                commandBuffer.putInt(meshDrawData.vertices());
                commandBuffer.putInt(1);
                commandBuffer.putInt(firstIndex);
                commandBuffer.putInt(meshDrawData.offset());
                commandBuffer.putInt(baseInstance);

                firstIndex += meshDrawData.vertices();
                baseInstance ++;
            }
        }
        commandBuffer.flip();

        animationDrawCount = commandBuffer.remaining() / config.getCommandSize();

        animatedVBO = new VertexBufferObject();
        animatedVBO.bind(GL_DRAW_INDIRECT_BUFFER);
        animatedVBO.bufferData(GL_DRAW_INDIRECT_BUFFER, commandBuffer, GL_DYNAMIC_DRAW);

        MemoryUtil.memFree(commandBuffer);
    }

    private void setupMaterialUniforms(TextureLoader textureLoader, MaterialCache materialCache) {
        List<Texture> textures = textureLoader.getTextures().stream().toList();
        int numTextures = textures.size();
        if (numTextures > config.getMaxTextures()) {
            Logger.warn("Only " + config.getMaxTextures() + " textures can be used");
        }
        Map<String, Integer> texturePosMap = new HashMap<>();
        for (int i = 0; i < Math.min(config.getMaxTextures(), numTextures); i++) {
            texturePosMap.put(textures.get(i).getTexturePath(), i);
        }

        shaderProgram.bind();
        List<Material> materialList = materialCache.getMaterialsList();
        int maxMaterials = config.getMaxMaterials();
        for (int i = 0; i < Math.min(materialList.size(), maxMaterials); i++) {
            Material material = materialCache.getMaterial(i);
            String name = uniforms.formatUniform("materials", i);

            uniforms.setUniform(name + ".diffuse", material.getDiffuseColor().convertToGLVector4f());
            uniforms.setUniform(name + ".specular", material.getSpecularColor().convertToGLVector4f());
            uniforms.setUniform(name + ".reflectance", material.getReflectance());

            int texIndex = 0;
            String diffusePath = material.getTexturePath();
            if (diffusePath != null) {
                texIndex = texturePosMap.getOrDefault(diffusePath, 0);
            }
            uniforms.setUniform(name + ".textureIndex", texIndex);

            int normalIndex = 0;
            String normalPath = material.getNormalMapPath();
            if (normalPath != null) {
                normalIndex = texturePosMap.getOrDefault(normalPath, 0);
            }
            uniforms.setUniform(name + ".normalMapIndex", normalIndex);

            int ormIndex = 0;
            String ormPath = material.getORMMapPath();
            if (ormPath != null) {
                ormIndex = texturePosMap.getOrDefault(ormPath, 0);
            }
            uniforms.setUniform(name + ".ormMapIndex", ormIndex);
        }
        shaderProgram.unbind();
    }
}