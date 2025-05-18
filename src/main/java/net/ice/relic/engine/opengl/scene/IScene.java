package net.ice.relic.engine.opengl.scene;

import net.ice.relic.engine.opengl.model.Model;

import java.util.HashMap;
import java.util.Map;

public interface IScene {

    Map<String, Model> modelMap = new HashMap<>();

    HashMap<String, Model> initModels(HashMap<String, Model> modelMap);

}
