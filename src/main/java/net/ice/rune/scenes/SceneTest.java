package net.ice.rune.scenes;

import net.ice.relic.engine.opengl.model.Model;
import net.ice.relic.engine.opengl.scene.IScene;
import net.ice.relic.engine.opengl.scene.Scene;

import java.util.HashMap;

import static net.ice.relic.engine.opengl.model.Model.DEFAULT_FLAGS;

public class SceneTest extends Scene {

    @Override
    public HashMap<String, Model> initModels(HashMap<String, Model> modelMap) {
        modelMap.put("test", new Model("resources/models/EmissiveStrengthTest.gltf", DEFAULT_FLAGS));

        return modelMap;
    }

}
