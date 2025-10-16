package net.ice.relic.core.rendering.backend.opengl.rendering.renderer;

import net.ice.relic.common.annotations.Rewrite;
import net.ice.relic.core.cache.MaterialCache;
import net.ice.relic.core.config.configs.RendererConfig;
import net.ice.relic.core.model.Material;
import net.ice.relic.core.rendering.backend.opengl.GLManager;
import net.ice.relic.core.rendering.backend.opengl.GLShader;
import net.ice.relic.core.rendering.backend.opengl.GLShaderProgram;
import net.ice.relic.core.rendering.backend.opengl.buffer.ShaderStorageBufferObject;
import net.ice.relic.core.rendering.backend.opengl.buffer.UniformBufferObject;
import net.ice.relic.core.rendering.backend.opengl.buffer.VertexBufferObject;
import net.ice.relic.core.rendering.backend.opengl.enums.DrawType;
import net.ice.relic.core.rendering.backend.opengl.model.Model;
import net.ice.relic.core.rendering.backend.opengl.rendering.enums.RenderType;
import net.ice.relic.core.rendering.pipeline.Pipeline;
import net.ice.relic.core.rendering.shader.IShader;
import net.ice.relic.core.rendering.shader.ShaderType;
import net.ice.relic.core.scene.SceneObject;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.ice.relic.core.rendering.backend.opengl.GLUtil.assertNoError;
import static net.ice.relic.core.rendering.backend.opengl.enums.BufferTarget.DRAW_INDIRECT;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GLUtil.setupDebugMessageCallback;

@Rewrite
public class SceneRenderer {

    private final Map<String, Integer> objectIndexMap;

    private VertexBufferObject staticVBO;
    private VertexBufferObject animatedVBO;
    private ShaderStorageBufferObject shaderStorage;

    private GLShaderProgram shaderProgram;

    protected RendererConfig config;

    private GLManager manager;

    protected final List<IShader> shaders;
    private Pipeline pipeline;

    private int animationDrawCount;
    private int staticDrawCount;

    public SceneRenderer(GLManager manager) {
        this.manager = manager;
        this.shaders = new ArrayList<>();
        this.config = manager.getRendererConfig();
        this.objectIndexMap = new HashMap<>();
    }

    public void init() {
        initShaders();
        this.shaderProgram = new GLShaderProgram().attach(shaders);
        assertNoError();
    }

    protected void initShaders() {
        shaders.add(new GLShader(ShaderType.VERTEX).load("scene.vert", ShaderType.VERTEX, false));
        shaders.add(new GLShader(ShaderType.FRAGMENT).load("scene.frag", ShaderType.FRAGMENT, false));
    }


    public void render() {
        pipeline.reset();

        pipeline.execute();

        pipeline.setUniform("projectionMatrix", manager.getApplication().getCurrentScene().getMatrix().getProjMatrix());
        pipeline.setUniform("viewMatrix", manager.getApplication().getCurrentScene().getCamera().getViewMatrix());

        int entityIndex = 0;
        for (Model model : manager.getApplication().getCurrentScene().getModels().values()) {
            for (SceneObject object : model.getSceneObjects()) {
                pipeline.setUniform(pipeline.formatUniform("modelMatrices", entityIndex), object.getTransform().getTransformMatrix());
                entityIndex++;
            }
        }

        int drawElement = 0;
        for (Model model : manager.getApplication().getCurrentScene().getModels().values()) {
            if (model.isAnimated()) continue;
            for (GLManager.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                for (SceneObject object : model.getSceneObjects()) {
                    String name = pipeline.formatUniform("drawElements", drawElement);
                    pipeline.setUniform(name + ".modelMatrixIndex", objectIndexMap.get(object.getName()));
                    pipeline.setUniform(name + ".materialIndex", meshDrawData.materialIdx());
                    drawElement++;
                }
            }
        }

        pipeline.resume();

        drawElement = 0;
        for (Model model : manager.getApplication().getCurrentScene().getModels().values()) {
            if (!model.isAnimated()) continue;
            for (GLManager.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                SceneObject object = meshDrawData.animMeshDrawData().entity();
                String name = pipeline.formatUniform("drawElements", drawElement);
                pipeline.setUniform(name + ".modelMatrixIndex", objectIndexMap.get(object.getName()));
                pipeline.setUniform(name + ".materialIndex", meshDrawData.materialIdx());
                drawElement++;
            }
        }

        pipeline.resume();
        assertNoError();
    }

    private void updateUniforms() {

    }

