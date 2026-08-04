package net.ice.relic.core.model;

import net.ice.curio.library.opengl.object.GLBuffer;

public class Mesh {

    private final float[] vertexPositions;
    private final float[] normals;
    private final float[] tangents;
    private final float[] bitangents;
    private final float[] weights;
    private final float[] textureCoords;

    private final int[] indices;
    private final int[] boneIndices;

    private int materialIndex;

    public Mesh(float[] vertices, float[] normals, float[] tangents, float[] bitangents, float[] textureCoords, int[] indices, int[] boneIndices, float[] weights, int materialIndex) {
        this.vertexPositions = vertices;
        this.normals = normals;
        this.tangents = tangents;
        this.bitangents = bitangents;
        this.weights = weights;
        this.textureCoords = textureCoords;
        this.indices = indices;
        this.boneIndices = boneIndices;
        this.materialIndex = 0;
    }

    public Mesh(float[] vertices, float[] normals, float[] tangents, float[] bitangents, float[] textureCoords, int[] indices, int[] boneIndices, float[] weights) {
        this(vertices, normals, tangents, bitangents, textureCoords, indices, boneIndices, weights, 0);
    }

    public int getMeshSize() {
        int vertexSize = vertexPositions.length;
        int normalsSize = normals.length * 3;
        int textureCoordsSize = textureCoords.length;

        return (vertexSize + normalsSize + textureCoordsSize) * 4;
    }

    public int getIndicesSize() {
        return indices.length * 4;
    }

    public void populateBufferWithMesh(GLBuffer meshesBuffer) {
        int rows = vertexPositions.length / 3;
        for (int row = 0; row < rows; row++) {
            int startPos = row * 3;
            int startTextCoord = row * 2;
            meshesBuffer.putFloat(vertexPositions[startPos]);
            meshesBuffer.putFloat(vertexPositions[startPos + 1]);
            meshesBuffer.putFloat(vertexPositions[startPos + 2]);
            meshesBuffer.putFloat(normals[startPos]);
            meshesBuffer.putFloat(normals[startPos + 1]);
            meshesBuffer.putFloat(normals[startPos + 2]);
            meshesBuffer.putFloat(tangents[startPos]);
            meshesBuffer.putFloat(tangents[startPos + 1]);
            meshesBuffer.putFloat(tangents[startPos + 2]);
            meshesBuffer.putFloat(bitangents[startPos]);
            meshesBuffer.putFloat(bitangents[startPos + 1]);
            meshesBuffer.putFloat(bitangents[startPos + 2]);
            meshesBuffer.putFloat(textureCoords[startTextCoord]);
            meshesBuffer.putFloat(textureCoords[startTextCoord + 1]);
        }
    }

    public float[] getVertexPositions() {
        return vertexPositions;
    }

    public void setMaterialIndex(int materialIndex) {
        this.materialIndex = materialIndex;
    }

    public int getMaterialIndex() {
        return materialIndex;
    }

    public int[] getIndices() {
        return indices;
    }

    public int[] getBoneIndices() {
        return boneIndices;
    }

    public float[] getWeights() {
        return weights;
    }


}