package net.ice.relic.core.rendering.backend.opengl.depricated.rendering.renderer;

import net.ice.curio.config.RendererConfig;
import net.ice.relic.core.cache.MaterialCache;
import net.ice.relic.core.model.Material;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLManager;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShader;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;
import net.ice.curio.library.opengl.wrapper.enums.DataType;
import net.ice.curio.library.opengl.wrapper.glsl.GLSLStruct;
import net.ice.curio.library.opengl.object.buffer.ShaderStorageBufferObject;
import net.ice.relic.core.rendering.backend.opengl.depricated.model.Model;
import net.ice.relic.core.rendering.backend.opengl.depricated.rendering.enums.RenderType;
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

import static net.ice.curio.library.opengl.wrapper.enums.Usage.DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL43.*;

@Deprecated
public class SceneRenderer {

    private final Map<String, Integer> objectIndexMap;

//    private VertexBufferObject staticVBO;
//    private VertexBufferObject animatedVBO;

    private ShaderStorageBufferObject materialBuffer;
    private ShaderStorageBufferObject albedoMapBuffer;
    private ShaderStorageBufferObject normalMapBuffer;
    private ShaderStorageBufferObject pbrMapBuffer;
    private GLShaderProgram shaderProgram;

    private GLManager manager;

    protected final List<IShader> shaders;
    private Pipeline pipeline;

    private int animationDrawCount;
    private int staticDrawCount;

    public SceneRenderer(GLManager manager) {
        this.manager = manager;
        this.shaders = new ArrayList<>();
        this.objectIndexMap = new HashMap<>();

    }

    public void init() {
        initShaders();
        this.shaderProgram = new GLShaderProgram().attach(shaders);
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
            for (GLRenderer.MeshDrawData meshDrawData : model.getMeshDrawData()) {
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
            for (GLRenderer.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                SceneObject object = meshDrawData.animMeshDrawData().entity();
                String name = pipeline.formatUniform("drawElements", drawElement);
                pipeline.setUniform(name + ".modelMatrixIndex", objectIndexMap.get(object.getName()));
                pipeline.setUniform(name + ".materialIndex", meshDrawData.materialIdx());
                drawElement++;
            }
        }

        pipeline.resume();
    }

    private void updateUniforms() {

    }

    public void changeRenderType(RenderType renderType) {
        this.shaders.clear();
        glUseProgram(0);

        switch (renderType) {
            case NORMAL -> {
                //manager.getGeometryBuffer().init();
                this.init();
            }
            case ALBEDO -> {
                shaderProgram.cleanup();
                shaders.add(new GLShader(ShaderType.VERTEX).load("albedo/scene.vert", ShaderType.VERTEX, false));
                shaders.add(new GLShader(ShaderType.FRAGMENT).load("albedo/scene.frag", ShaderType.FRAGMENT, false));
                this.shaderProgram = new GLShaderProgram().attach(shaders);
                setupPipeline();
            }
            case DEPTH -> {
                shaderProgram.cleanup();
                shaders.add(new GLShader(ShaderType.VERTEX).load("depth/scene.vert", ShaderType.VERTEX, false));
                shaders.add(new GLShader(ShaderType.FRAGMENT).load("depth/scene.frag", ShaderType.FRAGMENT, false));
                this.shaderProgram = new GLShaderProgram().attach(shaders);
                setupPipeline();
            }
            case NORMALS -> {
                shaderProgram.cleanup();
                shaders.add(new GLShader(ShaderType.VERTEX).load("normal/scene.vert", ShaderType.VERTEX, false));
                shaders.add(new GLShader(ShaderType.FRAGMENT).load("normal/scene.frag", ShaderType.FRAGMENT, false));
                this.shaderProgram = new GLShaderProgram().attach(shaders);
                setupPipeline();
            }
            case PBR -> {
                shaderProgram.cleanup();
                shaders.add(new GLShader(ShaderType.VERTEX).load("pbr/scene.vert", ShaderType.VERTEX, false));
                shaders.add(new GLShader(ShaderType.FRAGMENT).load("pbr/scene.frag", ShaderType.FRAGMENT, false));
                this.shaderProgram = new GLShaderProgram().attach(shaders);
                setupPipeline();
            }
            case POS -> {
                shaderProgram.cleanup();
                shaders.add(new GLShader(ShaderType.VERTEX).load("pos/scene.vert", ShaderType.VERTEX, false));
                shaders.add(new GLShader(ShaderType.FRAGMENT).load("pos/scene.frag", ShaderType.FRAGMENT, false));
                this.shaderProgram = new GLShaderProgram().attach(shaders);
                setupPipeline();
            }
            case SHADOW -> {
                shaderProgram.cleanup();
                shaders.add(new GLShader(ShaderType.VERTEX).load("shadows/shadow.vert", ShaderType.VERTEX, false));
                shaders.add(new GLShader(ShaderType.GEOMETRY).load("shadows/shadow.geom", ShaderType.GEOMETRY, false));
                shaders.add(new GLShader(ShaderType.FRAGMENT).load("shadows/shadow.frag", ShaderType.FRAGMENT, false));
                this.shaderProgram = new GLShaderProgram().attach(shaders);
                setupPipeline();
            }
        }

    }

