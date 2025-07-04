package net.ice.relic.engine.opengl;


import net.ice.relic.engine.opengl.model.Material;

import java.util.ArrayList;
import java.util.List;

public class MaterialCache {

    public static final int DEFAULT_MATERIAL_INDEX = 0;

    private final List<Material> materialList;

    public MaterialCache() {
        materialList = new ArrayList<>();
        Material defaultMaterial = new Material();
        materialList.add(defaultMaterial);
    }

    public void addMaterial(Material material) {
        materialList.add(material);
        material.setMaterialIndex(materialList.size() - 1);
    }

    public Material getMaterial(int idx) {
        return materialList.get(idx);
    }

    public List<Material> getMaterialsList() {
        return materialList;
    }
}