    public void changeRenderType(RenderType renderType) {
        this.shaders.clear();

        switch (renderType) {
            case NORMAL -> this.init();
            case NO_LIGHTING -> {
                shaderProgram.cleanup();
                shaders.add(new GLShader(ShaderType.VERTEX).load("nolighting/scene.vert", ShaderType.VERTEX, false));
                shaders.add(new GLShader(ShaderType.FRAGMENT).load("nolighting/scene.frag", ShaderType.FRAGMENT, false));
                this.shaderProgram = new GLShaderProgram().attach(shaders);
                assertNoError();
                setupPipeline();
            }
            case NORMAL_MAPS -> {
                shaderProgram.cleanup();
                shaders.add(new GLShader(ShaderType.VERTEX).load("normal/scene.vert", ShaderType.VERTEX, false));
                shaders.add(new GLShader(ShaderType.FRAGMENT).load("normal/scene.frag", ShaderType.FRAGMENT, false));
                this.shaderProgram = new GLShaderProgram().attach(shaders);
                assertNoError();
                setupPipeline();
            }
        }

    }

    public void setupData() {
        setupObjectData();
        setupStaticCommandBuffer();
        setupAnimationCommandBuffer();
        setupMaterialUniforms(manager.getApplication().getCurrentScene().getModelLoader().getMaterialCache());
        setupPipeline();
    }

    private void setupObjectData() {
        objectIndexMap.clear();
        int objectIndex = 0;
        for (Model model : manager.getApplication().getCurrentScene().getModels().values()) {
            for (SceneObject object : model.getSceneObjects()) {
                objectIndexMap.put(object.getName(), objectIndex);
                objectIndex++;
            }
        }
    }

    private void setupStaticCommandBuffer() {
        List<Model> models = manager.getApplication().getCurrentScene().getModels().values().stream().filter(m -> !m.isAnimated()).toList();

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

    private void setupAnimationCommandBuffer() {
        List<Model> models = manager.getApplication().getCurrentScene().getModels().values().stream()
                .filter(Model::isAnimated).toList();

        int meshCount = models.stream().mapToInt(m -> m.getMeshDrawData().size()).sum();
        ByteBuffer commandBuffer = MemoryUtil.memAlloc(meshCount * config.getCommandSize());

        int firstIndex = 0, baseInstance = 0;
        for (Model model : models) {
            for (GLManager.MeshDrawData meshDrawData : model.getMeshDrawData()) {
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

    public void setupMaterialUniforms(MaterialCache materialCache) {
        List<Material> materialList = materialCache.getMaterialsList();

        ShaderStorageBufferObject.Builder shaderStorageBuilder = new ShaderStorageBufferObject.Builder();

        for (Material material : materialList) {
            shaderStorageBuilder.addVec4f(material.getDiffuseColor().convertToGLVector4f())
                    .addVec4f(material.getSpecularColor().convertToGLVector4f())
                    .addFloat(material.getReflectance())
                    .addFloat(0)
                    .addFloat(0)
                    .addFloat(0)
                    .addLong(material.hasTexture() ? material.getTextureHandle() : 0L)
                    .addLong(material.hasNormalMap() ? material.getNormalHandle() : 0L);

            //long emissiveHandle = material.hasEmissiveMap() ? material.getEmissiveHandle() : 0L;
//            long specularHandle = material.hasSpecularMap() ? material.getSpecularHandle() : 0L;
//            long AOHandle = material.hasAOMap() ? material.getAOHandle() : 0L;
//            buffer.putLong(emissiveHandle);
//            buffer.putLong(specularHandle);
//            buffer.putLong(AOHandle);
        }
        shaderStorage = shaderStorageBuilder.build();
        shaderStorage.bind();
        shaderStorage.bindBase(5);
        shaderStorage.bufferData(GL_DYNAMIC_DRAW);
    }

    private void setupPipeline() {
        Pipeline.PipelineBuilder pipelineBuilder = new Pipeline.PipelineBuilder()
                .bindShaderProgram(shaderProgram)
                .uniform("projectionMatrix", shaderProgram)
                .uniform("viewMatrix", shaderProgram);

        for (int i = 0; i < config.getMaxDrawElements(); i++) {
            String name = "drawElements[" + i + "]";
            pipelineBuilder
                    .uniform(name + ".modelMatrixIndex", shaderProgram)
                    .uniform(name + ".materialIndex", shaderProgram);
        }

        for (int i = 0; i < config.getMaxSceneObjects(); i++) {
            pipelineBuilder.uniform("modelMatrices[" + i + "]", shaderProgram);
        }

        pipelineBuilder
                .pauseHere()

                .bindVertexBufferObject(staticVBO, DRAW_INDIRECT)
                .bindVertexArrayObject(manager.getStaticArrayObject())
                .multiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, staticDrawCount, 0)

                .pauseHere()

                .bindVertexBufferObject(animatedVBO, DRAW_INDIRECT)
                .bindVertexArrayObject(manager.getAnimationArrayObject())
                .multiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, animationDrawCount, 0)
                .unbindVertexArrayObject()
                .enable(GL_BLEND)
                .unbindShaderProgram(shaderProgram);

        this.pipeline = pipelineBuilder.build();
    }
}
