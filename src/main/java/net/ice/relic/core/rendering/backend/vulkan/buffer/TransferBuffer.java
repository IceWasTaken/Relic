package net.ice.relic.core.rendering.backend.vulkan.buffer;

import net.ice.relic.core.rendering.backend.vulkan.command.CommandBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkBufferCopy;

import static org.lwjgl.vulkan.VK10.vkCmdCopyBuffer;

public record TransferBuffer(VkBuffer srcBuffer, VkBuffer dstBuffer) {

    public void recordTransferCommand(CommandBuffer cmd) {
        try (var stack = MemoryStack.stackPush()) {
            VkBufferCopy.Buffer copyRegion = VkBufferCopy.calloc(1, stack)
                    .srcOffset(0).dstOffset(0).size(srcBuffer.getRequestedSize());
            vkCmdCopyBuffer(cmd.getCommandBuffer(), srcBuffer.getBuffer(), dstBuffer.getBuffer(), copyRegion);
        }
    }
}
