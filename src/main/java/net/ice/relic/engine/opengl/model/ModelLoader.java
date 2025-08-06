package net.ice.relic.engine.opengl.model;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.common.cache.MaterialCache;
import net.ice.relic.common.cache.ModelCache;
import net.ice.relic.common.model.Material;
import net.ice.relic.common.cache.TextureCache;
import net.ice.relic.common.model.MeshData;
import org.joml.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.File;
import java.lang.Math;
import java.nio.IntBuffer;
import java.util.*;

import static net.ice.relic.common.model.Material.processMaterial;
import static net.ice.relic.util.AssimpUtil.*;
import static net.ice.relic.util.IOUtil.loadRealFile;
import static net.ice.relic.util.MatrixUtil.toMatrix4f;
import static org.lwjgl.assimp.Assimp.*;

public class ModelLoader {

    public static final int MAX_BONES = 150;
    private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();

    private TextureCache textureCache;
    private MaterialCache materialCache;
    private ModelCache modelCache;

    public ModelLoader(RelicApplication relicApplication, TextureCache textureCache, MaterialCache materialCache, ModelCache modelCache) {
        this.textureCache = textureCache;
        this.materialCache = materialCache;
        this.modelCache = modelCache;
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

    private static Node buildNodesTree(AINode aiNode, Node parentNode) {
        String nodeName = aiNode.mName().dataString();
        Node node = new Node(nodeName, parentNode, toMatrix4f(aiNode.mTransformation()));

        int numChildren = aiNode.mNumChildren();
        PointerBuffer aiChildren = aiNode.mChildren();
        for (int i = 0; i < numChildren; i++) {
            AINode aiChildNode = AINode.create(aiChildren.get(i));
            Node childNode = buildNodesTree(aiChildNode, node);
            node.addChild(childNode);
        }
        return node;
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

    public Model loadModel(String modelId, String modelPath, boolean animation) {
        return loadModel(modelId, modelPath, textureCache, materialCache, modelCache, aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
                aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights |
                aiProcess_GenBoundingBoxes | (animation ? 0 : aiProcess_PreTransformVertices));
    }

    public Model loadModel(String modelId, String modelPath, TextureCache textureCache, MaterialCache materialCache, ModelCache cache, int flags) {
        File file = loadRealFile("resources", "models", modelPath);
        String modelDir = file.getParent();

        AIScene aiScene = aiImportFile("resources/models/" + modelPath, flags);
        if (aiScene == null) {
            throw new RuntimeException("Error loading model [" + modelPath + "]");
        }

        int numMaterials = aiScene.mNumMaterials();
        int numMeshes = aiScene.mNumMeshes();

        PointerBuffer aiMeshes = aiScene.mMeshes();

        List<Material> materialList = new ArrayList<>();
        List<MeshData> meshDataList = new ArrayList<>();
        List<Bone> boneList = new ArrayList<>();

        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
            Material material = processMaterial(aiMaterial, modelDir, textureCache);
            materialCache.addMaterial(material);
            materialList.add(material);
        }

        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            MeshData meshData = processMesh(aiMesh, boneList);
            int materialIdx = aiMesh.mMaterialIndex();
            if (materialIdx >= 0 && materialIdx < materialList.size()) {
                meshData.setMaterialIndex(materialList.get(materialIdx).getMaterialIndex());
            } else {
                meshData.setMaterialIndex(MaterialCache.DEFAULT_MATERIAL_INDEX);
            }
            meshDataList.add(meshData);
        }

        List<Animation> animations = new ArrayList<>();
        int numAnimations = aiScene.mNumAnimations();
        if (numAnimations > 0) {
            Node rootNode = buildNodesTree(aiScene.mRootNode(), null);
            Matrix4f globalInverseTransformation = toMatrix4f(aiScene.mRootNode().mTransformation()).invert();
            animations = processAnimations(aiScene, boneList, rootNode, globalInverseTransformation);
        }

        aiReleaseImport(aiScene);

        cache.addModel(new Model(modelId, meshDataList, animations));

        return new Model(modelId, meshDataList, animations);
    }



    private static List<Animation> processAnimations(AIScene aiScene, List<Bone> boneList, Node rootNode, Matrix4f globalInverseTransformation) {
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

    private static AnimMeshData processBones(AIMesh aiMesh, List<Bone> boneList) {
        List<Integer> boneIds = new ArrayList<>();
        List<Float> weights = new ArrayList<>();

        Map<Integer, List<VertexWeight>> weightSet = new HashMap<>();
        int numBones = aiMesh.mNumBones();
        PointerBuffer aiBones = aiMesh.mBones();
        for (int i = 0; i < numBones; i++) {
            AIBone aiBone = AIBone.create(aiBones.get(i));
            int id = boneList.size();
            Bone bone = new Bone(id, aiBone.mName().dataString(), toMatrix4f(aiBone.mOffsetMatrix()));
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
            for (int j = 0; j < Mesh.MAX_WEIGHTS; j++) {
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

        return new AnimMeshData(listFloatToArray(weights), listIntToArray(boneIds));
    }



    private static MeshData processMesh(AIMesh aiMesh, List<Bone> boneList) {
        float[] vertices = processAIVectorBuffer(aiMesh.mVertices());
        float[] normals = processAIVectorBuffer(aiMesh.mNormals());
        float[] tangents = processAIVectorBufferBackup(aiMesh.mTangents(), normals);
        float[] bitangents = processAIVectorBufferBackup(aiMesh.mBitangents(), normals);
        float[] textCoords = aiMesh.mTextureCoords(0) != null ? processTextCoords(aiMesh.mTextureCoords(0)) : new float[]{};
        int[] indices = processIndices(aiMesh);
        AnimMeshData animMeshData = processBones(aiMesh, boneList);

        // Texture coordinates may not have been populated. We need at least the empty slots
        if (textCoords.length == 0) {
            int numElements = (vertices.length / 3) * 2;
            textCoords = new float[numElements];
        }

        AIAABB aabb = aiMesh.mAABB();
        Vector3f aabbMin = new Vector3f(aabb.mMin().x(), aabb.mMin().y(), aabb.mMin().z());
        Vector3f aabbMax = new Vector3f(aabb.mMax().x(), aabb.mMax().y(), aabb.mMax().z());

        return new MeshData(vertices, normals, tangents, bitangents, textCoords, indices, animMeshData.boneIds,
                animMeshData.weights, aabbMin, aabbMax);
    }

    public record AnimMeshData(float[] weights, int[] boneIds) {
    }

    private record Bone(int boneId, String boneName, Matrix4f offsetMatrix) {
    }

    private record VertexWeight(int boneId, int vertexId, float weight) {
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

    public ModelCache getModelCache() {
        return modelCache;
    }

    public TextureCache getTextureCache() {
        return textureCache;
    }

    public MaterialCache getMaterialCache() {
        return materialCache;
    }
}
