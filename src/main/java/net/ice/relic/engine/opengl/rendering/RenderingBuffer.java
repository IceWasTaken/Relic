package net.ice.relic.engine.opengl.rendering;

import net.ice.relic.engine.RelicApplication;
import net.ice.relic.engine.opengl.VertexArrayObject;
import net.ice.relic.engine.opengl.VertexBufferObject;
import net.ice.relic.engine.opengl.model.Animation;
import net.ice.relic.engine.opengl.model.MeshData;
import net.ice.relic.engine.opengl.model.Model;
import net.ice.relic.engine.opengl.scene.SceneObject;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

public class RenderingBuffer {

    private VertexBufferObject bindingPoseBuffer;
    private VertexBufferObject bonesIndicesWeightsBuffer;
    private VertexBufferObject bonesMatricesBuffer;
    private VertexBufferObject destinationAnimationBuffer;

    private VertexArrayObject animationArrayObject;
    private VertexArrayObject staticArrayObject;

    private final List<VertexBufferObject> VBOs;
    private final RelicApplication application;

    public RenderingBuffer(RelicApplication application) {
        this.application = application;

        VBOs = new ArrayList<>();
    }

    public void cleanup() {
        VBOs.forEach(VertexBufferObject::delete);
        animationArrayObject.delete();
        staticArrayObject.delete();
    }

