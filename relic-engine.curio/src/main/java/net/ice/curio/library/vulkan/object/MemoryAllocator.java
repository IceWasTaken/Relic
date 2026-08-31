package net.ice.curio.library.vulkan.object;

import net.ice.curio.library.vulkan.VulkanContext;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.VmaAllocatorCreateInfo;
import org.lwjgl.util.vma.VmaVulkanFunctions;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.util.vma.Vma.VMA_ALLOCATOR_CREATE_BUFFER_DEVICE_ADDRESS_BIT;
import static org.lwjgl.util.vma.Vma.vmaCreateAllocator;
import static org.lwjgl.vulkan.VK13.VK_API_VERSION_1_3;

public class MemoryAllocator {

	private final long vmaHandle;

	public MemoryAllocator(VulkanContext context) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			PointerBuffer alloc = stack.mallocPointer(1);

			VmaVulkanFunctions vmaVulkanFunctions = VmaVulkanFunctions.calloc(stack)
					.set(
							context.getInstance().getVkInstance(),
							context.getDevice().getVkDevice()
					);

			VmaAllocatorCreateInfo createInfo = VmaAllocatorCreateInfo.calloc(stack)
					.flags(VMA_ALLOCATOR_CREATE_BUFFER_DEVICE_ADDRESS_BIT)
					.instance(context.getInstance().getVkInstance())
					.vulkanApiVersion(VK_API_VERSION_1_3)
					.device(context.getDevice().getVkDevice())
					.physicalDevice(context.getPhysicalDevice().getVkPhysicalDevice())
					.pVulkanFunctions(vmaVulkanFunctions);

			//failed to create vulkan memory allocator allocator
			//who named this shit
			checkVulkan(vmaCreateAllocator(createInfo, alloc), "[MemoryAllocator]: Failed to create VMA allocator");

			this.vmaHandle = alloc.get(0);
		}
	}

	long getVmaHandle() {
		return vmaHandle;
	}
}
