package net.ice.rune.scenes;

import net.ice.relic.engine.opengl.model.Model;
import net.ice.relic.engine.opengl.scene.Scene;
import net.ice.relic.engine.opengl.scene.SceneObject;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.assimp.Assimp.*;


public class SceneTest extends Scene {

    @Override
    public void initObjects() {
        addObject("test", new SceneObject("test", new Model("EmissiveStrengthTest.gltf", aiProcess_Triangulate | aiProcess_FlipUVs | aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices | aiProcess_PreTransformVertices)));

    }

}
