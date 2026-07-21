package net.ice.curio.library.vulkan.object.sync;

import net.ice.curio.library.vulkan.object.context.Device;
import net.ice.curio.library.vulkan.object.context.Queue;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandBufferSubmitInfo;
import org.lwjgl.vulkan.VkDevice;
import org.tinylog.Logger;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK13.VK_STRUCTURE_TYPE_COMMAND_BUFFER_SUBMIT_INFO;

public class CommandBuffer {

    private final VkCommandBuffer vkCommandBuffer;

    private final boolean primary;
    private final boolean singleUse;

    public CommandBuffer(VkDevice device, CommandPool commandPool, boolean primary, boolean singleUse) {
        Logger.info("CommandBuffer: Creating command buffer");
        this.primary = primary;
        this.singleUse = singleUse;

        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferAllocateInfo commandBufferAllocateInfo = VkCommandBufferAllocateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                    .level(primary ? VK_COMMAND_BUFFER_LEVEL_PRIMARY : VK_COMMAND_BUFFER_LEVEL_SECONDARY)
                    .commandBufferCount(1);

            commandPool.commandBufferAlloc(commandBufferAllocateInfo);

            PointerBuffer pb = stack.mallocPointer(1);
            checkVulkan(vkAllocateCommandBuffers(device, commandBufferAllocateInfo, pb), "CommandBuffer: Failed to allocate command buffer");
            vkCommandBuffer = new VkCommandBuffer(pb.get(0), device);
        }
    }

    public void submitAndWait(Device device, Queue queue) {
        Fence fence = device.createFence(true);
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferSubmitInfo.Buffer commands = VkCommandBufferSubmitInfo.calloc(1, stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_SUBMIT_INFO)
                    .commandBuffer(vkCommandBuffer);
            queue.submit(commands, null, null, fence);
        }
        device.waitForFence(fence);
        device.destroyFence(fence);
    }
}
