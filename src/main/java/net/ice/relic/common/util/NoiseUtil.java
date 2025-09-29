package net.ice.relic.common.util;

import net.ice.relic.common.noise.FastNoiseLite;
import net.ice.relic.core.model.MeshData;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class NoiseUtil {

    public static MeshData createMeshData(int size, FastNoiseLite noiseGenerator, int uvMult) {
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> uvs = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector3f> tangents = new ArrayList<>();
        List<Vector3f> bitangents = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        for (int z = 0; z < size; z++) {
            for (int x = 0; x < size; x++) {
                float y = noiseGenerator.GetNoise(x, z) * 10f; // scale height
                vertices.add(new Vector3f(x, y, z));
                uvs.add(new Vector2f((float) x / size * uvMult, (float) z / size * uvMult));
                normals.add(new Vector3f(0, 0, 0));
                tangents.add(new Vector3f(0, 0, 0));
                bitangents.add(new Vector3f(0, 0, 0));
            }
        }

        for (int z = 0; z < size - 1; z++) {
            for (int x = 0; x < size - 1; x++) {
                int topLeft     = z * size + x;
                int topRight    = topLeft + 1;
                int bottomLeft  = (z + 1) * size + x;
                int bottomRight = bottomLeft + 1;

                indices.add(topLeft);
                indices.add(bottomLeft);
                indices.add(topRight);

                indices.add(topRight);
                indices.add(bottomLeft);
                indices.add(bottomRight);
            }
        }

        for (int i = 0; i < indices.size(); i += 3) {
            int i0 = indices.get(i);
            int i1 = indices.get(i + 1);
            int i2 = indices.get(i + 2);

            Vector3f v0 = vertices.get(i0);
            Vector3f v1 = vertices.get(i1);
            Vector3f v2 = vertices.get(i2);

            Vector2f uv0 = uvs.get(i0);
            Vector2f uv1 = uvs.get(i1);
            Vector2f uv2 = uvs.get(i2);

            // Edges of the triangle
            Vector3f deltaPos1 = new Vector3f();
            Vector3f deltaPos2 = new Vector3f();
            v1.sub(v0, deltaPos1);
            v2.sub(v0, deltaPos2);

            // UV deltas
            Vector2f deltaUV1 = new Vector2f();
            Vector2f deltaUV2 = new Vector2f();
            uv1.sub(uv0, deltaUV1);
            uv2.sub(uv0, deltaUV2);

            float r = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV1.y * deltaUV2.x);

            Vector3f tangent = new Vector3f(
                    (deltaPos1.x * deltaUV2.y - deltaPos2.x * deltaUV1.y) * r,
                    (deltaPos1.y * deltaUV2.y - deltaPos2.y * deltaUV1.y) * r,
                    (deltaPos1.z * deltaUV2.y - deltaPos2.z * deltaUV1.y) * r
            );

            Vector3f bitangent = new Vector3f(
                    (deltaPos2.x * deltaUV1.x - deltaPos1.x * deltaUV2.x) * r,
                    (deltaPos2.y * deltaUV1.x - deltaPos1.y * deltaUV2.x) * r,
                    (deltaPos2.z * deltaUV1.x - deltaPos1.z * deltaUV2.x) * r
            );

            Vector3f normal = deltaPos1.cross(deltaPos2, new Vector3f()).normalize();

            // Accumulate per vertex
            normals.get(i0).add(normal);
            normals.get(i1).add(normal);
            normals.get(i2).add(normal);

            tangents.get(i0).add(tangent);
            tangents.get(i1).add(tangent);
            tangents.get(i2).add(tangent);

            bitangents.get(i0).add(bitangent);
            bitangents.get(i1).add(bitangent);
            bitangents.get(i2).add(bitangent);
        }

        for (int i = 0; i < vertices.size(); i++) {
            normals.get(i).normalize();
            tangents.get(i).normalize();
            bitangents.get(i).normalize();
        }

        float[] vertexArray = new float[vertices.size() * 3];
        float[] normalArray = new float[normals.size() * 3];
        float[] tangentArray = new float[tangents.size() * 3];
        float[] bitangentArray = new float[bitangents.size() * 3];
        float[] uvArray = new float[uvs.size() * 2];
        int[] indicesArray = new int[indices.size()];

        int i = 0;
        for (Vector3f v : vertices) {
            vertexArray[i++] = v.x;
            vertexArray[i++] = v.y;
            vertexArray[i++] = v.z;
        }

        i = 0;
        for (Vector3f n : normals) {
            normalArray[i++] = n.x;
            normalArray[i++] = n.y;
            normalArray[i++] = n.z;
        }

        i = 0;
        for (Vector3f t : tangents) {
            tangentArray[i++] = t.x;
            tangentArray[i++] = t.y;
            tangentArray[i++] = t.z;
        }

        i = 0;
        for (Vector3f b : bitangents) {
            bitangentArray[i++] = b.x;
            bitangentArray[i++] = b.y;
            bitangentArray[i++] = b.z;
        }

        i = 0;
        for (Vector2f uv : uvs) {
            uvArray[i++] = uv.x;
            uvArray[i++] = uv.y;
        }

        for (i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }

        return new MeshData(vertexArray, normalArray, tangentArray, bitangentArray, uvArray, indicesArray, null, null, null);
    }
}
