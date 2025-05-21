package net.ice.relic.engine.opengl.registry;

import net.ice.relic.engine.opengl.model.Model;

import static org.lwjgl.assimp.Assimp.*;

public class ModelRegistry {

    private static final int DEFAULT_FLAGS = aiProcess_Triangulate | aiProcess_FlipUVs | aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices | aiProcess_PreTransformVertices;

    public static final Model EMISSIVE_TEST = new Model("EmissieStrengthTest.gltf", DEFAULT_FLAGS);
}
