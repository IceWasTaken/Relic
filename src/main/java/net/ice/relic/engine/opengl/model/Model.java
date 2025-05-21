package net.ice.relic.engine.opengl.model;

import net.ice.relic.engine.opengl.Uniforms;
import net.ice.relic.engine.util.ColorUtil;
import net.ice.relic.engine.util.IOUtil;
import org.lwjgl.assimp.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryUtil.*;

public class Model {

    private final List<Mesh> meshes = new ArrayList<>();
    private Material material;

    public Model(String path, int flags) {
        AIScene scene = IOUtil.readModelFile(path, flags);

        if (scene == null || (scene.mFlags() & AI_SCENE_FLAGS_INCOMPLETE) != 0 || scene.mRootNode() == null) {
            throw new RuntimeException("Error loading model: " + aiGetErrorString());
        }

        processNode(scene.mRootNode(), scene);

        System.out.println("Loaded " + meshes.size() + " meshes");
    }

    private void processNode(AINode node, AIScene scene) {
        for (int i = 0; i < node.mNumMeshes(); i++) {
            int meshIndex = node.mMeshes().get(i);
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(meshIndex));
            meshes.add(processMesh(mesh, scene));
        }

        for (int i = 0; i < node.mNumChildren(); i++) {
            processNode(AINode.create(node.mChildren().get(i)), scene);
        }
    }

    private Mesh processMesh(AIMesh mesh, AIScene scene) {
        List<Float> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        int vertexCount = mesh.mNumVertices();

        for (int i = 0; i < vertexCount; i++) {
            AIVector3D pos = mesh.mVertices().get(i);
            AIVector3D normal = mesh.mNormals() != null ? mesh.mNormals().get(i) : AIVector3D.calloc();
            AIVector3D texCoord = mesh.mTextureCoords(0) != null ? mesh.mTextureCoords(0).get(i) : AIVector3D.calloc();

            // Position
            vertices.add(pos.x());
            vertices.add(pos.y());
            vertices.add(pos.z());

            // Normal
            vertices.add(normal.x());
            vertices.add(normal.y());
            vertices.add(normal.z());

            // UV
            vertices.add(texCoord.x());
            vertices.add(texCoord.y());

            // Tangent (if available)
            if (mesh.mTangents() != null) {
                AIVector3D tangent = mesh.mTangents().get(i);
                vertices.add(tangent.x());
                vertices.add(tangent.y());
                vertices.add(tangent.z());
            } else {
                vertices.add(0f);
                vertices.add(0f);
                vertices.add(0f);
            }
        }

        for (int i = 0; i < mesh.mNumFaces(); i++) {
            AIFace face = mesh.mFaces().get(i);
            IntBuffer faceIndices = face.mIndices();
            while (faceIndices.hasRemaining()) {
                indices.add(faceIndices.get());
            }
        }

        // Material
        AIMaterial aiMaterial = AIMaterial.create(scene.mMaterials().get(mesh.mMaterialIndex()));
        Material material = processMaterial(aiMaterial); // implement this to return a Material object

        return buildMesh(vertices, indices, material);
    }

    private Material processMaterial(AIMaterial aiMaterial) {
        float[] buf = new float[1];
        AIColor4D color = AIColor4D.create();

        float metallic = getFloat(aiMaterial, AI_MATKEY_METALLIC_FACTOR, buf);
        float roughness = getFloat(aiMaterial, AI_MATKEY_ROUGHNESS_FACTOR, buf);

        float specular = getFloat(aiMaterial, AI_MATKEY_SHININESS, buf); // Fallback phong-style
        float alpha = getFloat(aiMaterial, AI_MATKEY_OPACITY, buf);

        ColorUtil.Color emissionColor = new ColorUtil.Color(0f, 0f, 0f);
        if (aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_EMISSIVE, aiTextureType_NONE, 0, color) == 0) {
            emissionColor = new ColorUtil.Color(color.r(), color.g(), color.b());
        }

        float emissiveStrength = getFloat(aiMaterial, AI_MATKEY_EMISSIVE_INTENSITY, buf); // May not always be present

        this.material = new Material(
                metallic,
                specular,
                0f,
                roughness,
                0f,
                0f,
                0f,
                0f,
                0f,
                0f,
                1.0f,
                0f,
                0f,
                emissionColor,
                emissiveStrength,
                alpha
        );

        return material;
    }

    private Mesh buildMesh(List<Float> vertices, List<Integer> indices, Material material) {
        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        List<Integer> vboIds = new ArrayList<>();

        // Vertex data
        FloatBuffer vertexBuffer = memAllocFloat(vertices.size());
        for (float f : vertices) vertexBuffer.put(f);
        vertexBuffer.flip();

        int vertexVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexVbo);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        vboIds.add(vertexVbo);

        int stride = (3 + 3 + 2 + 3) * Float.BYTES; // pos + normal + uv + tangent

        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);                     // Position
        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3 * Float.BYTES);       // Normal
        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6 * Float.BYTES);       // UV
        glVertexAttribPointer(3, 3, GL_FLOAT, false, stride, 8 * Float.BYTES);       // Tangent

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);

        // Index data
        IntBuffer indexBuffer = memAllocInt(indices.size());
        for (int i : indices) indexBuffer.put(i);
        indexBuffer.flip();

        int indexVbo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexVbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        vboIds.add(indexVbo);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        memFree(vertexBuffer);
        memFree(indexBuffer);

        return new Mesh(vaoId, vboIds, indices.size(), material);
    }

    private float getFloat(AIMaterial mat, String key, float[] out) {
        int[] max = new int[]{1};
        aiGetMaterialFloatArray(mat, key, aiTextureType_NONE, 0, out, max);
        return out[0];
    }

    public void render(int shaderProgramID) {
        for (Mesh mesh : meshes) {
            mesh.render();
        }
    }

    public void cleanup() {
        for (Mesh mesh : meshes) {
            mesh.cleanup();
        }
    }

    public Material getMaterial() {
        return material;
    }
}
