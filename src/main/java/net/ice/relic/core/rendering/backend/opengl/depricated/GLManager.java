package net.ice.relic.core.rendering.backend.opengl.depricated;

import net.ice.heirloom.Lifecycle;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.model.mesh.MeshData;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.opengl.depricated.model.Animation;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GLManager implements Lifecycle {

    //private VertexArrayObject staticArrayObject;
    //private VertexArrayObject animationArrayObject;

//    private VertexBufferObject bindingPoseBuffer;
//    private VertexBufferObject bonesMatricesBuffer;
//    private VertexBufferObject bonesIndicesWeightsBuffer;
//    private VertexBufferObject destinationAnimationBuffer;


    public void loadAnimatedModels() {
        //List<Model> models = application.getCurrentScene().getModels().values().stream().filter(Model::isAnimated).toList();
        List<Model> models = new ArrayList<>();
        loadBindingPoses(models);
        loadBonesMatricesBuffer(models);
        loadBonesIndicesWeights(models);

        //animationArrayObject = new VertexArrayObject();
        //animationArrayObject.bind();

        int positionsSize = 0;
        int normalsSize = 0;
        int textureCoordsSize = 0;
        int indicesSize = 0;
        int offset = 0;
        int chunkBindingPoseOffset = 0;
        int bindingPoseOffset = 0;
        int chunkWeightsOffset = 0;
        int weightsOffset = 0;

//        for(Model model : models) {
//            List<Entity> entities = model.getEntities();
//            for(Entity entity : entities) {
//                List<GLRenderer.MeshDrawData> meshDrawData = model.getMeshDrawData();
//                bindingPoseOffset = chunkBindingPoseOffset;
//                weightsOffset = chunkWeightsOffset;
//                for (Mesh meshData : model.getMeshes()) {
//                    positionsSize += meshData.getVertices().length;
//                    normalsSize += meshData.getNormals().length;
//                    textureCoordsSize += meshData.getTextureCoords().length;
//                    indicesSize += meshData.getIndices().length;
//
//                    int meshSizeInBytes = (meshData.getVertices().length + meshData.getNormals().length * 3 + meshData.getTextureCoords().length) * 4;
//                    meshDrawData.add(new GLRenderer.MeshDrawData(meshSizeInBytes, meshData.getMaterialIndex(), offset,
//                            meshData.getIndices().length, new GLRenderer.AnimMeshDrawData(entity, bindingPoseOffset, weightsOffset)));
//                    bindingPoseOffset += meshSizeInBytes / 4;
//                    int groupSize = (int) Math.ceil((float) meshSizeInBytes / (14 * 4));
//                    weightsOffset += groupSize * 2 * 4;
//                    offset = positionsSize / 3;
//                }
//            }
//            chunkBindingPoseOffset += bindingPoseOffset;
//            chunkWeightsOffset += weightsOffset;
//        }

//        destinationAnimationBuffer = new VertexBufferObject();
//        vertexBufferObjects.add(destinationAnimationBuffer);
        FloatBuffer meshBuffer = MemoryUtil.memAllocFloat(positionsSize + normalsSize * 3 + textureCoordsSize);
//        for (Model model : models) {
//            mode.getMeshDrawData().forEach(meshDrawData -> {
//                for(Mesh meshData : model.getMeshes()) {
//                    //populateMeshBuffer(meshBuffer, meshData);
//                }
//            });
//        }
        meshBuffer.flip();
//        destinationAnimationBuffer.bind(GL_ARRAY_BUFFER);
//        destinationAnimationBuffer.bufferDataFloat(GL_ARRAY_BUFFER, meshBuffer, DrawType.STATIC);
        MemoryUtil.memFree(meshBuffer);

        //defineVertexAttributes();

//        VertexBufferObject vertexBufferObject = new VertexBufferObject();
//        vertexBufferObjects.add(vertexBufferObject);
        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indicesSize);
        for (Model model : models) {
            model.getEntities().forEach(e -> {
                for (MeshData meshData : model.getMeshData()) {
                    indicesBuffer.put(meshData.getIndices());
                }
            });
        }
        indicesBuffer.flip();
//        vertexBufferObject.bind(GL_ELEMENT_ARRAY_BUFFER);
//        vertexBufferObject.bufferDataInt(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, DrawType.STATIC);
        MemoryUtil.memFree(indicesBuffer);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }



    private void loadBindingPoses(List<Model> models) {
//        int totalVertices = 0;
//        for (Model model : models) {
//            for (Mesh meshData : model.getMeshes()) {
//                totalVertices += meshData.getVertices().length / 3; // 3 floats per vertex position
//            }
//        }

//        int bufferSize = totalVertices * 14; // 14 floats per vertex (pos, norm, tangent, bitangent, texcoord)
//        FloatBuffer meshesBuffer = MemoryUtil.memAllocFloat(bufferSize);

        for (Model model : models) {
            for (MeshData meshData : model.getMeshData()) {
                //populateMeshBuffer(meshesBuffer, meshData);
            }
        }
        //meshesBuffer.flip();

//        bindingPoseBuffer = new VertexBufferObject();
//        vertexBufferObjects.add(bindingPoseBuffer);
//        bindingPoseBuffer.bind(GL_SHADER_STORAGE_BUFFER);
//        bindingPoseBuffer.bufferDataFloat(GL_SHADER_STORAGE_BUFFER, meshesBuffer, DrawType.STATIC);

        //MemoryUtil.memFree(meshesBuffer);

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
                    bufferSize += matrices.length * 64; // 64 bytes per 4x4 matrix (16 floats * 4 bytes)
                }
            }
        }

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
                    }
                    frame.clear();
                }
            }
        }

        dataBuffer.flip();

//        bonesMatricesBuffer = new VertexBufferObject();
//        vertexBufferObjects.add(bonesMatricesBuffer);
//        bonesMatricesBuffer.bind(GL_SHADER_STORAGE_BUFFER);
//        bonesMatricesBuffer.bufferData(GL_SHADER_STORAGE_BUFFER, dataBuffer, DrawType.STATIC);

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

//        bonesIndicesWeightsBuffer = new VertexBufferObject();
//        vertexBufferObjects.add(bonesIndicesWeightsBuffer);
//        bonesIndicesWeightsBuffer.bind(GL_SHADER_STORAGE_BUFFER);
//        bonesIndicesWeightsBuffer.bufferData(GL_SHADER_STORAGE_BUFFER, dataBuffer, DrawType.STATIC);
//        MemoryUtil.memFree(dataBuffer);

        //glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }
}
