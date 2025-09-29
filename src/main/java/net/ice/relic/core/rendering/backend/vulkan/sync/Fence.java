package net.ice.relic.core.rendering.backend.vulkan.sync;

import net.ice.relic.core.rendering.backend.vulkan.device.Device;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkFenceCreateInfo;

import java.nio.LongBuffer;

import static net.ice.relic.core.rendering.backend.vulkan.VulkanUtil.checkVulkan;
import static org.lwjgl.vulkan.VK10.*;

public class Fence {

    private long fenceHandle;

    public Fence() {}

    public Fence init(Device device, boolean signaled) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer handleBuffer = stack.callocLong(1);

            VkFenceCreateInfo fenceCreateInfo = VkFenceCreateInfo.create()
                    .sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO)
                    .flags(signaled ? VK_FENCE_CREATE_SIGNALED_BIT : 0);


            checkVulkan(vkCreateFence(device.getDevice(), fenceCreateInfo, null, handleBuffer), "Vulkan: Failed to create fence.");

            this.fenceHandle = handleBuffer.get(0);
        }
        return this;
    }

    public void fenceWait(Device device) {
        vkWaitForFences(device.getDevice(), fenceHandle, true, Long.MAX_VALUE);
    }

    public void reset(Device device) {
        vkResetFences(device.getDevice(), fenceHandle);
    }

    public void cleanup(Device device) {
        vkDestroyFence(device.getDevice(), fenceHandle, null);
    }

    public long getFenceHandle() {
        return fenceHandle;
    }
}