    private void defineVertexAttributes() {
        int stride = 3 * 4 * 4 + 2 * 4;
        int pointer = 0;
        // Positions
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, pointer);
        pointer += 3 * 4;
        // Normals
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, pointer);
        pointer += 3 * 4;
        // Tangents
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, stride, pointer);
        pointer += 3 * 4;
        // Bitangents
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, stride, pointer);
        pointer += 3 * 4;
        // Texture coordinates
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(4, 2, GL_FLOAT, false, stride, pointer);
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
            List<RenderingBuffer.MeshDrawData> meshDrawDataList = model.getMeshDrawData();
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

        VertexBufferObject vboId = new VertexBufferObject();
        VBOs.add(vboId);
        FloatBuffer meshesBuffer = MemoryUtil.memAllocFloat(positionsSize + normalsSize * 3 + textureCoordsSize);
        for (Model model : modelList) {
            for (MeshData meshData : model.getMeshData()) {
                populateMeshBuffer(meshesBuffer, meshData);
            }
        }
        meshesBuffer.flip();
        vboId.bind(GL_ARRAY_BUFFER);
        glBufferData(GL_ARRAY_BUFFER, meshesBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(meshesBuffer);

        defineVertexAttributes();

        // Index VBO
        vboId = new VertexBufferObject();
        VBOs.add(vboId);
        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indicesSize);
        for (Model model : modelList) {
            for (MeshData meshData : model.getMeshData()) {
                indicesBuffer.put(meshData.getIndices());
            }
        }
        indicesBuffer.flip();
        vboId.bind(GL_ELEMENT_ARRAY_BUFFER);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(indicesBuffer);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void loadAnimatedModels() {
        List<Model> models = application.getCurrentScene().getModels().values().stream().filter(Model::isAnimated).toList();
        loadBindingPoses(models);
        loadBonesMatricesBuffer(models);
        loadBonesIndicesWeights(models);

        animationArrayObject = new VertexArrayObject();
        animationArrayObject.bind();

        int positionsSize = 0;
        int normalsSize = 0;
        int textureCoordsSize = 0;
        int indicesSize = 0;
        int offset = 0;
        int chunkBindingPoseOffset = 0;
        int bindingPoseOffset = 0;
        int chunkWeightsOffset = 0;
        int weightsOffset = 0;

        for(Model model : models) {
            List<SceneObject> entities = model.getSceneObjects();
            for(SceneObject entity : entities) {
                List<MeshDrawData> meshDrawData = model.getMeshDrawData();
                bindingPoseOffset = chunkBindingPoseOffset;
                weightsOffset = chunkWeightsOffset;
                for (MeshData meshData : model.getMeshData()) {
                    positionsSize += meshData.getVertices().length;
                    normalsSize += meshData.getNormals().length;
                    textureCoordsSize += meshData.getTextureCoords().length;
                    indicesSize += meshData.getIndices().length;

                    int meshSizeInBytes = (meshData.getVertices().length + meshData.getNormals().length * 3 + meshData.getTextureCoords().length) * 4;
                    meshDrawData.add(new MeshDrawData(meshSizeInBytes, meshData.getMaterialIndex(), offset,
                            meshData.getIndices().length, new AnimMeshDrawData(entity, bindingPoseOffset, weightsOffset)));
                    bindingPoseOffset += meshSizeInBytes / 4;
                    int groupSize = (int) Math.ceil((float) meshSizeInBytes / (14 * 4));
                    weightsOffset += groupSize * 2 * 4;
                    offset = positionsSize / 3;
                }
            }
            chunkBindingPoseOffset += bindingPoseOffset;
            chunkWeightsOffset += weightsOffset;
        }

        destinationAnimationBuffer = new VertexBufferObject();
        VBOs.add(destinationAnimationBuffer);
        FloatBuffer meshBuffer = MemoryUtil.memAllocFloat(positionsSize + normalsSize * 3 + textureCoordsSize);
        for (Model model : models) {
            model.getMeshDrawData().forEach(meshDrawData -> {
                for(MeshData meshData : model.getMeshData()) {
                    populateMeshBuffer(meshBuffer, meshData);
                }
            });
        }
        meshBuffer.flip();
        destinationAnimationBuffer.bind(GL_ARRAY_BUFFER);
        destinationAnimationBuffer.bufferDataFloat(GL_ARRAY_BUFFER, meshBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(meshBuffer);

        defineVertexAttributes();

        VertexBufferObject vertexBufferObject = new VertexBufferObject();
        VBOs.add(vertexBufferObject);
        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indicesSize);
        for (Model model : models) {
            model.getSceneObjects().forEach(e -> {
                for (MeshData meshData : model.getMeshData()) {
                    indicesBuffer.put(meshData.getIndices());
                }
            });
        }
        indicesBuffer.flip();
        vertexBufferObject.bind(GL_ELEMENT_ARRAY_BUFFER);
        vertexBufferObject.bufferDataInt(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(indicesBuffer);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    private void loadBindingPoses(List<Model> models) {
        int meshSize = 0;
        for (Model model : models) {
            for (MeshData meshData : model.getMeshData()) {
                meshSize += meshData.getVertices().length + meshData.getNormals().length * 3 +
                        meshData.getTextureCoords().length + meshData.getIndices().length;
            }
        }

        bindingPoseBuffer = new VertexBufferObject();
        VBOs.add(bindingPoseBuffer);
        FloatBuffer meshesBuffer = MemoryUtil.memAllocFloat(meshSize);
        for (Model model : models) {
            for (MeshData meshData : model.getMeshData()) {
                populateMeshBuffer(meshesBuffer, meshData);
            }
        }
        meshesBuffer.flip();
        bindingPoseBuffer.bind(GL_SHADER_STORAGE_BUFFER);
        bindingPoseBuffer.bufferDataFloat(GL_SHADER_STORAGE_BUFFER, meshesBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(meshesBuffer);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void loadBonesMatricesBuffer(List<Model> models) {
        int bufferSize = 0;
        for (Model model : models) {
            List<Animation> animationsList = model.getAnimations();
            for (Animation animation : animationsList) {
                List<Animation.AnimatedFrame> frameList = animation.frames();
                for (Animation.AnimatedFrame frame : frameList) {
                    Matrix4f[] matrices = frame.getBonesMatrices();
                    bufferSize += matrices.length * 64;
                }
            }
        }

        bonesMatricesBuffer = new VertexBufferObject();
        VBOs.add(bonesMatricesBuffer);
        ByteBuffer dataBuffer = MemoryUtil.memAlloc(bufferSize);
        int matrixSize = 4 * 4 * 4;
        for (Model model : models) {
            List<Animation> animationsList = model.getAnimations();
            for (Animation animation : animationsList) {
                List<Animation.AnimatedFrame> frameList = animation.frames();
                for (Animation.AnimatedFrame frame : frameList) {
                    frame.setOffset(dataBuffer.position() / matrixSize);
                    Matrix4f[] matrices = frame.getBonesMatrices();
                    for (Matrix4f matrix : matrices) {
                        matrix.get(dataBuffer);
                        dataBuffer.position(dataBuffer.position() + matrixSize);
                    }
                    frame.clear();
                }
            }
        }
        dataBuffer.flip();
        bonesMatricesBuffer.bind(GL_SHADER_STORAGE_BUFFER);
        bonesMatricesBuffer.bufferData(GL_SHADER_STORAGE_BUFFER, dataBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(dataBuffer);
    }

    private void loadBonesIndicesWeights(List<Model> models) {
        int bufferSize = 0;
        for (Model model : models) {
            for (MeshData meshData : model.getMeshData()) {
                bufferSize += meshData.getBoneIndices().length * 4 + meshData.getWeights().length * 4;
            }
        }
        ByteBuffer dataBuffer = MemoryUtil.memAlloc(bufferSize);
        for (Model model : models) {
            for (MeshData meshData : model.getMeshData()) {
                int[] bonesIndices = meshData.getBoneIndices();
                float[] weights = meshData.getWeights();
                int rows = bonesIndices.length / 4;
                for (int row = 0; row < rows; row++) {
                    int startPos = row * 4;
                    dataBuffer.putFloat(weights[startPos]);
                    dataBuffer.putFloat(weights[startPos + 1]);
                    dataBuffer.putFloat(weights[startPos + 2]);
                    dataBuffer.putFloat(weights[startPos + 3]);
                    dataBuffer.putFloat(bonesIndices[startPos]);
                    dataBuffer.putFloat(bonesIndices[startPos + 1]);
                    dataBuffer.putFloat(bonesIndices[startPos + 2]);
                    dataBuffer.putFloat(bonesIndices[startPos + 3]);
                }
            }
        }
        dataBuffer.flip();

        bonesIndicesWeightsBuffer = new VertexBufferObject();
        VBOs.add(bonesIndicesWeightsBuffer);
        bonesIndicesWeightsBuffer.bind(GL_SHADER_STORAGE_BUFFER);
        bonesIndicesWeightsBuffer.bufferData(GL_SHADER_STORAGE_BUFFER, dataBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(dataBuffer);

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
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

    public VertexArrayObject getAnimationArrayObject() {
        return animationArrayObject;
    }

    public VertexArrayObject getStaticArrayObject() {
        return staticArrayObject;
    }

    public VertexBufferObject getBindingPoseBuffer() {
        return bindingPoseBuffer;
    }

    public VertexBufferObject getBonesIndicesWeightsBuffer() {
        return bonesIndicesWeightsBuffer;
    }

    public VertexBufferObject getBonesMatricesBuffer() {
        return bonesMatricesBuffer;
    }

    public VertexBufferObject getDestinationAnimationBuffer() {
        return destinationAnimationBuffer;
    }

    public record AnimMeshDrawData(SceneObject entity, int bindingPoseOffset, int weightsOffset) {
    }

    public record MeshDrawData(int sizeInBytes, int materialIdx, int offset, int vertices, AnimMeshDrawData animMeshDrawData) {
        public MeshDrawData(int sizeInBytes, int materialIdx, int offset, int vertices) {
            this(sizeInBytes, materialIdx, offset, vertices, null);
        }
    }


}
