package net.ice.relic.engine.opengl.model;

import net.ice.relic.engine.opengl.AABB;
import org.joml.Vector3f;

public class MeshData {

    private AABB aabb;

    private float[] vertices;
    private float[] normals;
    private float[] tangents;
    private float[] bitangents;
    private float[] weights;
    private float[] textureCoords;

    private int[] indices;
    private int[] boneIndices;

    private int materialIndex;

    public MeshData(float[] vertices, float[] normals, float[] tangents, float[] bitangents, float[] textureCoords, int[] indices, int[] boneIndices, float[] weights, AABB aabb) {
        this.aabb = aabb;
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

    public MeshData(float[] vertices, float[] normals, float[] tangents, float[] bitangents, float[] textureCoords, int[] indices, int[] boneIndices, float[] weights, Vector3f aabbMin, Vector3f aabbMax) {
        this(vertices, normals, tangents, bitangents, textureCoords, indices, boneIndices, weights, new AABB(aabbMin, aabbMax));
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

    public AABB getAabb() {
        return aabb;
    }
}
