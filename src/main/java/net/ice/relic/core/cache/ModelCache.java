package net.ice.relic.core.cache;

import net.ice.relic.engine.opengl.model.Model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ModelCache {

    public static final String DEFAULT_MODEL = "resources/models/default.obj";
    private final Map<String, Model> modelMap;

    public ModelCache() {
        modelMap = new HashMap<>();
    }

    public void addModel(Model model) {
        modelMap.putIfAbsent(model.getId(), model);
    }

    public Model getModel(String modelName) {
        Model model = null;
        if(modelName != null) {
            model = modelMap.get(modelName);
        }
        if(model == null) {
            model = modelMap.get(DEFAULT_MODEL);
        }

        return model;
    }

    public boolean isDefaultModel(Model model) {
        return Objects.equals(model.getId(), DEFAULT_MODEL);
    }

}
