package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;
import net.ice.curio.graphics.object.Viewport;
import net.ice.curio.library.opengl.object.GLViewport;
import net.ice.curio.library.opengl.object.buffer.GLBuffer;
import net.ice.curio.library.opengl.wrapper.enums.Usage;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.cache.MaterialCache;
import net.ice.relic.core.model.Material;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;
import net.ice.relic.core.rendering.backend.opengl.Uniforms;
import net.ice.relic.core.rendering.backend.opengl.depricated.model.Model;
import net.ice.relic.core.scene.SceneObject;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL40.GL_DRAW_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL43.glMultiDrawElementsIndirect;

public class GLSceneRenderer implements Lifecycle {

    private GLBuffer staticCommandBuffer;

    private GLShaderProgram shaderProgram;

    private int staticDrawCount;

    private final GLRenderer glRenderer;

    private final Map<String, Integer> objectIndexMap;
    private Uniforms uniforms;
    private Viewport viewport;

    private GLBuffer materialBuffer;
    private Struct materialBufferStruct;

    private GLBuffer mapBuffer;
    private Struct mapBufferStruct;

    public GLSceneRenderer(GLRenderer glRenderer) {
        this.glRenderer = glRenderer;
        this.viewport = new GLViewport(
                glRenderer.getApplication().getWindow().getWidth(),
                glRenderer.getApplication().getWindow().getHeight()
        );
        this.objectIndexMap = new HashMap<>();
    }

    @Override
    public void init() {
        this.shaderProgram = new GLShaderProgram("scene");

        this.uniforms = new Uniforms(shaderProgram);
        uniforms.createUniform("projectionMatrix");
        uniforms.createUniform("viewMatrix");
    }

    @Override
    public void render() {
        shaderProgram.bind();
        viewport.bind();

        glRenderer.getGeometryBuffer().bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDisable(GL_BLEND);

        uniforms.setUniform("projectionMatrix", glRenderer.getApplication().getCurrentScene().getMatrix().getProjMatrix());
        uniforms.setUniform("viewMatrix", glRenderer.getApplication().getCurrentScene().getCamera().getViewMatrix());


        glBindBuffer(GL_DRAW_INDIRECT_BUFFER, staticCommandBuffer.getHandle());
        glRenderer.getStaticArrayObject().bind();

        glMultiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, staticDrawCount, 0);
        glBindVertexArray(0);

        glRenderer.getGeometryBuffer().unbind();
        shaderProgram.unbind();
    }

    public void resize(int width, int height) {
        this.viewport.resize(width, height);
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

        staticCommandBuffer = new GLBuffer();
        staticCommandBuffer.bufferData(commandBuffer, Usage.DYNAMIC_DRAW);

        MemoryUtil.memFree(commandBuffer);
    }

    public void setupMaterialUniforms(MaterialCache materialCache) {
        List<Material> materialList = materialCache.getMaterialsList();

        this.materialBufferStruct = new Material.MaterialStruct(StructType.STD430);

        this.mapBufferStruct = new Struct(StructType.STD430) {
            @Override
            public Class<?> getRecord() {
                return MapRecord.class;
            }
        };

        this.materialBuffer = new GLBuffer();
        this.mapBuffer = new GLBuffer();

        ByteBuffer dummy = MemoryUtil.memAlloc(44 * 200);
        materialBuffer.bufferData(dummy, Usage.STREAM_DRAW);
        MemoryUtil.memFree(dummy);

        dummy = MemoryUtil.memAlloc(8 * 3 * 200);
        mapBuffer.bufferData(dummy, Usage.STREAM_DRAW);
        MemoryUtil.memFree(dummy);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            int index = 0;
            for (Material material : materialList) {
                int base = materialBufferStruct.getStride() * index;


                materialBuffer
                        .bufferSubData(materialBufferStruct.getOffset(0) + base, material.getDiffuseColor().div().vec4f())
                        .bufferSubData(materialBufferStruct.getOffset(1) + base, material.getSpecularColor().div().vec4f())
                        .bufferSubData(materialBufferStruct.getOffset(2) + base, material.getReflectance())
                        .bufferSubData(materialBufferStruct.getOffset(3) + base, material.getRoughnessFactor())
                        .bufferSubData(materialBufferStruct.getOffset(4) + base, material.getMetallicFactor());

                base = mapBufferStruct.getStride() * index;

                mapBuffer
                        .bufferSubData(mapBufferStruct.getOffset(0) + base, material.getTextureHandle())
                        .bufferSubData(mapBufferStruct.getOffset(1) + base, material.getNormalHandle())
                        .bufferSubData(mapBufferStruct.getOffset(2) + base, material.getRoughnessHandle());

                index++;
            }
        }


        materialBuffer.bindBase(GL_SHADER_STORAGE_BUFFER, 7);
        mapBuffer.bindBase(GL_SHADER_STORAGE_BUFFER, 8);
    }

    public record MapRecord(
            long albedoMaps,
            long normalMaps,
            long pbrMaps
    ) {

    }
}
