package net.ice.relic.engine.opengl.model;

import net.ice.relic.engine.opengl.AABB;
import net.ice.relic.engine.opengl.MaterialCache;
import net.ice.relic.engine.opengl.model.texture.Texture;
import net.ice.relic.engine.opengl.model.texture.TextureLoader;
import net.ice.relic.engine.util.ColorUtil;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;
import org.tinylog.Logger;

import java.io.File;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.assimp.Assimp.*;

public class ModelLoader {

    public static final int MAX_BONES = 150;
    private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();

    public static Model loadModelFromFile(String id, String name, TextureLoader loader, MaterialCache materialCache, int flags) {
        String filePath = "resources/models/" + name;
        File file = new File(filePath);

        if (!file.exists()) {
            throw new RuntimeException("Model file does not exist: " + name);
        }

        String modelDirectory = file.getParent();

        AIScene aiScene = aiImportFile("resources/models/" + name, flags);
        if (aiScene == null) {
            throw new RuntimeException("Unable to load model: " + name);
        }


        int meshCount = aiScene.mNumMeshes();
        int materialCount = aiScene.mNumMaterials();

        PointerBuffer aiMeshes = aiScene.mMeshes();

        List<Material> materials = new ArrayList<>(materialCount);
        List<MeshData> meshDataList = new ArrayList<>();
        List<Bone> bones = new ArrayList<>();

        for (int i = 0; i < materialCount; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
            Material material = processMaterial(aiMaterial, modelDirectory, loader);
            materialCache.addMaterial(material);
            materials.add(material);
        }

        for (int i = 0; i < meshCount; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            MeshData meshData = processMesh(aiMesh, bones);
            int materialIndex = aiMesh.mMaterialIndex();
            if (materialIndex >= 0 && materialIndex < materials.size()) {
                meshData.setMaterialIndex(materials.get(materialIndex).getMaterialIndex());
            } else {
                meshData.setMaterialIndex(MaterialCache.DEFAULT_MATERIAL_INDEX);
            }
            meshDataList.add(meshData);
        }

        List<Animation> animations = new ArrayList<>();
        int numAnimations = aiScene.mNumAnimations();
        if (numAnimations > 0) {
            Node rootNode = buildNodesTree(aiScene.mRootNode(), null);
            Matrix4f globalInverseTransformation = toMatrix(aiScene.mRootNode().mTransformation()).invert();
            animations = processAnimations(aiScene, bones, rootNode, globalInverseTransformation);
        }

        aiReleaseImport(aiScene);

        return new Model(id, meshDataList, animations);
    }

    private static Node buildNodesTree(AINode aiNode, Node parentNode) {
        String nodeName = aiNode.mName().dataString();
        Node node = new Node(nodeName, parentNode, toMatrix(aiNode.mTransformation()));

        int numChildren = aiNode.mNumChildren();
        PointerBuffer aiChildren = aiNode.mChildren();
        for (int i = 0; i < numChildren; i++) {
            AINode aiChildNode = AINode.create(aiChildren.get(i));
            Node childNode = buildNodesTree(aiChildNode, node);
            node.addChild(childNode);
        }
        return node;
    }

    private static List<Animation> processAnimations(AIScene aiScene, List<Bone> boneList,
                                                           Node rootNode, Matrix4f globalInverseTransformation) {
        List<Animation> animations = new ArrayList<>();

        // Process all animations
        int numAnimations = aiScene.mNumAnimations();
        PointerBuffer aiAnimations = aiScene.mAnimations();
        for (int i = 0; i < numAnimations; i++) {
            AIAnimation aiAnimation = AIAnimation.create(aiAnimations.get(i));
            int maxFrames = calcAnimationMaxFrames(aiAnimation);

            List<Animation.AnimatedFrame> frames = new ArrayList<>();
            Animation animation = new Animation(aiAnimation.mName().dataString(), aiAnimation.mDuration(), frames);
            animations.add(animation);

            for (int j = 0; j < maxFrames; j++) {
                Matrix4f[] boneMatrices = new Matrix4f[MAX_BONES];
                Arrays.fill(boneMatrices, IDENTITY_MATRIX);
                Animation.AnimatedFrame animatedFrame = new Animation.AnimatedFrame(boneMatrices);
                buildFrameMatrices(aiAnimation, boneList, animatedFrame, j, rootNode,
                        rootNode.getNodeTransformation(), globalInverseTransformation);
                frames.add(animatedFrame);
            }
        }
        return animations;
    }

