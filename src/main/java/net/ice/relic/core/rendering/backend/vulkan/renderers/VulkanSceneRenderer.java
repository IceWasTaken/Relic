package net.ice.relic.core.rendering.backend.vulkan.renderers;

import net.ice.curio.library.vulkan.VulkanContext;
import net.ice.heirloom.Lifecycle;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkClearValue;

public class VulkanSceneRenderer implements Lifecycle {

	private final VkClearValue clearColorValue;
	private final VkClearValue clearDepthValue;

	public VulkanSceneRenderer(VulkanContext ctx) {
		this.clearColorValue = VkClearValue.calloc().color(
				c -> c
						.float32(0, 0f)
						.float32(1, 0f)
						.float32(2, 0f)
						.float32(3, 0f)
		);

		this.clearDepthValue = VkClearValue.calloc().color(
				c -> c.float32(0, 1f)
		);
	}

	@Override
	public void render() {
		try(MemoryStack stack = MemoryStack.stackPush()) {

		}
	}

	@Override
	public void cleanup() {
	}
}
