package net.ice.relic.core.rendering.backend.vulkan.model;

import net.ice.relic.core.rendering.backend.vulkan.VulkanManager;
import net.ice.relic.core.rendering.backend.vulkan.buffer.VkBuffer;

public record VulkanMesh(VkBuffer verticesBuffer, VkBuffer indicesBuffer, int numIndices) {
    public void cleanup(VulkanManager vkCtx) {
        verticesBuffer.cleanup(vkCtx);
        indicesBuffer.cleanup(vkCtx);
    }
}
