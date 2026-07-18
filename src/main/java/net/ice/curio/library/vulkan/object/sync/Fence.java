package net.ice.curio.library.vulkan.object.sync;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkFenceCreateInfo;

import java.nio.LongBuffer;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.VK10.*;

public class Fence {

    private final long fenceHandle;

    public Fence(VkDevice device, boolean signaled) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkFenceCreateInfo createInfo = VkFenceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO)
                    .flags(signaled ? VK_FENCE_CREATE_SIGNALED_BIT : 0);

            LongBuffer buffer = stack.mallocLong(1);
            checkVulkan(vkCreateFence(device, createInfo, null, buffer), "Fence: Failed to create fence");
            fenceHandle = buffer.get(0);
        }
    }

    public long getHandle() {
        return fenceHandle;
    }

    public void destroyFence(VkDevice device) {
        vkDestroyFence(device, fenceHandle, null);
    }

    public void waitForFence(VkDevice device) {
        vkWaitForFences(device, fenceHandle, true, Long.MAX_VALUE);
    }

    public void reset(VkDevice device) {
        vkResetFences(device, fenceHandle);
    }
}
