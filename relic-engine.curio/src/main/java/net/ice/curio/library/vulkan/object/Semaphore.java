package net.ice.curio.library.vulkan.object;

import net.ice.curio.library.vulkan.VulkanContext;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;
import org.lwjgl.vulkan.VkSemaphoreSubmitInfo;

import java.nio.LongBuffer;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.VK10.vkCreateSemaphore;
import static org.lwjgl.vulkan.VK10.vkDestroySemaphore;

public class Semaphore {

	private final long vkSemaphore;

	public Semaphore(VulkanContext context) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			VkSemaphoreCreateInfo semaphoreCreateInfo = VkSemaphoreCreateInfo.calloc(stack).sType$Default();

			LongBuffer lb = stack.mallocLong(1);
			checkVulkan(
					vkCreateSemaphore(
							context.getDevice().getVkDevice(),
							semaphoreCreateInfo,
							null,
							lb
					),
					"[Semaphore]: Failed to create semaphore"
			);

			this.vkSemaphore = lb.get(0);
		}
	}

	public VkSemaphoreSubmitInfo.Buffer generateSubmitInfo(MemoryStack stack, long stageMask) {
		return VkSemaphoreSubmitInfo.calloc(1, stack).sType$Default().stageMask(stageMask).semaphore(vkSemaphore);
	}

	public void cleanup(VulkanContext context) {
		vkDestroySemaphore(context.getDevice().getVkDevice(), vkSemaphore, null);
	}

	long getVkSemaphore() {
		return vkSemaphore;
	}
}
