package net.ice.relic.engine.opengl.model;

import org.lwjgl.assimp.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;

public class Model {

    public static int DEFAULT_FLAGS = aiProcess_Triangulate | aiProcess_FlipUVs | aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices | aiProcess_PreTransformVertices;
    private final List<Mesh> meshes = new ArrayList<>();

    public Model(String path, int flags) {
        AIScene scene = aiImportFile(path, flags);

        if (scene == null || (scene.mFlags() & AI_SCENE_FLAGS_INCOMPLETE) != 0 || scene.mRootNode() == null) {
            throw new RuntimeException("Error while loading model: " + aiGetErrorString());
        }

        processNode(scene.mRootNode(), scene);
    }

    private void processNode(AINode node, AIScene scene) {
        int numMeshes = node.mNumMeshes();
        IntBuffer meshIndices = node.mMeshes();
        for (int i = 0; i < numMeshes; i++) {
            int meshIndex = meshIndices.get(i);
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(meshIndex));
            meshes.add(processMesh(mesh, scene));
        }

        int numChildren = node.mNumChildren();
        for (int i = 0; i < numChildren; i++) {
            processNode(AINode.create(node.mChildren().get(i)), scene);
        }
    }

    private Mesh processMesh(AIMesh mesh, AIScene scene) {
        List<Float> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        int textureId = -1;
        int emissiveTextureId = -1;
        int normalTextureID = -1;
        boolean hasEmissiveMap = false;

        float emissiveStrength = 1.0f;
        float[] emissiveColor = new float[] {1.0f, 1.0f, 1.0f};

        for (int i = 0; i < mesh.mNumVertices(); i++) {
            AIVector3D pos = mesh.mVertices().get(i);
            vertices.add(pos.x());
            vertices.add(pos.y());
            vertices.add(pos.z());

            AIVector3D normal = mesh.mNormals().get(i);
            vertices.add(normal.x());
            vertices.add(normal.y());
            vertices.add(normal.z());

            if (mesh.mTextureCoords(0) != null) {
                AIVector3D texCoord = mesh.mTextureCoords(0).get(i);
                vertices.add(texCoord.x());
                vertices.add(texCoord.y());
            } else {
                vertices.add(0.0f);
                vertices.add(0.0f);
            }
        }

        for (int i = 0; i < mesh.mNumFaces(); i++) {
            AIFace face = mesh.mFaces().get(i);
            IntBuffer buffer = face.mIndices();
            while (buffer.hasRemaining()) {
                indices.add(buffer.get());
            }
        }

        AIMaterial material = AIMaterial.create(scene.mMaterials().get(mesh.mMaterialIndex()));

        AIColor4D color = AIColor4D.create();
        if (aiGetMaterialColor(material, AI_MATKEY_COLOR_EMISSIVE, aiTextureType_NONE, 0, color) == 0) {
            emissiveColor[0] = color.r();
            emissiveColor[1] = color.g();
            emissiveColor[2] = color.b();
        }

        float[] strength = new float[1];
        if (aiGetMaterialFloatArray(material, AI_MATKEY_EMISSIVE_INTENSITY, aiTextureType_NONE, 0, strength, new int[]{1}) == 0) {
            emissiveStrength = strength[0];
        }

        AIString path = AIString.calloc();

        if (aiGetMaterialTexture(material, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null) == 0) {
            String texPath = "models/" + path.dataString();
            textureId = TextureLoader.loadTexture(texPath);
        }

        path = AIString.calloc();
        if (aiGetMaterialTexture(material, aiTextureType_EMISSIVE, 0, path, (IntBuffer) null, null, null, null, null, null) == 0) {
            hasEmissiveMap = true;
            String texPath = "models/" + path.dataString();
            emissiveTextureId = TextureLoader.loadTexture(texPath);
        }

        path = AIString.calloc();
        if (aiGetMaterialTexture(material, aiTextureType_NORMALS, 0, path, (IntBuffer) null, null, null, null, null, null) == 0) {
            String texPath = "models/" + path.dataString();
            normalTextureID = TextureLoader.loadTexture(texPath);
        }

        return new Mesh(vertices, indices, textureId, emissiveStrength, emissiveColor, hasEmissiveMap, emissiveTextureId, normalTextureID);
    }

    public void render(int shaderProgram) {
        for (Mesh mesh : meshes) {
            mesh.render(shaderProgram);
        }
    }

    public void cleanup() {
        for (Mesh mesh : meshes) {
            mesh.cleanup();
        }
    }
}
