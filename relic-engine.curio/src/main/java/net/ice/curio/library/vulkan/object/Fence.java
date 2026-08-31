package net.ice.curio.library.vulkan.object;

import net.ice.curio.library.vulkan.VulkanContext;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkFenceCreateInfo;

import java.nio.LongBuffer;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.VK10.*;

public class Fence {

    private final long fenceHandle;

    public Fence(VulkanContext context, boolean signaled) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkFenceCreateInfo createInfo = VkFenceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO)
                    .flags(signaled ? VK_FENCE_CREATE_SIGNALED_BIT : 0);

            LongBuffer buffer = stack.mallocLong(1);
            checkVulkan(vkCreateFence(context.getDevice().getVkDevice(), createInfo, null, buffer), "[Fence]: Failed to create fence");
            fenceHandle = buffer.get(0);
        }
    }

    public void destroyFence(VulkanContext context) {
        vkDestroyFence(context.getDevice().getVkDevice(), fenceHandle, null);
    }

    public void waitForFence(VulkanContext context) {
        vkWaitForFences(context.getDevice().getVkDevice(), fenceHandle, true, Long.MAX_VALUE);
    }

    public void reset(VulkanContext context) {
        vkResetFences(context.getDevice().getVkDevice(), fenceHandle);
    }

    public void cleanup(VulkanContext context) {
        vkDestroyFence(context.getDevice().getVkDevice(), fenceHandle, null);
    }

    long getHandle() {
        return fenceHandle;
    }
}