    private static Matrix4f buildNodeTransformationMatrix(AINodeAnim aiNodeAnim, int frame) {
        AIVectorKey.Buffer positionKeys = aiNodeAnim.mPositionKeys();
        AIVectorKey.Buffer scalingKeys = aiNodeAnim.mScalingKeys();
        AIQuatKey.Buffer rotationKeys = aiNodeAnim.mRotationKeys();

        AIVectorKey aiVecKey;
        AIVector3D vec;

        Matrix4f nodeTransform = new Matrix4f();
        int numPositions = aiNodeAnim.mNumPositionKeys();
        if (numPositions > 0) {
            aiVecKey = positionKeys.get(Math.min(numPositions - 1, frame));
            vec = aiVecKey.mValue();
            nodeTransform.translate(vec.x(), vec.y(), vec.z());
        }
        int numRotations = aiNodeAnim.mNumRotationKeys();
        if (numRotations > 0) {
            AIQuatKey quatKey = rotationKeys.get(Math.min(numRotations - 1, frame));
            AIQuaternion aiQuat = quatKey.mValue();
            Quaternionf quat = new Quaternionf(aiQuat.x(), aiQuat.y(), aiQuat.z(), aiQuat.w());
            nodeTransform.rotate(quat);
        }
        int numScalingKeys = aiNodeAnim.mNumScalingKeys();
        if (numScalingKeys > 0) {
            aiVecKey = scalingKeys.get(Math.min(numScalingKeys - 1, frame));
            vec = aiVecKey.mValue();
            nodeTransform.scale(vec.x(), vec.y(), vec.z());
        }

        return nodeTransform;
    }

    private static void buildFrameMatrices(AIAnimation aiAnimation, List<Bone> boneList, Animation.AnimatedFrame animatedFrame,
                                           int frame, Node node, Matrix4f parentTransformation, Matrix4f globalInverseTransform) {
        String nodeName = node.getName();
        AINodeAnim aiNodeAnim = findAIAnimNode(aiAnimation, nodeName);
        Matrix4f nodeTransform = node.getNodeTransformation();
        if (aiNodeAnim != null) {
            nodeTransform = buildNodeTransformationMatrix(aiNodeAnim, frame);
        }
        Matrix4f nodeGlobalTransform = new Matrix4f(parentTransformation).mul(nodeTransform);

        List<Bone> affectedBones = boneList.stream().filter(b -> b.boneName().equals(nodeName)).toList();
        for (Bone bone : affectedBones) {
            Matrix4f boneTransform = new Matrix4f(globalInverseTransform).mul(nodeGlobalTransform).
                    mul(bone.offsetMatrix());
            animatedFrame.getBonesMatrices()[bone.boneId()] = boneTransform;
        }

        for (Node childNode : node.getChildren()) {
            buildFrameMatrices(aiAnimation, boneList, animatedFrame, frame, childNode, nodeGlobalTransform,
                    globalInverseTransform);
        }
    }

