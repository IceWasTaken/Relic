package net.ice.relic.core.rendering.backend.opengl;

import net.ice.curio.library.opengl.object.VertexArrayObject;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.ecs.component.components.rendering.model.StaticModelComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.Mesh;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.Renderer;
import net.ice.relic.core.rendering.backend.opengl.buffer.InstanceBuffer;
import net.ice.relic.core.rendering.backend.opengl.buffer.MaterialMapBuffer;
import net.ice.relic.core.rendering.backend.opengl.buffer.StaticCommandBuffer;
import net.ice.relic.core.rendering.backend.opengl.framebuffers.GeometryBuffer;
import net.ice.relic.core.rendering.backend.opengl.framebuffers.ShadowBuffer;
import net.ice.relic.core.rendering.backend.opengl.framebuffers.SwapBuffer;
import net.ice.relic.core.rendering.backend.opengl.renderers.*;
import org.joml.Vector2i;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT;
import static org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.opengl.GLUtil.setupDebugMessageCallback;

public class GLRenderer extends Renderer implements Lifecycle {

    public static int COMMAND_SIZE = 5;
    public static final Vector2i SHADOW_MAP_SIZE = new Vector2i(4096);

    private VertexArrayObject staticArrayObject;
    private GLBuffer vertexBuffer;
    private GLBuffer indexBuffer;

    private GeometryBuffer geometryBuffer;
    private ShadowBuffer shadowBuffer;
    private SwapBuffer lightBuffer;
    private SwapBuffer swapBuffer;

    private final InstanceBuffer instanceBuffer;
    private final StaticCommandBuffer staticCommandBuffer;
    private final MaterialMapBuffer materialMapBuffer;

    private final GLSceneRenderer sceneRenderer;
    private final GLShadowRenderer shadowRenderer;
    private final GLLightRenderer lightRenderer;
    private final GLBloomRenderer bloomRenderer;

    private final GLPostRenderer postRenderer;
    private final GLDebugRenderer visualizeRenderer;
    private final GLGuiRenderer guiRenderer;


    @Override
    public void resize(int width, int height) {
        this.geometryBuffer = new GeometryBuffer(width, height);
        this.lightBuffer = new SwapBuffer(width, height);
        this.swapBuffer = new SwapBuffer(width, height);

        this.guiRenderer.onResize(width, height);

        this.sceneRenderer.resize(width, height);
        this.lightRenderer.resize(width, height);
        this.postRenderer.resize(width, height);
        this.bloomRenderer.resize(width, height);
    }

    @Override
    public void setupData() {
        loadStaticModels();

        this.staticCommandBuffer.createStaticCommandBuffer();
    }

    public GLRenderer(RelicApplication relicApplication) {
        super(relicApplication);
        this.instanceBuffer = new InstanceBuffer();
        this.staticCommandBuffer = new StaticCommandBuffer();
        this.materialMapBuffer = new MaterialMapBuffer();

        this.sceneRenderer = new GLSceneRenderer(this);
        this.shadowRenderer = new GLShadowRenderer(this);
        this.lightRenderer = new GLLightRenderer(this);
        this.bloomRenderer = new GLBloomRenderer(this);

        this.postRenderer = new GLPostRenderer(this);

        this.visualizeRenderer = new GLDebugRenderer(this);
        this.guiRenderer = new GLGuiRenderer(this);
    }

    @Override
    public void init() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
        glEnable(GL_FRAMEBUFFER_SRGB);
        setupDebugMessageCallback();
        //SystemInfo.logGLInfo();

        this.geometryBuffer = new GeometryBuffer(application.getWindow().getWidth(), application.getWindow().getHeight());
        this.lightBuffer = new SwapBuffer(application.getWindow().getWidth(), application.getWindow().getHeight());
        this.swapBuffer = new SwapBuffer(application.getWindow().getWidth(), application.getWindow().getHeight());
        this.shadowBuffer = new ShadowBuffer();
        this.instanceBuffer.createInstanceBuffer();
        this.materialMapBuffer.createMaterialMapBuffer();

        sceneRenderer.init();
        shadowRenderer.init();
        lightRenderer.init();
        bloomRenderer.init();

        postRenderer.init();

