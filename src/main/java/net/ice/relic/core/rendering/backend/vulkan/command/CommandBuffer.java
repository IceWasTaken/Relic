package net.ice.relic.core.rendering.backend.vulkan.command;

import net.ice.relic.core.rendering.backend.vulkan.Queue;
import net.ice.relic.core.rendering.backend.vulkan.VulkanManager;
import net.ice.relic.core.rendering.backend.vulkan.device.Device;
import net.ice.relic.core.rendering.backend.vulkan.sync.Fence;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;

import java.nio.IntBuffer;

import static net.ice.relic.core.rendering.backend.vulkan.VulkanUtil.checkVulkan;
import static org.lwjgl.vulkan.VK10.*;

public class CommandBuffer {

    private final boolean primary;
    private final boolean oneTimeSubmit;

    private VkCommandBuffer commandBuffer;

    public CommandBuffer(boolean primary, boolean oneTimeSubmit) {
        this.primary = primary;
        this.oneTimeSubmit = oneTimeSubmit;

    }

    public CommandBuffer init(Device device, CommandPool commandPool) {
        Logger.info("Vulkan: Creating command buffer.");

        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferAllocateInfo allocateInfo = VkCommandBufferAllocateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                    .commandPool(commandPool.getCommandPoolHandle())
                    .level(primary ? VK_COMMAND_BUFFER_LEVEL_PRIMARY : VK_COMMAND_BUFFER_LEVEL_SECONDARY)
                    //I Guess? "pAllocateInfo->commandBufferCount must be greater than 0." So 1 would work?
                    .commandBufferCount(1);

            PointerBuffer commandBuffer = stack.mallocPointer(1);
            checkVulkan(vkAllocateCommandBuffers(device.getDevice(), allocateInfo, commandBuffer), "Vulkan: Failed to allocate command buffer.");

            this.commandBuffer = new VkCommandBuffer(commandBuffer.get(0), device.getDevice());
        }
        return this;
    }

    public void record() {
        this.record(null);
    }

    public void record(InheritanceInfo inheritanceInfo) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferBeginInfo bufferBeginInfo = VkCommandBufferBeginInfo.calloc(stack).sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
            if(oneTimeSubmit) {
                bufferBeginInfo.flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
            }
            if (!primary) {
                if (inheritanceInfo == null) {
                    throw new RuntimeException("Secondary buffers must declare inheritance info");
                }
                int numColorFormats = inheritanceInfo.colorFormats.length;
                IntBuffer pColorFormats = stack.callocInt(inheritanceInfo.colorFormats.length);
                for (int i = 0; i < numColorFormats; i++) {
                    pColorFormats.put(0, inheritanceInfo.colorFormats[i]);
                }
                var renderingInfo = VkCommandBufferInheritanceRenderingInfo.calloc(stack)
                        .sType$Default()
                        .depthAttachmentFormat(inheritanceInfo.depthFormat)
                        .pColorAttachmentFormats(pColorFormats)
                        .rasterizationSamples(inheritanceInfo.rasterizationSamples);
                var vkInheritanceInfo = VkCommandBufferInheritanceInfo.calloc(stack)
                        .sType$Default()
                        .pNext(renderingInfo);
                bufferBeginInfo.pInheritanceInfo(vkInheritanceInfo);
            }
            checkVulkan(vkBeginCommandBuffer(commandBuffer, bufferBeginInfo), "Vulkan: Failed to begin command buffer");

        }
    }

    public void submitAndWait(VulkanManager vkCtx, Queue queue) {
        Fence fence = new Fence().init(vkCtx.getDevice(), true);
        fence.reset(vkCtx.getDevice());
        try (var stack = MemoryStack.stackPush()) {
            var cmds = VkCommandBufferSubmitInfo.calloc(1, stack)
                    .sType$Default()
                    .commandBuffer(commandBuffer);
            queue.submit(cmds, null, null, fence);
        }
        fence.fenceWait(vkCtx.getDevice());
        fence.cleanup(vkCtx.getDevice());
    }

    public void stopRecording() {
        checkVulkan(vkEndCommandBuffer(commandBuffer), "Vulkan: Failed to end command buffer");
    }

    public void reset() {
        vkResetCommandBuffer(commandBuffer, VK_COMMAND_BUFFER_RESET_RELEASE_RESOURCES_BIT);
    }

    public VkCommandBuffer getCommandBuffer() {
        return commandBuffer;
    }

    public record InheritanceInfo(int depthFormat, int[] colorFormats, int rasterizationSamples) {
    }
}

