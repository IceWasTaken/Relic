package net.ice.relic.core.rendering.backend.opengl;

import net.ice.curio.library.opengl.object.VertexArrayObject;
import net.ice.curio.library.opengl.object.buffer.GLBuffer;
import net.ice.curio.library.opengl.object.buffer.IndexBufferObject;
import net.ice.curio.library.opengl.object.buffer.VertexBufferObject;
import net.ice.curio.library.opengl.object.framebuffer.FramebufferObject;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.model.mesh.MeshData;
import net.ice.relic.core.rendering.backend.Renderer;
import net.ice.relic.core.rendering.backend.opengl.depricated.buffer.ShadowBuffer;
import net.ice.relic.core.rendering.backend.opengl.depricated.model.Model;
import net.ice.relic.core.rendering.backend.opengl.renderers.*;
import net.ice.curio.system.SystemInfo;
import net.ice.relic.core.scene.SceneObject;
import org.joml.Vector2i;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT;
import static org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS;

public class GLRenderer extends Renderer implements Lifecycle {

    public static int COMMAND_SIZE = 5;
    public static final Vector2i SHADOW_MAP_SIZE = new Vector2i(4096);

    private VertexArrayObject staticArrayObject;

    private final List<GLBuffer> vertexBufferObjects;

    private FramebufferObject geometryBuffer;
    private ShadowBuffer shadowBuffer;

    private final GLSceneRenderer sceneRenderer;
    private final GLLightRenderer lightRenderer;
    private final GLShadowRenderer shadowRenderer;
    private final GLGuiRenderer guiRenderer;
    private final GLDebugRenderer visualizeRenderer;

    @Override
    public void resize(int width, int height) {
        this.geometryBuffer = new FramebufferObject(new Vector2i(width, height), 5);
        this.guiRenderer.onResize(width, height);
        this.sceneRenderer.resize(width, height);
        this.lightRenderer.resize(width, height);
    }

    @Override
    public void setupData() {
        loadStaticModels();

        sceneRenderer.setupBuffers();
        shadowRenderer.setupBuffers();
        guiRenderer.setupBuffers();
    }

    public GLRenderer(RelicApplication relicApplication) {
        super(relicApplication);
        this.sceneRenderer = new GLSceneRenderer(this);
        this.lightRenderer = new GLLightRenderer(this);
        this.shadowRenderer = new GLShadowRenderer(this);
        this.guiRenderer = new GLGuiRenderer(this);
        this.visualizeRenderer = new GLDebugRenderer(this);

        this.vertexBufferObjects = new ArrayList<>();
    }

    @Override
    public void init() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
        glEnable(GL_FRAMEBUFFER_SRGB);
        //setupDebugMessageCallback();
        SystemInfo.logGLInfo();

