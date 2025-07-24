package net.ice.relic.generation;

import net.ice.relic.common.cache.MaterialCache;
import net.ice.relic.common.cache.ModelCache;
import net.ice.relic.common.cache.TextureCache;
import net.ice.relic.common.model.Material;
import net.ice.relic.common.model.MeshData;
import net.ice.relic.common.scene.SceneObject;
import net.ice.relic.engine.opengl.model.Mesh;
import net.ice.relic.engine.opengl.model.Model;
import net.ice.relic.engine.opengl.model.texture.Texture;
import net.ice.relic.noise.FastNoiseLite;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Terrain {

    private final int width = 1024;
    private final int height = 1024;

    private final List<Vector3f> vertices = new ArrayList<>();
    private final List<Vector3f> normals = new ArrayList<>();
    private final List<Vector2f> uvs = new ArrayList<>();
    private final List<Integer> indices = new ArrayList<>();
    private final List<Vector3f> tangents = new ArrayList<>();
    private final List<Vector3f> bitangents = new ArrayList<>();

    private Texture texture;
    private Texture normalTexture;
    private Material material;
    private Model model;
    private MeshData meshData;

    public Terrain(int seed, MaterialCache materialCache, TextureCache textureCache, ModelCache modelCache) {
        FastNoiseLite noise = new FastNoiseLite(seed);
        noise.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        noise.SetCellularJitter(2.4f);

        // 1. Generate vertices, uvs, and placeholders
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                float y = noise.GetNoise(x, z) * 10f; // scale height
                y = (float) Math.pow(y, 1.5);
                vertices.add(new Vector3f(x, y, z));
                uvs.add(new Vector2f((float) x / width * 8.0f, (float) z / height * 8.0f));
                normals.add(new Vector3f(0, 0, 0));
                tangents.add(new Vector3f(0, 0, 0));
                bitangents.add(new Vector3f(0, 0, 0));
            }
        }

        // 2. Generate triangle indices
        for (int z = 0; z < height - 1; z++) {
            for (int x = 0; x < width - 1; x++) {
                int topLeft     = z * width + x;
                int topRight    = topLeft + 1;
                int bottomLeft  = (z + 1) * width + x;
                int bottomRight = bottomLeft + 1;

                indices.add(topLeft);
                indices.add(bottomLeft);
                indices.add(topRight);

                indices.add(topRight);
                indices.add(bottomLeft);
                indices.add(bottomRight);
            }
        }

        // 3. Calculate normals, tangents, and bitangents
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

        // 4. Normalize all vectors
        for (int i = 0; i < vertices.size(); i++) {
            normals.get(i).normalize();
            tangents.get(i).normalize();
            bitangents.get(i).normalize();
        }

        MeshData meshData = new MeshData(getVertices(), getNormals(), getTangents(), getBitangents(), getUVs(), getIndices(), null, null, null);
        meshData.setMaterialIndex(0);

        List<MeshData> meshDataList = new ArrayList<>();
        meshDataList.add(meshData);

        Material material = new Material();

        Texture texture1 = textureCache.createTexture("resources/textures/terrain/" + "grass.png");
        //Texture texture2 = textureCache.createTexture("resources/textures/terrain/" + "grass_normal.png");

        material.setTexturePath(texture1.getTexturePath());
        //material.setNormalMapPath(texture2.getTexturePath());

        material.setDiffuseColor(Material.DEFAULT_COLOR);

        material.setTexture(texture1);
        //material.setNormalMap(texture2);


        this.material = material;
        this.model = new Model("terrain", meshDataList, null);

        materialCache.addMaterial(material);
        modelCache.addModel(model);
    }

    // Accessors
    public float[] getVertices() {
        float[] data = new float[vertices.size() * 3];
        int i = 0;
        for (Vector3f v : vertices) {
            data[i++] = v.x;
            data[i++] = v.y;
            data[i++] = v.z;
        }
        return data;
    }

    public float[] getNormals() {
        float[] data = new float[normals.size() * 3];
        int i = 0;
        for (Vector3f n : normals) {
            data[i++] = n.x;
            data[i++] = n.y;
            data[i++] = n.z;
        }
        return data;
    }

    public float[] getTangents() {
        float[] data = new float[tangents.size() * 3];
        int i = 0;
        for (Vector3f t : tangents) {
            data[i++] = t.x;
            data[i++] = t.y;
            data[i++] = t.z;
        }
        return data;
    }

    public float[] getBitangents() {
        float[] data = new float[bitangents.size() * 3];
        int i = 0;
        for (Vector3f b : bitangents) {
            data[i++] = b.x;
            data[i++] = b.y;
            data[i++] = b.z;
        }
        return data;
    }

    public float[] getUVs() {
        float[] data = new float[uvs.size() * 2];
        int i = 0;
        for (Vector2f uv : uvs) {
            data[i++] = uv.x;
            data[i++] = uv.y;
        }
        return data;
    }

    public int[] getIndices() {
        int[] data = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            data[i] = indices.get(i);
        }
        return data;
    }

    public Model getModel() {
        return model;
    }
}
