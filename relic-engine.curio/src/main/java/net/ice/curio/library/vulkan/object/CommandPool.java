package net.ice.curio.library.vulkan.object;

import net.ice.curio.library.vulkan.VulkanContext;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.tinylog.Logger;

import java.nio.LongBuffer;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.VK10.*;

public class CommandPool {

    private final long commandPoolHandle;

    public CommandPool(VulkanContext context, int queueFamilyIndex, boolean supportReset) {
        Logger.info("[CommandPool]: Creating command pool");

        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandPoolCreateInfo commandPoolCreateInfo = VkCommandPoolCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
                    .queueFamilyIndex(queueFamilyIndex)
                    .flags(supportReset ? VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT : 0);

            LongBuffer longBuffer = stack.mallocLong(1);

            checkVulkan(
                    vkCreateCommandPool(
                            context.getDevice().getVkDevice(),
                            commandPoolCreateInfo,
                            null,
                            longBuffer
                    ),
                    "[CommandPool]: Failed to create command pool"
            );

            commandPoolHandle = longBuffer.get(0);
        }
    }

    public void cleanup(VkDevice device) {
        Logger.info("CommandPool: Destroying pool");
        vkDestroyCommandPool(device, commandPoolHandle, null);
    }

    long getCommandPoolHandle() {
        return commandPoolHandle;
    }

    public void reset(VulkanContext context) {
        vkResetCommandPool(context.getDevice().getVkDevice(), commandPoolHandle, 0);
    }
}