        this.geometryBuffer = new FramebufferObject(new Vector2i(application.getWindow().getWidth(), application.getWindow().getHeight()), 5);
        this.shadowBuffer = new ShadowBuffer();
        shadowRenderer.init();
        sceneRenderer.init();
        lightRenderer.init();
        guiRenderer.init();
        visualizeRenderer.init();
    }

    @Override
    public void render() {
        glViewport(0, 0, application.getWindow().getWidth(), application.getWindow().getHeight());
        sceneRenderer.render();
        shadowRenderer.render();
        lightRenderer.render();
        guiRenderer.render();
        visualizeRenderer.render();
    }


    public void loadStaticModels() {
        List<Model> modelList = application.getCurrentScene().getModels().values().stream().filter(m -> !m.isAnimated()).toList();
        staticArrayObject = new VertexArrayObject();
        staticArrayObject.bind();
        int positionsSize = 0;
        int normalsSize = 0;
        int textureCoordsSize = 0;
        int indicesSize = 0;
        int offset = 0;
        for (Model model : modelList) {
            List<MeshDrawData> meshDrawDataList = model.getMeshDrawData();
            for (MeshData meshData : model.getMeshData()) {
                positionsSize += meshData.getVertices().length;
                normalsSize += meshData.getNormals().length;
                textureCoordsSize += meshData.getTextureCoords().length;
                indicesSize += meshData.getIndices().length;

                int meshSizeInBytes = meshData.getVertices().length * 14 * 4;
                meshDrawDataList.add(new MeshDrawData(meshSizeInBytes, meshData.getMaterialIndex(), offset,
                        meshData.getIndices().length));
                offset = positionsSize / 3;
            }
        }

        GLBuffer vboId = new VertexBufferObject();
        vertexBufferObjects.add(vboId);
        FloatBuffer meshesBuffer = MemoryUtil.memAllocFloat(positionsSize + normalsSize * 3 + textureCoordsSize);
        for (Model model : modelList) {
            for (MeshData meshData : model.getMeshData()) {
                populateMeshBuffer(meshesBuffer, meshData);
            }
        }
        meshesBuffer.flip();
        vboId.bind();
        glBufferData(GL_ARRAY_BUFFER, meshesBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(meshesBuffer);

        defineVertexAttributes();

        vboId = new IndexBufferObject();
        vertexBufferObjects.add(vboId);
        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indicesSize);
        for (Model model : modelList) {
            for (MeshData meshData : model.getMeshData()) {
                indicesBuffer.put(meshData.getIndices());
            }
        }
        indicesBuffer.flip();
        vboId.bind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(indicesBuffer);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    private void populateMeshBuffer(FloatBuffer meshesBuffer, MeshData meshData) {
        float[] positions = meshData.getVertices();
        float[] normals = meshData.getNormals();
        float[] tangents = meshData.getTangents();
        float[] bitangents = meshData.getBitangents();
        float[] textCoords = meshData.getTextureCoords();

        int rows = positions.length / 3;
        for (int row = 0; row < rows; row++) {
            int startPos = row * 3;
            int startTextCoord = row * 2;
            meshesBuffer.put(positions[startPos]);
            meshesBuffer.put(positions[startPos + 1]);
            meshesBuffer.put(positions[startPos + 2]);
            meshesBuffer.put(normals[startPos]);
            meshesBuffer.put(normals[startPos + 1]);
            meshesBuffer.put(normals[startPos + 2]);
            meshesBuffer.put(tangents[startPos]);
            meshesBuffer.put(tangents[startPos + 1]);
            meshesBuffer.put(tangents[startPos + 2]);
            meshesBuffer.put(bitangents[startPos]);
            meshesBuffer.put(bitangents[startPos + 1]);
            meshesBuffer.put(bitangents[startPos + 2]);
            meshesBuffer.put(textCoords[startTextCoord]);
            meshesBuffer.put(textCoords[startTextCoord + 1]);
        }
    }

    private void defineVertexAttributes() {
        int stride = 3 * 4 * 4 + 2 * 4;
        int pointer = 0;

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, pointer);
        pointer += 3 * 4;

        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, pointer);
        pointer += 3 * 4;

        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, stride, pointer);
        pointer += 3 * 4;

        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, stride, pointer);
        pointer += 3 * 4;

        glEnableVertexAttribArray(4);
        glVertexAttribPointer(4, 2, GL_FLOAT, false, stride, pointer);
    }


    public FramebufferObject getGeometryBuffer() {
        return geometryBuffer;
    }

    public ShadowBuffer getShadowBuffer() {
        return shadowBuffer;
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

    public record AnimMeshDrawData(SceneObject entity, int bindingPoseOffset, int weightsOffset) { }

    public record MeshDrawData(int sizeInBytes, int materialIdx, int offset, int vertices, AnimMeshDrawData animMeshDrawData) {
        public MeshDrawData(int sizeInBytes, int materialIdx, int offset, int vertices) {
            this(sizeInBytes, materialIdx, offset, vertices, null);
        }
    }
}
