package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.enums.BufferAccess;
import net.ice.curio.graphics.enums.BufferFlags;
import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;
import net.ice.curio.graphics.object.Viewport;
import net.ice.curio.library.opengl.object.GLViewport;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.curio.library.opengl.wrapper.enums.Usage;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.cache.MaterialCache;
import net.ice.relic.core.ecs.component.components.rendering.model.StaticModelComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.Material;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.Uniforms;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;
import net.ice.relic.core.rendering.backend.opengl.struct.DrawElementsIndirectCommand;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL30.GL_MAP_WRITE_BIT;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL40.GL_DRAW_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL43.glMultiDrawElementsIndirect;
import static org.lwjgl.opengl.GL44.*;

public class GLSceneRenderer implements Lifecycle {

    private GLBuffer staticCommandBuffer;
    private DrawElementsIndirectCommand commandBufferStruct;

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

        staticCommandBuffer.bind(GL_DRAW_INDIRECT_BUFFER);
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
        for (Model model : StaticModelComponent.getAllModels()) {
            for (Entity object : model.getSceneObjects()) {
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
        List<Model> models = StaticModelComponent.getAllModels();

        int meshCount = 0;
        int firstIndex = 0;
        int baseInstance = 0;

        for (Model model : models) {
            meshCount += model.getMeshDrawData().size();
        }

        this.commandBufferStruct = new DrawElementsIndirectCommand();
        this.staticCommandBuffer = new GLBuffer((long) meshCount * commandBufferStruct.getStride(), GL_MAP_WRITE_BIT);
        this.staticDrawCount = staticCommandBuffer.remaining() / 20;

        for (Model model : models) {
            List<Entity> entities = model.getSceneObjects();
            int numEntities = entities.size();
            for (GLRenderer.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                // count
                staticCommandBuffer.putInt(meshDrawData.vertexCount());

                // instanceCount
                staticCommandBuffer.putInt(numEntities);
                staticCommandBuffer.putInt(firstIndex);
                // baseVertex
                staticCommandBuffer.putInt(meshDrawData.offset());
                staticCommandBuffer.putInt(baseInstance);

                firstIndex += meshDrawData.vertexCount();
                baseInstance += entities.size();
            }
        }
        staticCommandBuffer.unmap();
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

        this.materialBuffer = new GLBuffer(44 * 200, GL_MAP_WRITE_BIT);
        this.mapBuffer = new GLBuffer(8 * 3 * 200, GL_MAP_WRITE_BIT);

        int index = 0;
        for (Material material : materialList) {
            int base = materialBufferStruct.getStride() * index;

            materialBuffer.putFloat(materialBufferStruct.getOffset(0) + base, material.getDiffuseColor().div().vec4f().x);
            materialBuffer.putFloat(materialBufferStruct.getOffset(0) + base + 4, material.getDiffuseColor().div().vec4f().y);
            materialBuffer.putFloat(materialBufferStruct.getOffset(0) + base + 8, material.getDiffuseColor().div().vec4f().z);
            materialBuffer.putFloat(materialBufferStruct.getOffset(0) + base + 12, material.getDiffuseColor().div().vec4f().w);

            materialBuffer.putFloat(materialBufferStruct.getOffset(1) + base, material.getSpecularColor().div().vec4f().x);
            materialBuffer.putFloat(materialBufferStruct.getOffset(1) + base + 4, material.getSpecularColor().div().vec4f().y);
            materialBuffer.putFloat(materialBufferStruct.getOffset(1) + base + 8, material.getSpecularColor().div().vec4f().z);
            materialBuffer.putFloat(materialBufferStruct.getOffset(1) + base + 12, material.getSpecularColor().div().vec4f().w);

            materialBuffer.putFloat(materialBufferStruct.getOffset(2) + base, material.getReflectance());
            materialBuffer.putFloat(materialBufferStruct.getOffset(3) + base, material.getRoughnessFactor());
            materialBuffer.putFloat(materialBufferStruct.getOffset(4) + base, material.getMetallicFactor());

            base = mapBufferStruct.getStride() * index;

            mapBuffer.putLong(mapBufferStruct.getOffset(0) + base, material.getTextureHandle());
            mapBuffer.putLong(mapBufferStruct.getOffset(1) + base, material.getNormalHandle());
            mapBuffer.putLong(mapBufferStruct.getOffset(2) + base, material.getRoughnessHandle());

            index++;
        }



        materialBuffer.bindBase(GL_SHADER_STORAGE_BUFFER, 7);
        mapBuffer.bindBase(GL_SHADER_STORAGE_BUFFER, 8);

        materialBuffer.unmap();
        mapBuffer.unmap();
    }

    public record MapRecord(
            long albedoMaps,
            long normalMaps,
            long pbrMaps
    ) {

    }
}
