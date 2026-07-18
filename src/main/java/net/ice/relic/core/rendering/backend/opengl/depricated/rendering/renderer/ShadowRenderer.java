package net.ice.relic.core.rendering.backend.opengl.depricated.rendering.renderer;

import net.ice.curio.config.RendererConfig;
import net.ice.relic.core.Shadows;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.depricated.AbstractGLRenderer;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLManager;
import net.ice.relic.core.rendering.pipeline.Pipeline;
import net.ice.relic.core.rendering.backend.opengl.depricated.model.Model;
import net.ice.relic.core.rendering.backend.opengl.depricated.rendering.buffer.ShadowBuffer;
import net.ice.relic.core.rendering.shader.ShaderType;
import net.ice.relic.core.scene.SceneObject;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

@Deprecated
public class ShadowRenderer extends AbstractGLRenderer {

    private int animationDrawCount;
    private int staticDrawCount;

//    private VertexBufferObject staticVBO;
//    private VertexBufferObject animatedVBO;

    private ShadowBuffer shadowBuffer;
    private Shadows shadows;
    private Map<String, Integer> objectIndexMap;

    private Pipeline pipeline;

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

    }

    @Override
    public void render() {
        pipeline.reset();
        pipeline.execute();

        int entityIndex = 0;
        for (Model model : manager.getApplication().getCurrentScene().getModels().values()) {
            for (SceneObject object : model.getSceneObjects()) {
                pipeline.setUniform(uniforms.formatUniform("modelMatrices", entityIndex), object.getTransform().getTransformMatrix());
                entityIndex++;
            }
        }

        int drawElement = 0;
        for (Model model : manager.getApplication().getCurrentScene().getModels().values()) {
            if (model.isAnimated()) continue;
            for (GLRenderer.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                for (SceneObject object : model.getSceneObjects()) {
                    String name = uniforms.formatUniform("drawElements", drawElement);
                    pipeline.setUniform(name + ".modelMatrixIndex", objectIndexMap.get(object.getName()));
                    pipeline.setUniform(name + ".materialIndex", meshDrawData.materialIdx());
                    drawElement++;
                }
            }
        }

        for (int i = 0; i < Shadows.SHADOW_MAP_COUNT; i++) {
            Matrix4f shadowData = shadows.getShadowData().get(i).getProjViewMatrix();
            pipeline.setUniform("projViewMatrices[" + i + "]", shadowData);
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

    @Override
    public void setupData() {
        setupEntitiesData();
        setupStaticCommandBuffer();
        setupAnimatedCommandBuffer();
        setupPipeline();
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
        ByteBuffer commandBuffer = MemoryUtil.memAlloc(numMeshes * RendererConfig.getCommandSize());
        for (Model model : modelList) {
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

        staticDrawCount = commandBuffer.remaining() / RendererConfig.getCommandSize();

//        staticVBO = new VertexBufferObject();
//        staticVBO.bind(GL_DRAW_INDIRECT_BUFFER);
//        staticVBO.bufferData(GL_DRAW_INDIRECT_BUFFER, commandBuffer, DrawType.DYNAMIC);

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
        ByteBuffer commandBuffer = MemoryUtil.memAlloc(numMeshes * RendererConfig.getCommandSize());
        for (Model model : modelList) {
            for (GLRenderer.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                GLRenderer.AnimMeshDrawData animMeshDrawData = meshDrawData.animMeshDrawData();
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

        animationDrawCount = commandBuffer.remaining() / RendererConfig.getCommandSize();

//        animatedVBO = new VertexBufferObject();
//        animatedVBO.bind(GL_DRAW_INDIRECT_BUFFER);
//        animatedVBO.bufferData(GL_DRAW_INDIRECT_BUFFER, commandBuffer, DrawType.DYNAMIC);

        MemoryUtil.memFree(commandBuffer);
    }

    @Override
    public void cleanup() {
        super.cleanup();

//        shadowBuffer.cleanup();
//        staticVBO.delete();
//        animatedVBO.delete();
    }

    public Shadows getShadows() {
        return shadows;
    }

    public ShadowBuffer getShadowBuffer() {
        return shadowBuffer;
    }

    private void setupPipeline() {
        Pipeline.PipelineBuilder pipelineBuilder = new Pipeline.PipelineBuilder()
                .customCommand("updateShadows", (v) -> shadows.update(manager.getApplication().getCurrentScene()))
                //.bindFramebuffer(shadowBuffer.getShadowMapFBO())
                .customCommand("shadowViewport", (v) -> shadowBuffer.shadowViewport())
                .bindShaderProgram(shaderProgram);

        for (int i = 0; i < RendererConfig.getMaxDrawElements(); i++) {
            pipelineBuilder.uniform("drawElements[" + i + "]" + ".modelMatrixIndex", shaderProgram);
            pipelineBuilder.uniform("drawElements[" + i + "]" + ".materialIndex", shaderProgram);
        }
        for (int i = 0; i < RendererConfig.getMaxSceneObjects(); i++) {
            pipelineBuilder.uniform("modelMatrices[" + i + "]", shaderProgram);
        }

        for (int i = 0; i < 3; i++) {
            pipelineBuilder.uniform("projViewMatrices[" + i + "]", shaderProgram);
        }

        pipelineBuilder
                .pauseHere()

                //.bindVertexBufferObject(staticVBO)
                //.bindVertexArrayObject(manager.getStaticArrayObject())
                .multiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, staticDrawCount, 0)

                .pauseHere()

                //.bindVertexBufferObject(animatedVBO, BufferTarget.DRAW_INDIRECT)
                //.bindVertexArrayObject(manager.getAnimationArrayObject())
                .multiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, animationDrawCount, 0)

                .unbindVertexArrayObject()
                .unbindShaderProgram(shaderProgram);

        this.pipeline = pipelineBuilder.build();

    }
}
