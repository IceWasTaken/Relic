package net.ice.curio.library.vulkan.object;

import net.ice.curio.graphics.CommandBuffer;
import net.ice.curio.library.vulkan.object.context.Queue;
import net.ice.curio.library.vulkan.object.sync.Fence;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandBufferSubmitInfo;
import org.tinylog.Logger;

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK13.VK_STRUCTURE_TYPE_COMMAND_BUFFER_SUBMIT_INFO;

public abstract class VulkanCommandBuffer extends CommandBuffer {

    public abstract void submitAndWait(Device device, Queue queue);

    public static class PrimaryCommandBuffer extends VulkanCommandBuffer {

        private final VkCommandBuffer vkCommandBuffer;

        public PrimaryCommandBuffer(CommandPool pool, Device device) {
            Logger.info("[VulkanCommandBuffer]: Creating primary command buffer");

            try(MemoryStack stack = MemoryStack.stackPush()) {
                VkCommandBufferAllocateInfo commandBufferAllocateInfo = VkCommandBufferAllocateInfo.calloc(stack)
                        .sType$Default()
                        .commandPool(pool.getCommandPoolHandle())
                        .level(VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                        .commandBufferCount(1);

                PointerBuffer pb = stack.mallocPointer(1);
                device.allocateCommandBuffer(commandBufferAllocateInfo, pb);

                this.vkCommandBuffer = new VkCommandBuffer(pb.get(0), device.getVkDevice());
            }
        }

        @Override
        public void submitAndWait(Device device, Queue queue) {
            super.submitAndWait(device, queue, vkCommandBuffer);
        }
    }

    public static class SecondaryCommandBuffer extends VulkanCommandBuffer {

        private final VkCommandBuffer vkCommandBuffer;

        public SecondaryCommandBuffer(CommandPool pool, Device device) {
			Logger.info("[VulkanCommandBuffer]: Creating secondary command buffer");

            try(MemoryStack stack = MemoryStack.stackPush()) {
                VkCommandBufferAllocateInfo commandBufferAllocateInfo = VkCommandBufferAllocateInfo.calloc(stack)
                        .sType$Default()
                        .commandPool(pool.getCommandPoolHandle())
                        .level(VK_COMMAND_BUFFER_LEVEL_SECONDARY)
                        .commandBufferCount(1);

                PointerBuffer pb = stack.mallocPointer(1);
                device.allocateCommandBuffer(commandBufferAllocateInfo, pb);

                this.vkCommandBuffer = new VkCommandBuffer(pb.get(0), device.getVkDevice());
            }
        }

        @Override
        public void submitAndWait(Device device, Queue queue) {
            super.submitAndWait(device, queue, vkCommandBuffer);
        }
    }

    protected void submitAndWait(Device device, Queue queue, VkCommandBuffer commandBuffer) {
        Fence fence = device.createFence(true);
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferSubmitInfo.Buffer commands = VkCommandBufferSubmitInfo.calloc(1, stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_SUBMIT_INFO)
                    .commandBuffer(commandBuffer);
            queue.submitCommands(commands, null, null, fence);
        }
        device.waitForFence(fence);
        device.destroyFence(fence);
    }
}
