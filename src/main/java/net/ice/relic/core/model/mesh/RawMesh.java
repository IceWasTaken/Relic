package net.ice.relic.core.model.mesh;

import java.util.ArrayList;
import java.util.List;

//Mesh directly taken from a relic 3D model file
public class RawMesh {

    private String id;

    private int[] indices;
    private int[] boneIndices;
    private float[] weights;

    private int materialIndex;

    public RawMesh(String id, int[] indices, int[] boneIndices, float[] weights, int materialIndex) {
        this.id = id;
        this.indices = indices;
        this.boneIndices = boneIndices;
        this.weights = weights;
        this.materialIndex = materialIndex;
    }

    public MeshData createMesh(float[] vertices, float[] normals, float[] tangents, float[] biTangents, float[] textureCoords) {
        List<Float> meshVertices = new ArrayList<>();
        List<Float> meshNormals = new ArrayList<>();
        List<Float> meshTangents = new ArrayList<>();
        List<Float> meshBiTangents = new ArrayList<>();
        List<Float> meshTextureCoords = new ArrayList<>();

        for (int index : indices) {

            int v = index * 3;
            meshVertices.add(vertices[v]);
            meshVertices.add(vertices[v + 1]);
            meshVertices.add(vertices[v + 2]);

            int n = index * 3;
            meshNormals.add(normals[n]);
            meshNormals.add(normals[n + 1]);
            meshNormals.add(normals[n + 2]);

            int t = index * 3;
            meshTangents.add(tangents[t]);
            meshTangents.add(tangents[t + 1]);
            meshTangents.add(tangents[t + 2]);

            int b = index * 3;
            meshBiTangents.add(biTangents[b]);
            meshBiTangents.add(biTangents[b + 1]);
            meshBiTangents.add(biTangents[b + 2]);

            int uv = index * 2;
            meshTextureCoords.add(textureCoords[uv]);
            meshTextureCoords.add(textureCoords[uv + 1]);
        }

        return new MeshData(
                toArray(meshVertices),
                toArray(meshNormals),
                toArray(meshTangents),
                toArray(meshBiTangents),
                toArray(meshTextureCoords),
                indices,
                boneIndices,
                weights,
                materialIndex
        );
    }

    private float[] toArray(List<Float> floats) {
        float[] floats1 = new float[floats.size()];

        int i = 0;
        for(Float f : floats) {
            floats1[i] = f;
            i++;
        }
        return floats1;
    }
}
