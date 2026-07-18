package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.library.opengl.wrapper.enums.DataType;
import net.ice.curio.library.opengl.wrapper.enums.FramebufferTarget;
import net.ice.curio.library.opengl.wrapper.enums.Usage;
import net.ice.curio.library.opengl.wrapper.glsl.GLSLStruct;
import net.ice.curio.library.opengl.object.buffer.DrawIndirectBuffer;
import net.ice.curio.library.opengl.object.buffer.ShaderStorageBufferObject;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.cache.MaterialCache;
import net.ice.relic.core.model.Material;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLManager;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShader;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;
import net.ice.relic.core.rendering.backend.opengl.depricated.buffer.UniformBufferObject;
import net.ice.relic.core.rendering.backend.opengl.depricated.model.Model;
import net.ice.relic.core.rendering.shader.ShaderType;
import net.ice.relic.core.scene.SceneObject;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.ice.curio.library.opengl.wrapper.enums.Usage.DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL43.glMultiDrawElementsIndirect;

public class GLSceneRenderer implements Lifecycle {

    private DrawIndirectBuffer staticCommandBuffer;

    private GLShaderProgram shaderProgram;

    private int staticDrawCount;

    private final GLRenderer glRenderer;

    private final Map<String, Integer> objectIndexMap;
    private UniformBufferObject uniforms;

    private ShaderStorageBufferObject materialBuffer;
    private ShaderStorageBufferObject albedoMapBuffer;
    private ShaderStorageBufferObject normalMapBuffer;
    private ShaderStorageBufferObject pbrMapBuffer;

    public GLSceneRenderer(GLRenderer glRenderer) {
        this.glRenderer = glRenderer;

        this.objectIndexMap = new HashMap<>();
    }

    @Override
    public void init() {
        this.shaderProgram = new GLShaderProgram().attach(List.of(
                new GLShader(ShaderType.VERTEX).load("scene.vert", ShaderType.VERTEX, false),
                new GLShader(ShaderType.FRAGMENT).load("scene.frag", ShaderType.FRAGMENT, false)
        ));

        this.uniforms = new UniformBufferObject(shaderProgram);
        uniforms.createUniform("projectionMatrix");
        uniforms.createUniform("viewMatrix");

        for (int i = 0; i < 200; i++) {
            String name = "instances[" + i + "]";
            uniforms.createUniform(name + ".modelMatrix");
            uniforms.createUniform(name + ".materialIndex");
        }
    }

    @Override
    public void render() {
        shaderProgram.bind();
        glRenderer.getGeometryBuffer().bind(FramebufferTarget.FRAMEBUFFER);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDisable(GL_BLEND);

        uniforms.setUniform("projectionMatrix", glRenderer.getApplication().getCurrentScene().getMatrix().getProjMatrix());
        uniforms.setUniform("viewMatrix", glRenderer.getApplication().getCurrentScene().getCamera().getViewMatrix());

        int drawElement = 0;
        for (Model model : glRenderer.getApplication().getCurrentScene().getModels().values()) {
            if (model.isAnimated()) continue;
            for (GLRenderer.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                for (SceneObject object : model.getSceneObjects()) {
                    String name = uniforms.formatUniform("instances", drawElement);
                    uniforms.setUniform(name + ".modelMatrix", object.getTransform().getTransformMatrix());
                    uniforms.setUniform(name + ".materialIndex", meshDrawData.materialIdx());
                    drawElement++;
                }
            }
        }

        staticCommandBuffer.bind();
        glRenderer.getStaticArrayObject().bind();
        glMultiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, staticDrawCount, 0);
        glRenderer.getStaticArrayObject().unbind();
        glRenderer.getGeometryBuffer().unbind(FramebufferTarget.FRAMEBUFFER);
        shaderProgram.unbind();
    }

    private void setupObjectData() {
        objectIndexMap.clear();
        int objectIndex = 0;
        for (Model model : glRenderer.getApplication().getCurrentScene().getModels().values()) {
            for (SceneObject object : model.getSceneObjects()) {
                objectIndexMap.put(object.getName(), objectIndex);
                objectIndex++;
            }
        }
    }

    public void setupBuffers() {
        setupObjectData();
        setupStaticCommandBuffer();
        setupMaterialUniforms(glRenderer.getApplication().getCurrentScene().getModelLoader().getMaterialCache());
    }



    private void setupStaticCommandBuffer() {
        List<Model> models = glRenderer.getApplication().getCurrentScene().getModels().values().stream().filter(m -> !m.isAnimated()).toList();

        int numMeshes = 0;
        int firstIndex = 0;
        int baseInstance = 0;

        for (Model model : models) {
            numMeshes += model.getMeshDrawData().size();
        }

        ByteBuffer commandBuffer = MemoryUtil.memAlloc(numMeshes * 5 * 4);
        for (Model model : models) {
            List<SceneObject> entities = model.getSceneObjects();
            int numEntities = entities.size();
            for (GLRenderer.MeshDrawData meshDrawData : model.getMeshDrawData()) {
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
        staticDrawCount = commandBuffer.remaining() / 20;

        staticCommandBuffer = new DrawIndirectBuffer();
        staticCommandBuffer.bind();
        staticCommandBuffer.bufferData(commandBuffer, Usage.DYNAMIC_DRAW);

        MemoryUtil.memFree(commandBuffer);
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
}
