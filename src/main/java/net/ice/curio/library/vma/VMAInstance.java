package net.ice.curio.library.vma;

import net.ice.curio.library.vulkan.VulkanContext;
import net.ice.heirloom.Lifecycle;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.util.vma.VmaAllocationInfo;
import org.lwjgl.util.vma.VmaAllocatorCreateInfo;
import org.lwjgl.util.vma.VmaVulkanFunctions;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkImageCreateInfo;

import java.nio.LongBuffer;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.util.vma.Vma.*;
import static org.lwjgl.vulkan.VK13.VK_API_VERSION_1_3;

///[VMA Doc](https://gpuopen-librariesandsdks.github.io/VulkanMemoryAllocator/html/group__group__init.html)
public class VMAInstance implements Lifecycle {

    private final long allocator;

    public VMAInstance(VulkanContext vulkanContext) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer allocator = stack.mallocPointer(1);

            VmaVulkanFunctions vulkanFunctions = vulkanContext.getDevice().createVMAVulkanFunctions(vulkanContext, stack);

            VmaAllocatorCreateInfo createInfo = VmaAllocatorCreateInfo.calloc(stack);
            vulkanContext.getInstance().setupVMACreateInfo(createInfo);
            createInfo.vulkanApiVersion(VK_API_VERSION_1_3);
            vulkanContext.getDevice().setupVMACreateInfo(createInfo);
            vulkanContext.getPhysicalDevice().setupVMACreateInfo(createInfo);
            createInfo.pVulkanFunctions(vulkanFunctions);

            checkVulkan(vmaCreateAllocator(createInfo, allocator), "VMAInstance: Failed to create allocator.");

            this.allocator = allocator.get(0);
        }
    }

    public void createBuffer(VkBufferCreateInfo bufferCreateInfo, VmaAllocationCreateInfo allocationCreateInfo, LongBuffer lb, PointerBuffer allocation, VmaAllocationInfo allocationInfo) {
        checkVulkan(vmaCreateBuffer(allocator, bufferCreateInfo, allocationCreateInfo, lb, allocation, allocationInfo), "VMAInstance: Failed to create buffer.");
    }

    public void createBuffer(VkBufferCreateInfo bufferCreateInfo, VmaAllocationCreateInfo allocationCreateInfo, LongBuffer lb, PointerBuffer allocation) {
        createBuffer(bufferCreateInfo, allocationCreateInfo, lb, allocation, null);
    }

    public void createImage(VkImageCreateInfo imageCreateInfo, VmaAllocationCreateInfo allocationCreateInfo, LongBuffer lb, PointerBuffer allocation) {
        checkVulkan(vmaCreateImage(allocator, imageCreateInfo, allocationCreateInfo, lb, allocation, null), "VMAInstance: Failed to create image.");
    }

    public void destroyImage(long vkImage, long allocation) {
        vmaDestroyImage(allocator, vkImage, allocation);
    }


    @Override
    public void cleanup() {
        vmaDestroyAllocator(allocator);
    }
}
