package net.ice.relic.engine.opengl.registry;

import static org.lwjgl.assimp.Assimp.*;

public class ModelRegistry {

    public static final int DEFAULT_FLAGS = aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
            aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights |
            aiProcess_GenBoundingBoxes | aiProcess_PreTransformVertices;
    //re-implement later.
//    public static final Model CUBE_TEST = ModelLoader.loadModelFromFile("test", "cube.gltf", null, DEFAULT_FLAGS);
//    public static final Model EMISSIVE_TEST = ModelLoader.loadModelFromFile("test", "EmissiveStrengthTest.gltf", null, DEFAULT_FLAGS);
}
