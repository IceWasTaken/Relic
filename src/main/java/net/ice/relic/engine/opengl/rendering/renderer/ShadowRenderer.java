package net.ice.relic.engine.opengl.rendering.renderer;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.Shadow;
import net.ice.relic.engine.opengl.*;
import net.ice.relic.engine.opengl.model.Model;
import net.ice.relic.engine.opengl.rendering.buffer.*;
import net.ice.relic.core.scene.SceneObject;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL40.GL_DRAW_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL43.glMultiDrawElementsIndirect;

public class ShadowRenderer extends AbstractRenderer {

    private int animationDrawCount;
    private int staticDrawCount;

    private VertexBufferObject staticVBO;
    private VertexBufferObject animatedVBO;

    private ShadowBuffer shadowBuffer;
    private ArrayList<Shadow> shadows;
    private Map<String, Integer> objectIndexMap;

    public ShadowRenderer(RelicApplication application) {
        super(application);

        this.objectIndexMap = new HashMap<>();
        this.shadows = new ArrayList<>();
    }

    @Override
    public void init(RenderingBuffers renderingBuffer, GeometryBuffer buffer, RefractionBuffer refractionBuffer, ReflectionBuffer reflectionBuffer) {
        super.init(renderingBuffer, buffer, refractionBuffer, reflectionBuffer);
        this.shadowBuffer = new ShadowBuffer();

        for (int i = 0; i < Shadow.SHADOW_MAP_COUNT; i++) {
            shadows.add(new Shadow());
        }
    }

    @Override
    protected void initShaders() {
        loadShader("shadow.vert", Shader.ShaderType.VERTEX);
    }

    @Override
    protected void initUniforms() {
        uniforms.createUniform("projectionMatrix");

        for (int i = 0; i < config.getMaxDrawElements(); i++) {
            uniforms.createUniform(uniforms.formatUniform("drawElements", i) + ".modelMatrixIndex");
        }

        for (int i = 0; i < config.getMaxSceneObjects(); i++) {
            uniforms.createUniform(uniforms.formatUniform("modelMatrices", i));
        }
    }

    @Override
    protected void render() {
        Shadow.update(shadows, application.getCurrentScene());

        shadowBuffer.getShadowMapFBO().bind(GL_FRAMEBUFFER);
        shadowBuffer.shadowViewport();

        shaderProgram.bind();

        int entityIndex = 0;
        for(Model model : application.getCurrentScene().getModels().values()) {
            List<SceneObject> objects = model.getSceneObjects();
            for(SceneObject object : objects) {
                uniforms.setUniform(uniforms.formatUniform("modelMatrices", entityIndex), object.getTransform().getTransformMatrix());
                entityIndex++;
            }
        }

        for (int i = 0; i < Shadow.SHADOW_MAP_COUNT; i++) {
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowBuffer.getShadowMapArrayTexture().getIds()[i], 0);
            glClear(GL_DEPTH_BUFFER_BIT);
        }

        int drawElement = 0;
        for (Model model: application.getCurrentScene().getModels().values()) {
            if (model.isAnimated()) {
                continue;
            }
            List<SceneObject> entities = model.getSceneObjects();
            for (RenderingBuffers.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                for (SceneObject entity : entities) {
                    uniforms.setUniform(uniforms.formatUniform("drawElements", drawElement) + ".modelMatrixIndex", objectIndexMap.get(entity.getName()));
                    drawElement++;
                }
            }
        }
        staticVBO.bind(GL_DRAW_INDIRECT_BUFFER);
        renderingBuffer.getStaticArrayObject().bind();
        for (int i = 0; i < Shadow.SHADOW_MAP_COUNT; i++) {
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowBuffer.getShadowMapArrayTexture().getIds()[i], 0);
            Shadow shadow = shadows.get(i);
            uniforms.setUniform("projectionMatrix", shadow.getProjectionMatrix());
            glMultiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, staticDrawCount, 0);
        }

        drawElement = 0;
        for (Model model: application.getCurrentScene().getModels().values()) {
            if (!model.isAnimated()) {
                continue;
            }
            for (RenderingBuffers.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                RenderingBuffers.AnimMeshDrawData animMeshDrawData = meshDrawData.animMeshDrawData();
                SceneObject entity = animMeshDrawData.entity();
                uniforms.setUniform("drawElements" + drawElement + ".modelMatrixIndex", objectIndexMap.get(entity.getName()));
                drawElement++;
            }
        }
        animatedVBO.bind(GL_DRAW_INDIRECT_BUFFER);
        renderingBuffer.getAnimationArrayObject().bind();
        for (int i = 0; i < Shadow.SHADOW_MAP_COUNT; i++) {
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowBuffer.getShadowMapArrayTexture().getIds()[i], 0);
            Shadow shadow = shadows.get(i);
            uniforms.setUniform("projectionMatrix", shadow.getProjectionMatrix());
            glMultiDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0, animationDrawCount, 0);
        }

        glBindVertexArray(0);
        assertNoError();
    }

    @Override
    protected void setupData() {
        setupEntitiesData();
        setupStaticCommandBuffer();
        setupAnimatedCommandBuffer();
    }

    private void setupEntitiesData() {
        objectIndexMap.clear();
        int entityIdx = 0;
        for (Model model : application.getCurrentScene().getModels().values()) {
            List<SceneObject> entities = model.getSceneObjects();
            for (SceneObject entity : entities) {
                objectIndexMap.put(entity.getName(), entityIdx);
                entityIdx++;
            }
        }
    }

    private void setupStaticCommandBuffer() {
        List<Model> modelList = application.getCurrentScene().getModels().values().stream().filter(m -> !m.isAnimated()).toList();
        int numMeshes = 0;
        for (Model model : application.getCurrentScene().getModels().values()) {
            numMeshes += model.getMeshDrawData().size();
        }

        int firstIndex = 0;
        int baseInstance = 0;
        ByteBuffer commandBuffer = MemoryUtil.memAlloc(numMeshes * config.getCommandSize());
        for (Model model : modelList) {
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

    private void setupAnimatedCommandBuffer() {
        List<Model> modelList = application.getCurrentScene().getModels().values().stream().filter(Model::isAnimated).toList();
        int numMeshes = 0;
        for (Model model : modelList) {
            numMeshes += model.getMeshDrawData().size();
        }

        int firstIndex = 0;
        int baseInstance = 0;
        ByteBuffer commandBuffer = MemoryUtil.memAlloc(numMeshes * config.getCommandSize());
        for (Model model : modelList) {
            for (RenderingBuffers.MeshDrawData meshDrawData : model.getMeshDrawData()) {
                RenderingBuffers.AnimMeshDrawData animMeshDrawData = meshDrawData.animMeshDrawData();
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
        animatedVBO.bufferData(GL_DRAW_INDIRECT_BUFFER, commandBuffer, GL_DYNAMIC_DRAW);

        MemoryUtil.memFree(commandBuffer);
    }

    @Override
    public void cleanup() {
        super.cleanup();

        shadowBuffer.cleanup();
        staticVBO.delete();
        animatedVBO.delete();
    }

    public ArrayList<Shadow> getShadows() {
        return shadows;
    }

    public ShadowBuffer getShadowBuffer() {
        return shadowBuffer;
    }
}
