package net.ice.relic.core.rendering.backend.vulkan.model;

import net.ice.relic.core.rendering.backend.vulkan.VulkanManager;

import java.util.ArrayList;
import java.util.List;

public class VulkanModel {

    private final String id;
    private final List<VulkanMesh> vulkanMeshList;

    public VulkanModel(String id) {
        this.id = id;
        vulkanMeshList = new ArrayList<>();
    }

    public void cleanup(VulkanManager vkCtx) {
        //vulkanMeshList.forEach(mesh -> mesh.cleanup(vkCtx));
    }

    public String getId() {
        return id;
    }

    public List<VulkanMesh> getVulkanMeshList() {
        return vulkanMeshList;
    }
}