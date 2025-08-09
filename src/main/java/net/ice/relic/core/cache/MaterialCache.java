package net.ice.relic.core.cache;


import net.ice.relic.core.model.Material;

import java.util.ArrayList;
import java.util.List;

public class MaterialCache {

    public static final int DEFAULT_MATERIAL_INDEX = 0;

    private final List<Material> materialList;

    public MaterialCache() {
        materialList = new ArrayList<>();
        Material defaultMaterial = new Material();
        defaultMaterial.setMaterialIndex(DEFAULT_MATERIAL_INDEX);
        defaultMaterial.setTexturePath("resources/textures/default.png");
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
