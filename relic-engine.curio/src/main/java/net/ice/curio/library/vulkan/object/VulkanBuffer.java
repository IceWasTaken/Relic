package net.ice.curio.library.vulkan.object;

import net.ice.curio.library.vulkan.VulkanContext;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkBufferDeviceAddressInfo;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.util.vma.Vma.*;
import static org.lwjgl.vulkan.VK10.VK_SHARING_MODE_EXCLUSIVE;
import static org.lwjgl.vulkan.VK12.VK_BUFFER_USAGE_SHADER_DEVICE_ADDRESS_BIT;
import static org.lwjgl.vulkan.VK12.vkGetBufferDeviceAddress;

public final class VulkanBuffer {

	private final long allocation;
	private final long buffer;
	private final PointerBuffer pointer;
	private final long requestedSize;

	private long address;

	private ByteBuffer mappedMemory;

    public VulkanBuffer(
			VulkanContext vulkanContext,
			long size,
			int bufferUsage,
			int vmaUsage,
			int vmaFlags,
			int reqFlags
	) {
		this.requestedSize = size;

        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferCreateInfo vkBufferCreateInfo = VkBufferCreateInfo.calloc(stack)
                    .sType$Default()
                    .size(size)
                    .usage(bufferUsage)
                    .sharingMode(VK_SHARING_MODE_EXCLUSIVE);

            VmaAllocationCreateInfo allocationCreateInfo = VmaAllocationCreateInfo.calloc(stack)
                    .usage(vmaUsage)
					.flags(vmaFlags)
					.requiredFlags(reqFlags);

			PointerBuffer allocation = stack.callocPointer(1);
			LongBuffer lb = stack.mallocLong(1);
			checkVulkan(
					vmaCreateBuffer(vulkanContext.getVMAInstance().getVmaHandle(), vkBufferCreateInfo, allocationCreateInfo, lb, allocation, null),
					"[VulkanBuffer]: Failed to create buffer"
			);

			this.buffer = lb.get(0);
			this.allocation = allocation.get(0);
			pointer = MemoryUtil.memAllocPointer(1);
			if((bufferUsage & VK_BUFFER_USAGE_SHADER_DEVICE_ADDRESS_BIT) > 0) {
				this.address = getAddress(vulkanContext);
			}
        }
    }

	public ByteBuffer map(VulkanContext context) {
		if(mappedMemory == null) {
			checkVulkan(vmaMapMemory(context.getVMAInstance().getVmaHandle(), allocation, pointer), "[VulkanBuffer]: Failed to map buffer");
			mappedMemory = MemoryUtil.memByteBuffer(pointer.get(0), (int) requestedSize);
		}
		return mappedMemory;
	}

	public void unmap(VulkanContext context) {
		if(mappedMemory != null) {
			vmaUnmapMemory(context.getVMAInstance().getVmaHandle(), allocation);
			mappedMemory = null;
		}
	}



	private long getAddress(VulkanContext ctx) {
		long addr;
		try(MemoryStack stack = MemoryStack.stackPush()) {
			addr = vkGetBufferDeviceAddress(ctx.getDevice().getVkDevice(), VkBufferDeviceAddressInfo.calloc(stack).sType$Default().buffer(buffer));
		}
		return addr;
	}



}
