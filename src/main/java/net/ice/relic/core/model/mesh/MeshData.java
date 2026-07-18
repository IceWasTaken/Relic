package net.ice.relic.core.model.mesh;

public class MeshData {


    private final float[] vertices;
    private final float[] normals;
    private final float[] tangents;
    private final float[] bitangents;
    private final float[] weights;
    private final float[] textureCoords;

    private final int[] indices;
    private final int[] boneIndices;

    private int materialIndex;

    public MeshData(float[] vertices, float[] normals, float[] tangents, float[] bitangents, float[] textureCoords, int[] indices, int[] boneIndices, float[] weights, int materialIndex) {
        this.vertices = vertices;
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

    public void setMaterialIndex(int materialIndex) {
        this.materialIndex = materialIndex;
    }

    public int getMaterialIndex() {
        return materialIndex;
    }

    public float[] getVertices() {
        return vertices;
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