    public void setupData() {
        setupObjectData();
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



    private void setupAnimationCommandBuffer() {
        List<Model> models = manager.getApplication().getCurrentScene().getModels().values().stream()
                .filter(Model::isAnimated).toList();

        int meshCount = models.stream().mapToInt(m -> m.getMeshDrawData().size()).sum();
        ByteBuffer commandBuffer = MemoryUtil.memAlloc(meshCount * RendererConfig.getCommandSize());

        int firstIndex = 0, baseInstance = 0;
        for (Model model : models) {
            for (GLRenderer.MeshDrawData meshDrawData : model.getMeshDrawData()) {
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
        animationDrawCount = commandBuffer.remaining() / RendererConfig.getCommandSize();

//        animatedVBO = new VertexBufferObject();
//        animatedVBO.bind(GL_DRAW_INDIRECT_BUFFER);
//        animatedVBO.bufferData(GL_DRAW_INDIRECT_BUFFER, commandBuffer, DrawType.DYNAMIC);
//        MemoryUtil.memFree(commandBuffer);
    }

    public void setupMaterialUniforms(MaterialCache materialCache) {
        List<Material> materialList = materialCache.getMaterialsList();
        int materialCount = materialList.size();

        GLSLStruct materialStructFormat = new GLSLStruct(List.of(
                DataType.VEC4, //16
                DataType.VEC4, //32
                DataType.FLOAT32, //36
                DataType.FLOAT32, //40
                DataType.FLOAT32, //44
                DataType.FLOAT32
        ));

        GLSLStruct mapStructFormat = new GLSLStruct(List.of(DataType.UINT64));

        this.materialBuffer = new ShaderStorageBufferObject(materialStructFormat, materialCount);
        this.albedoMapBuffer = new ShaderStorageBufferObject(mapStructFormat, materialCount);
        this.normalMapBuffer = new ShaderStorageBufferObject(mapStructFormat, materialCount);
        this.pbrMapBuffer = new ShaderStorageBufferObject(mapStructFormat, materialCount);

        int index = 0;
        for (Material material : materialList) {
            materialBuffer
                    .setVec4(0, index, material.getDiffuseColor().div().vec4f())
                    .setVec4(1, index, material.getSpecularColor().div().vec4f());

            albedoMapBuffer.setLong(0, index, material.getTextureHandle());
            normalMapBuffer.setLong(0, index, material.getNormalHandle());
            pbrMapBuffer.setLong(0, index, material.getRoughnessHandle());

            materialBuffer
                    .setFloat(2, index, material.getReflectance())
                    .setFloat(3, index, material.getRoughnessFactor())
                    .setFloat(4, index, material.getMetallicFactor())
                    .setFloat(5, index, 0);

            index++;
        }

        materialBuffer.bind();
        materialBuffer.bindBase(7);
        materialBuffer.syncToGPU(DYNAMIC_DRAW);

        albedoMapBuffer.bind();
        albedoMapBuffer.bindBase(8);
        albedoMapBuffer.syncToGPU(DYNAMIC_DRAW);

        normalMapBuffer.bind();
        normalMapBuffer.bindBase(9);
        normalMapBuffer.syncToGPU(DYNAMIC_DRAW);

        pbrMapBuffer.bind();
        pbrMapBuffer.bindBase(10);
        pbrMapBuffer.syncToGPU(DYNAMIC_DRAW);
    }

    private void setupPipeline() {
        Pipeline.PipelineBuilder pipelineBuilder = new Pipeline.PipelineBuilder()
                .bindShaderProgram(shaderProgram)
                .uniform("projectionMatrix", shaderProgram)
                .uniform("viewMatrix", shaderProgram);

        for (int i = 0; i < RendererConfig.getMaxDrawElements(); i++) {
            String name = "drawElements[" + i + "]";
            pipelineBuilder
                    .uniform(name + ".modelMatrixIndex", shaderProgram)
                    .uniform(name + ".materialIndex", shaderProgram);
        }

        for (int i = 0; i < RendererConfig.getMaxSceneObjects(); i++) {
            pipelineBuilder.uniform("modelMatrices[" + i + "]", shaderProgram);
        }

        pipelineBuilder
                .pauseHere()

                //.bindVertexBufferObject(staticVBO, DRAW_INDIRECT)
                //.bindVertexArrayObject(manager.getStaticArrayObject())
                .multiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, staticDrawCount, 0)

                .pauseHere()

                //.bindVertexBufferObject(animatedVBO, DRAW_INDIRECT)
                //bindVertexArrayObject(manager.getAnimationArrayObject())
                .multiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, animationDrawCount, 0)
                .unbindVertexArrayObject()
                .enable(GL_BLEND)
                .unbindShaderProgram(shaderProgram);

        this.pipeline = pipelineBuilder.build();
    }

}
