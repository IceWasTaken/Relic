package net.ice.relic.common.util;

import net.ice.heirloom.color.RGBColor;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;

public class AssimpUtil {

    /**
     * Process an aiVector3D buffer, returns a float array. Three floats per vector in the buffer.
     * Used primarily for vertex positions and normals.
     * @param buffer the data to process.
     * @return float[] with data from the vector buffer.
     * @since 0.3.0
     * @see #processAIVectorBufferBackup(AIVector3D.Buffer, float[])
     */
    public static float[] processAIVectorBuffer(AIVector3D.Buffer buffer) {
        float[] result = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D vector3D = buffer.get();
            result[pos++] = vector3D.x();
            result[pos++] = vector3D.y();
            result[pos++] = vector3D.z();
        }

        return result;
    }

    /**
     * Process an aiVector3D buffer, returns a float array. Three floats per vector in the buffer.
     * If the buffer is empty, the backup array is used instead.
     * Used primarily for tangents and bitangents.
     * @param buffer the data to process.
     * @param backup a backup array to use if the buffer is empty.
     * @return float[] with data from the vector buffer.
     * @since 0.3.0
     * @see #processAIVectorBuffer(AIVector3D.Buffer)
     */
    public static float[] processAIVectorBufferBackup(AIVector3D.Buffer buffer, float[] backup) {
        if(buffer == null || buffer.remaining() == 0) {
            return backup;
        }

        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D vector3D = buffer.get();
            data[pos++] = vector3D.x();
            data[pos++] = vector3D.y();
            data[pos++] = vector3D.z();
        }

        if (data.length == 0) {
            data = new float[backup.length];
        }
        return data;
    }

    public static float[] processTextCoords(AIVector3D.Buffer buffer) {
        float[] data = new float[buffer.remaining() * 2];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D textCoord = buffer.get();
            data[pos++] = textCoord.x();
            data[pos++] = 1 - textCoord.y();
        }
        return data;
    }

    public static int[] processIndices(AIMesh aiMesh) {
        List<Integer> indices = new ArrayList<>();
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
        return indices.stream().mapToInt(Integer::intValue).toArray();
    }

    public static RGBColor getMaterialColor(AIMaterial aiMaterial, String matKey, int textureType, int index) {

        AIColor4D color = AIColor4D.create();
        int result = aiGetMaterialColor(aiMaterial, matKey, textureType, index, color);
        if (result == aiReturn_SUCCESS) {
            return new RGBColor(color.r(), color.g(), color.b(), color.a());
        }
        return new RGBColor(0, 0, 0, 0);
    }

    public static RGBColor getMaterialColor(AIMaterial aiMaterial, String matKey, int textureType) {

        AIColor4D color = AIColor4D.create();
        int result = aiGetMaterialColor(aiMaterial, matKey, textureType, 0, color);
        if (result == aiReturn_SUCCESS) {
            return new RGBColor(color.r(), color.g(), color.b(), color.a());
        }
        return new RGBColor(0, 0, 0, 0);
    }

    public static RGBColor getMaterialColor(AIMaterial aiMaterial, String matKey) {
        AIColor4D color = AIColor4D.create();
        int result = aiGetMaterialColor(aiMaterial, matKey, aiTextureType_NONE, 0, color);
        if (result == aiReturn_SUCCESS) {
            return new RGBColor(color.r(), color.g(), color.b(), color.a());
        }
        return new RGBColor(0, 0, 0, 0);
    }

    public static String getTexturePath(AIMaterial aiMaterial, int textureType, int index) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            AIString aiPath = AIString.calloc(stack);
            Assimp.aiGetMaterialTexture(aiMaterial, textureType, index, aiPath, (IntBuffer) null, null, null, null, null, null);
            return aiPath.dataString();
        }
    }

    public static String getTexturePath(AIMaterial aiMaterial, int textureType) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            AIString aiPath = AIString.calloc(stack);
            Assimp.aiGetMaterialTexture(aiMaterial, textureType, 0, aiPath, (IntBuffer) null, null, null, null, null, null);
            return aiPath.dataString();
        }
    }

//    public static GLTexture getTexture(AIScene aiScene, AIMaterial aiMaterial, TextureCache textureCache, Material material, int textureType) {
//        try(MemoryStack stack = MemoryStack.stackPush()) {
//            AIString aiTexturePath = AIString.calloc(stack);
//            aiGetMaterialTexture(aiMaterial, textureType, 0, aiTexturePath, (IntBuffer) null, null, null, null, null, null);
//            String texturePath = aiTexturePath.dataString();
//            if (!texturePath.isEmpty()) {
//                if(isInternalTexture(texturePath)) {
//                    AITexture aiTexture = getInternalTexture(aiScene, parseInt(texturePath.substring(1)));
//                    return textureCache.createTexture(aiTexture.pcDataCompressed());
//                } else {
//                    return textureCache.createTexture(Resource.getResourceDefaultNamespace(material.getTexturePath()));
//                }
//            }
//        }
//        return null;
//    }

    private static AITexture getInternalTexture(AIScene aiScene, int index) {
        return AITexture.create(aiScene.mTextures().get(index));
    }
    private static boolean isInternalTexture(String path) {
        return path.startsWith("*");
    }




}
