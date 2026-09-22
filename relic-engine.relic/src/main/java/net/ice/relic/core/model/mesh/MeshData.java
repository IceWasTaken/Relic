package net.ice.relic.core.model.mesh;

import net.ice.curio.library.opengl.object.GLBuffer;

public class MeshData {

    private final float[] positions;
    private final float[] normals;
    private final float[] tangents;
    private final float[] bitangents;
    private final float[] textureCoords;
    private final int[] indices;

    private final float[] weights;
    private final int[] boneIndices;

    private int materialIndex;

    public MeshData(float[] vertices, float[] normals, float[] tangents, float[] bitangents, float[] textureCoords, int[] indices, int[] boneIndices, float[] weights, int materialIndex) {
        this.positions = vertices;
        this.normals = normals;
        this.tangents = tangents;
        this.bitangents = bitangents;
        this.weights = weights;
        this.textureCoords = textureCoords;
        this.indices = indices;
        this.boneIndices = boneIndices;
        this.materialIndex = 0;
    }

    public MeshData(float[] vertices, float[] normals, float[] tangents, float[] bitangents, float[] textureCoords, int[] indices, int[] boneIndices, float[] weights) {
        this(vertices, normals, tangents, bitangents, textureCoords, indices, boneIndices, weights, 0);
    }

    public int getMeshSize() {
        int vertexSize = positions.length;
        int normalsSize = normals.length * 3;
        int textureCoordsSize = textureCoords.length;

        return (vertexSize + normalsSize + textureCoordsSize) * 4;
    }

    public int getIndicesSize() {
        return indices.length * 4;
    }

    public void populateBufferWithMesh(GLBuffer meshesBuffer) {
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
            meshesBuffer.putFloat(textureCoords[startTextCoord]);
            meshesBuffer.putFloat(textureCoords[startTextCoord + 1]);
        }
    }

    public float[] getPositions() {
        return positions;
    }

    public float[] getNormals() {
        return normals;
    }

    public float[] getTangents() {
        return tangents;
    }

    public float[] getBitangents() {
        return bitangents;
    }

    public float[] getTextureCoords() {
        return textureCoords;
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