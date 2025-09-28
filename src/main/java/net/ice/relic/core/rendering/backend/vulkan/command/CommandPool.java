package net.ice.relic.core.rendering.backend.vulkan.command;

import net.ice.relic.core.rendering.backend.vulkan.device.Device;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.tinylog.Logger;

import java.nio.LongBuffer;

import static net.ice.relic.core.rendering.backend.vulkan.VulkanUtil.checkVulkan;
import static org.lwjgl.vulkan.VK10.*;

public class CommandPool {

    private long commandPoolHandle;

    public CommandPool() {}

    public CommandPool init(Device device) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer handleBuffer = stack.mallocLong(1);

            VkCommandPoolCreateInfo poolCreateInfo = VkCommandPoolCreateInfo
                    .create()
                    .sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO);

            checkVulkan(vkCreateCommandPool(device.getDevice(), poolCreateInfo, null, handleBuffer), "Vulkan: Failed to create command pool.");
            this.commandPoolHandle = handleBuffer.get(0);
        }
        return this;
    }

    public void reset(Device device) {
        vkResetCommandPool(device.getDevice(), commandPoolHandle, 0);
    }

    public void cleanup(Device device) {
        Logger.info("Vulkan: Destroying command pool.");
        vkDestroyCommandPool(device.getDevice(), commandPoolHandle, null);
    }

    public long getCommandPoolHandle() {
        return commandPoolHandle;
    }


}
