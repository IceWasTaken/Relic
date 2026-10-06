package net.ice.relic.core.cache;

import net.ice.relic.core.model.material.Material;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

public class MaterialCache {

    public static final int DEFAULT_MATERIAL_INDEX = 0;

    private final List<Material> materialList;

    public MaterialCache() {
        materialList = new ArrayList<>();
    }

    public void addMaterial(Material material) {
        materialList.add(material);
        material.setMaterialIndex(materialList.indexOf(material));
    }

    public Material getMaterial(int idx) {
        if(materialList.get(idx) == null) {
            Logger.error("Failed to get material from cache.");
        }
        return materialList.get(idx);
    }

    public List<Material> getMaterialsList() {
        return materialList;
    }
}