        visualizeRenderer.init();
        guiRenderer.init();
    }

    @Override
    public void render() {
        updateBuffers();

        glViewport(0, 0, application.getWindow().getWidth(), application.getWindow().getHeight());

        sceneRenderer.render();
        shadowRenderer.render();

        //lightBuffer.bind();
        lightRenderer.render();
        //lightBuffer.unbind();

        //bloomRenderer.render();

        //postRenderer.render();

        glViewport(0, 0, application.getWindow().getWidth(), application.getWindow().getHeight());

        visualizeRenderer.render();
        guiRenderer.render();

        syncBuffers();
    }

    private void syncBuffers() {
        instanceBuffer.sync();
        staticCommandBuffer.sync();
        materialMapBuffer.sync();
    }

    private void updateBuffers() {
        instanceBuffer.updateInstanceBuffer(this);
        staticCommandBuffer.updateStaticCommandBuffer(this);
        materialMapBuffer.updateMaterialMapBuffer(this);
    }


    public void loadStaticModels() {
        List<Model> modelList = StaticModelComponent.getAllModels();
        staticArrayObject = new VertexArrayObject();
        staticArrayObject.bind();
        int positionsSize = 0;
        int normalsSize = 0;
        int textureCoordsSize = 0;
        int indicesSize = 0;
        int offset = 0;
        for (Model model : modelList) {
            List<MeshDrawData> meshDrawDataList = model.getMeshDrawData();
            for (Mesh meshData : model.getMeshData()) {
                positionsSize += meshData.getVertices().length;
                normalsSize += meshData.getNormals().length;
                textureCoordsSize += meshData.getTextureCoords().length;
                indicesSize += meshData.getIndices().length;

                int meshSizeInBytes = meshData.getVertices().length * 14 * 4;
                meshDrawDataList.add(new MeshDrawData(meshSizeInBytes, meshData.getMaterialIndex(), offset, meshData.getIndices().length));
                offset = positionsSize / 3;
            }
        }

        this.vertexBuffer = new GLBuffer((positionsSize + normalsSize * 3L + textureCoordsSize) * 4, GL_MAP_WRITE_BIT);

        for (Model model : modelList) {
            for (Mesh meshData : model.getMeshData()) {
                populateMeshBuffer(vertexBuffer, meshData);
            }
        }

        setupVAOAttributes(vertexBuffer);

        this.indexBuffer = new GLBuffer(indicesSize * 4L, GL_MAP_WRITE_BIT);

        for (Model model : modelList) {
            for (Mesh meshData : model.getMeshData()) {
                indexBuffer.putInt(meshData.getIndices());
            }
        }

        staticArrayObject.elementBuffer(indexBuffer);

        this.vertexBuffer.unmap();
        this.indexBuffer.unmap();

        glBindVertexArray(0);
    }

    private void setupVAOAttributes(GLBuffer vertexBuffer) {
        staticArrayObject.vertexBuffer(0, vertexBuffer, 0, 56);

        staticArrayObject.attributeFormat(0, 3, GL_FLOAT, false, 0);
        staticArrayObject.attributeFormat(1, 3, GL_FLOAT, false, 12);
        staticArrayObject.attributeFormat(2, 3, GL_FLOAT, false, 24);
        staticArrayObject.attributeFormat(3, 3, GL_FLOAT, false, 36);
        staticArrayObject.attributeFormat(4, 2, GL_FLOAT, false, 48);

        staticArrayObject.attributeBinding(0, 0);
        staticArrayObject.attributeBinding(1, 0);
        staticArrayObject.attributeBinding(2, 0);
        staticArrayObject.attributeBinding(3, 0);
        staticArrayObject.attributeBinding(4, 0);

        staticArrayObject.enableAttribute(0);
        staticArrayObject.enableAttribute(1);
        staticArrayObject.enableAttribute(2);
        staticArrayObject.enableAttribute(3);
        staticArrayObject.enableAttribute(4);
    }

    private void populateMeshBuffer(GLBuffer meshesBuffer, Mesh meshData) {
        float[] positions = meshData.getVertices();
        float[] normals = meshData.getNormals();
        float[] tangents = meshData.getTangents();
        float[] bitangents = meshData.getBitangents();
        float[] textCoords = meshData.getTextureCoords();

        int rows = positions.length / 3;
        for (int row = 0; row < rows; row++) {
            int startPos = row * 3;
            int startTextCoord = row * 2;
            meshesBuffer.putFloat(positions[startPos]);
            meshesBuffer.putFloat(positions[startPos + 1]);
            meshesBuffer.putFloat(positions[startPos + 2]);
            meshesBuffer.putFloat(normals[startPos]);
            meshesBuffer.putFloat(normals[startPos + 1]);
            meshesBuffer.putFloat(normals[startPos + 2]);
            meshesBuffer.putFloat(tangents[startPos]);
            meshesBuffer.putFloat(tangents[startPos + 1]);
            meshesBuffer.putFloat(tangents[startPos + 2]);
            meshesBuffer.putFloat(bitangents[startPos]);
            meshesBuffer.putFloat(bitangents[startPos + 1]);
            meshesBuffer.putFloat(bitangents[startPos + 2]);
            meshesBuffer.putFloat(textCoords[startTextCoord]);
            meshesBuffer.putFloat(textCoords[startTextCoord + 1]);
        }
    }

    public GeometryBuffer getGeometryBuffer() {
        return geometryBuffer;
    }

    public ShadowBuffer getShadowBuffer() {
        return shadowBuffer;
    }

    public SwapBuffer getSwapBuffer() {
        return swapBuffer;
    }

    public SwapBuffer getLightBuffer() {
        return lightBuffer;
    }

    public VertexArrayObject getStaticArrayObject() {
        return staticArrayObject;
    }

    public RelicApplication getApplication() {
        return application;
    }

    public GLShadowRenderer getShadowRenderer() {
        return shadowRenderer;
    }

    public GLPostRenderer getPostRenderer() {
        return postRenderer;
    }

    public StaticCommandBuffer getStaticCommandBuffer() {
        return staticCommandBuffer;
    }

    public record AnimMeshDrawData(Entity entity, int bindingPoseOffset, int weightsOffset) { }

    public record MeshDrawData(int sizeInBytes, int materialIdx, int offset, int count, AnimMeshDrawData animMeshDrawData) {
        public MeshDrawData(int sizeInBytes, int materialIdx, int offset, int vertices) {
            this(sizeInBytes, materialIdx, offset, vertices, null);
        }
    }
}
