package net.ice.curio.library.vulkan.object;

import net.ice.curio.library.vulkan.VulkanContext;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineCacheCreateInfo;
import org.tinylog.Logger;

import java.nio.LongBuffer;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.VK10.vkCreatePipelineCache;
import static org.lwjgl.vulkan.VK10.vkDestroyPipelineCache;

public class PipelineCache {
	private final long vkPipelineCache;

	public PipelineCache(VulkanContext context) {
		Logger.debug("[PipelineCache]: Creating new pipeline cache");
		try (MemoryStack stack = MemoryStack.stackPush()) {
			VkPipelineCacheCreateInfo createInfo = VkPipelineCacheCreateInfo.calloc(stack).sType$Default();

			LongBuffer handle = stack.mallocLong(1);
			checkVulkan(
					vkCreatePipelineCache(context.getDevice().getVkDevice(), createInfo, null, handle),
					"[PipelineCache]: Error while creating pipeline cache"
			);
			vkPipelineCache = handle.get(0);
		}
	}

	public void cleanup(VulkanContext context) {
		Logger.debug("[PipelineCache]: Destroying pipeline cache");
		vkDestroyPipelineCache(context.getDevice().getVkDevice(), vkPipelineCache, null);
	}

	long getVkPipelineCache() {
		return vkPipelineCache;
	}
}