    private static AINodeAnim findAIAnimNode(AIAnimation aiAnimation, String nodeName) {
        AINodeAnim result = null;
        int numAnimNodes = aiAnimation.mNumChannels();
        PointerBuffer aiChannels = aiAnimation.mChannels();
        for (int i = 0; i < numAnimNodes; i++) {
            AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(i));
            if (nodeName.equals(aiNodeAnim.mNodeName().dataString())) {
                result = aiNodeAnim;
                break;
            }
        }
        return result;
    }

    private static int calcAnimationMaxFrames(AIAnimation aiAnimation) {
        int maxFrames = 0;
        int numNodeAnims = aiAnimation.mNumChannels();
        PointerBuffer aiChannels = aiAnimation.mChannels();
        for (int i = 0; i < numNodeAnims; i++) {
            AINodeAnim aiNodeAnim = AINodeAnim.create(aiChannels.get(i));
            int numFrames = Math.max(Math.max(aiNodeAnim.mNumPositionKeys(), aiNodeAnim.mNumScalingKeys()),
                    aiNodeAnim.mNumRotationKeys());
            maxFrames = Math.max(maxFrames, numFrames);
        }

        return maxFrames;
    }

    private static MeshData processMesh(AIMesh aiMesh, List<Bone> bones) {
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        AIVector3D.Buffer aiTangents = aiMesh.mTangents();
        AIVector3D.Buffer aiBitangents = aiMesh.mBitangents();
        AIVector3D.Buffer aiTextureCoords = aiMesh.mTextureCoords(0);
        AIFace.Buffer aiFaces = aiMesh.mFaces();

        AnimationMeshData animationMeshData = processBones(aiMesh, bones);

        float[] vertices = new float[aiVertices.remaining() * 3];
        float[] normals = new float[aiNormals.remaining() * 3];
        float[] tangents = new float[aiTangents.remaining() * 3];
        float[] bitangents = new float[aiBitangents.remaining() * 3];
        float[] textureCoords = new float[aiTextureCoords.remaining() * 2];

        int faceCount = aiMesh.mNumFaces();

        int position = 0;
        while(aiVertices.remaining() > 0) {
            AIVector3D coordinate = aiVertices.get();
            vertices[position++] = coordinate.x();
            vertices[position++] = coordinate.y();
            vertices[position++] = coordinate.z();
        }

        position = 0;
        while(aiNormals.remaining() > 0) {
            AIVector3D normal = aiNormals.get();
            normals[position++] = normal.x();
            normals[position++] = normal.y();
            normals[position++] = normal.z();
        }

        position = 0;
        while(aiTangents.remaining() > 0) {
            AIVector3D tangent = aiTangents.get();
            tangents[position++] = tangent.x();
            tangents[position++] = tangent.y();
            tangents[position++] = tangent.z();
        }

        if(tangents.length == 0) {
            tangents = new float[normals.length];
        }

        position = 0;
        while(aiBitangents.remaining() > 0) {
            AIVector3D bitangent = aiBitangents.get();
            bitangents[position++] = bitangent.x();
            bitangents[position++] = bitangent.y();
            bitangents[position++] = bitangent.z();
        }

        if(bitangents.length == 0) {
            bitangents = new float[normals.length];
        }

        position = 0;
        while(aiTextureCoords.remaining() > 0) {
            AIVector3D textureCoordinate = aiTextureCoords.get();
            textureCoords[position++] = textureCoordinate.x();
            textureCoords[position++] = 1 - textureCoordinate.y();
        }

        List<Integer> indicesList = new ArrayList<>();
        for (int i = 0; i < faceCount; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer aiIndices = aiFace.mIndices();
            while (aiIndices.remaining() > 0) {
                indicesList.add(aiIndices.get());
            }
        }
        int[] indices = indicesList.stream().mapToInt(Integer::intValue).toArray();

        AIAABB aabb = aiMesh.mAABB();
        Vector3f min = new Vector3f(aabb.mMin().x(), aabb.mMin().y(), aabb.mMin().z());
        Vector3f max = new Vector3f(aabb.mMax().x(), aabb.mMax().y(), aabb.mMax().z());
        AABB aabb1 = new AABB(min, max);



        return new MeshData(vertices, normals, tangents, bitangents, textureCoords, indices, animationMeshData.boneIds, animationMeshData.weights, aabb1);
    }

    private static AnimationMeshData processBones(AIMesh aiMesh, List<Bone> boneList) {
        List<Integer> boneIds = new ArrayList<>();
        List<Float> weights = new ArrayList<>();

        Map<Integer, List<VertexWeight>> weightSet = new HashMap<>();
        int numBones = aiMesh.mNumBones();
        PointerBuffer aiBones = aiMesh.mBones();
        for (int i = 0; i < numBones; i++) {
            AIBone aiBone = AIBone.create(aiBones.get(i));
            int id = boneList.size();
            Bone bone = new Bone(id, aiBone.mName().dataString(), toMatrix(aiBone.mOffsetMatrix()));
            boneList.add(bone);
            int numWeights = aiBone.mNumWeights();
            AIVertexWeight.Buffer aiWeights = aiBone.mWeights();
            for (int j = 0; j < numWeights; j++) {
                AIVertexWeight aiWeight = aiWeights.get(j);
                VertexWeight vw = new VertexWeight(bone.boneId(), aiWeight.mVertexId(),
                        aiWeight.mWeight());
                List<VertexWeight> vertexWeightList = weightSet.get(vw.vertexId());
                if (vertexWeightList == null) {
                    vertexWeightList = new ArrayList<>();
                    weightSet.put(vw.vertexId(), vertexWeightList);
                }
                vertexWeightList.add(vw);
            }
        }

        int numVertices = aiMesh.mNumVertices();
        for (int i = 0; i < numVertices; i++) {
            List<VertexWeight> vertexWeightList = weightSet.get(i);
            int size = vertexWeightList != null ? vertexWeightList.size() : 0;
            for (int j = 0; j < 4; j++) {
                if (j < size) {
                    VertexWeight vw = vertexWeightList.get(j);
                    weights.add(vw.weight());
                    boneIds.add(vw.boneId());
                } else {
                    weights.add(0.0f);
                    boneIds.add(0);
                }
            }
        }

        return new AnimationMeshData(listFloatToArray(weights), listIntToArray(boneIds));
    }

    private static Material processMaterial(AIMaterial aiMaterial, String modelDirectory, TextureLoader loader) {
        Material material = new Material();
        try(MemoryStack stack = MemoryStack.stackPush()) {
            AIColor4D color4D = AIColor4D.create();

            int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, color4D);
            if(result == aiReturn_SUCCESS) {
                material.setAmbientColor(new ColorUtil.Color(color4D.r(), color4D.g(), color4D.b(), color4D.a()));
            }

            result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color4D);
            if(result == aiReturn_SUCCESS) {
                material.setDiffuseColor(new ColorUtil.Color(color4D.r(), color4D.g(), color4D.b(), color4D.a()));
            }

            result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, color4D);
            if(result == aiReturn_SUCCESS) {
                material.setSpecularColor(new ColorUtil.Color(color4D.r(), color4D.g(), color4D.b(), color4D.a()));
            }

            result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_EMISSIVE, aiTextureType_NONE, 0, color4D);
            if(result == aiReturn_SUCCESS) {
                material.setEmissiveColor(new ColorUtil.Color(color4D.r(), color4D.g(), color4D.b(), color4D.a()));
            }


            float reflectance = 0.0f;
            float[] shininessFactor = new float[]{0.0f};
            int[] pMax = new int[]{1};

            result = aiGetMaterialFloatArray(aiMaterial, AI_MATKEY_SHININESS_STRENGTH, aiTextureType_NONE, 0, shininessFactor, pMax);
            if(result == aiReturn_SUCCESS) {
                reflectance = shininessFactor[0];
            }

            result = aiGetMaterialFloatArray(aiMaterial, AI_MATKEY_REFLECTIVITY, aiTextureType_NONE, 0, shininessFactor, pMax);
            if(result == aiReturn_SUCCESS) {
                reflectance = shininessFactor[0];
            }
            material.setReflectance(reflectance);

            AIString aiTexturePath = AIString.calloc(stack);
            aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null, null, null, null, null, null);
            String texturePath = aiTexturePath.dataString();
            if(texturePath != null && texturePath.length() > 0) {
                String fullPath = modelDirectory + File.separator + new File(texturePath).getName();
                material.setTexturePath(fullPath);
                Texture diffuseTexture = loader.createTexture(fullPath);
                material.setTexture(diffuseTexture);
                material.setDiffuseColor(Material.DEFAULT_COLOR);
            }

            AIString aiNormalMapPath = AIString.calloc(stack);
            Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_NORMALS, 0, aiNormalMapPath, (IntBuffer) null, null, null, null, null, null);
            String normalMapPath = aiNormalMapPath.dataString();
            if(normalMapPath != null && normalMapPath.length() > 0) {
                String fullNormalPath = modelDirectory + File.separator + new File(normalMapPath).getName();
                material.setNormalMapPath(fullNormalPath);
                Texture normalTexture = loader.createTexture(fullNormalPath);
                material.setNormalMap(normalTexture);
            }

            AIString aiOcclusionMapPath = AIString.calloc(stack);
            Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_AMBIENT_OCCLUSION, 0, aiOcclusionMapPath, (IntBuffer) null, null, null, null, null, null);
            String occlusionMapPath = aiOcclusionMapPath.dataString();
            if (occlusionMapPath != null && occlusionMapPath.length() > 0) {
                String fullORMPath = modelDirectory + File.separator + new File(occlusionMapPath).getName();
                material.setORMMapPath(fullORMPath);
                Texture ormTexture = loader.createTexture(fullORMPath);
                material.setORMMap(ormTexture);
            }

            AIString aiEmissiveMapPath = AIString.calloc(stack);
            Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_EMISSIVE, 0, aiEmissiveMapPath, (IntBuffer) null, null, null, null, null, null);
            String emissiveMapPath = aiEmissiveMapPath.dataString();
            if (emissiveMapPath != null && emissiveMapPath.length() > 0) {
                String fullEmissivePath = modelDirectory + File.separator + new File(emissiveMapPath).getName();
                material.setEmissiveMapPath(fullEmissivePath);
                Texture emissiveTexture = loader.createTexture(fullEmissivePath);
                material.setEmissiveMap(emissiveTexture);
            }
            return material;
        }
    }

    private static Matrix4f toMatrix(AIMatrix4x4 aiMatrix4x4) {
        Matrix4f result = new Matrix4f();
        result.m00(aiMatrix4x4.a1());
        result.m10(aiMatrix4x4.a2());
        result.m20(aiMatrix4x4.a3());
        result.m30(aiMatrix4x4.a4());
        result.m01(aiMatrix4x4.b1());
        result.m11(aiMatrix4x4.b2());
        result.m21(aiMatrix4x4.b3());
        result.m31(aiMatrix4x4.b4());
        result.m02(aiMatrix4x4.c1());
        result.m12(aiMatrix4x4.c2());
        result.m22(aiMatrix4x4.c3());
        result.m32(aiMatrix4x4.c4());
        result.m03(aiMatrix4x4.d1());
        result.m13(aiMatrix4x4.d2());
        result.m23(aiMatrix4x4.d3());
        result.m33(aiMatrix4x4.d4());

        return result;
    }

    public static float[] listFloatToArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] floatArr = new float[size];
        for (int i = 0; i < size; i++) {
            floatArr[i] = list.get(i);
        }
        return floatArr;
    }

    public static int[] listIntToArray(List<Integer> list) {
        return list.stream().mapToInt((Integer v) -> v).toArray();
    }

    public record AnimationMeshData(float[] weights, int[] boneIds) {
    }

    private record Bone(int boneId, String boneName, Matrix4f offsetMatrix) {
    }

    private record VertexWeight(int boneId, int vertexId, float weight) {
    }
}
