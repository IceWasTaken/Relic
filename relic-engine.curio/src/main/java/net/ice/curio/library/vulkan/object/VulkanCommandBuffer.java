package net.ice.curio.library.vulkan.object;

import net.ice.curio.graphics.CommandBuffer;
import net.ice.curio.graphics.object.pipeline.Pipeline;
import net.ice.curio.library.vulkan.VulkanContext;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK13.vkCmdBeginRendering;

public class VulkanCommandBuffer extends CommandBuffer {

    private final boolean isPrimary;
    private final boolean oneTimeUse;

    private final VkCommandBuffer vkCommandBuffer;

    public VulkanCommandBuffer(VulkanContext context, CommandPool pool, boolean isPrimary, boolean oneTimeUse) {
        Logger.debug("[VulkanCommandBuffer]: Creating new {} command buffer", isPrimary ? "primary" : "secondary");

        this.isPrimary = isPrimary;
        this.oneTimeUse = oneTimeUse;

        VkDevice vkDevice = context.getDevice().getVkDevice();

        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.calloc(stack)
                    .sType$Default()
                    .commandPool(pool.getCommandPoolHandle())
                    .level(isPrimary ? VK_COMMAND_BUFFER_LEVEL_PRIMARY : VK_COMMAND_BUFFER_LEVEL_SECONDARY)
                    .commandBufferCount(1);

            PointerBuffer pb = stack.mallocPointer(1);
            checkVulkan(
                    vkAllocateCommandBuffers(
                            vkDevice,
                            allocInfo,
                            pb
                    ),
                    "[VulkanCommandBuffer]: Failed to allocate command buffer"
            );

            vkCommandBuffer = new VkCommandBuffer(pb.get(0), vkDevice);
        }
    }

    public void beginRecording() {
        beginRecording(null);
    }

    public void beginRecording(InheritanceInfo inheritanceInfo) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferBeginInfo commandBufferInfo = VkCommandBufferBeginInfo.calloc(stack).sType$Default();

            if(oneTimeUse) {
                commandBufferInfo.flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
            }
            if(!isPrimary) {
                if(inheritanceInfo == null) {
                    throw new RuntimeException("[VulkanCommandBuffer]: Secondary buffers must have inheritance info");
                }

                IntBuffer colorFormats = stack.callocInt(inheritanceInfo.colorFormats.length);

                for(int i : inheritanceInfo.colorFormats) {
                    colorFormats.put(0, i);
                }

                VkCommandBufferInheritanceRenderingInfo renderingInfo = VkCommandBufferInheritanceRenderingInfo.calloc(stack)
                        .sType$Default()
                        .depthAttachmentFormat(inheritanceInfo.depthFormat)
                        .pColorAttachmentFormats(colorFormats)
                        .rasterizationSamples(inheritanceInfo.rasterizationSamples);

                VkCommandBufferInheritanceInfo vkInheritanceInfo =VkCommandBufferInheritanceInfo.calloc(stack)
                        .sType$Default()
                        .pNext(renderingInfo);

                commandBufferInfo.pInheritanceInfo(vkInheritanceInfo);
            }

            checkVulkan(
                    vkBeginCommandBuffer(
                            vkCommandBuffer,
                            commandBufferInfo
                    ),
                    "[VulkanCommandBuffer]: Failed to begin command buffer"
            );
        }
    }

    public void endRecording() {
        checkVulkan(
                vkEndCommandBuffer(vkCommandBuffer),
                "[VulkanCommandBuffer]: Failed to end command buffer"
        );
    }

    public void reset() {
        vkResetCommandBuffer(vkCommandBuffer, VK_COMMAND_BUFFER_RESET_RELEASE_RESOURCES_BIT);
    }

    public void submitAndWait(VulkanContext context, Queue queue) {
        Fence fence = new Fence(context, true);
        fence.reset(context);
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferSubmitInfo.Buffer commands = VkCommandBufferSubmitInfo.calloc(1, stack)
                    .sType$Default()
                    .commandBuffer(vkCommandBuffer);
            queue.submit(
                    commands,
                    null,
                    null,
                    fence
            );
        }
        fence.waitForFence(context);
        fence.cleanup(context);
    }

    public VkCommandBufferSubmitInfo.Buffer generateSubmitInfo(MemoryStack stack) {
        return VkCommandBufferSubmitInfo.calloc(1, stack).sType$Default().commandBuffer(vkCommandBuffer);
    }

    public void cmdBeginRendering(VkRenderingInfo renderingInfo) {
        vkCmdBeginRendering(vkCommandBuffer, renderingInfo);
    }

    public void cmdBindPipeline(VkPipeline pipeline) {
        vkCmdBindPipeline(vkCommandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline.getVkPipeline());
    }
    public void cmdSetViewport(VkViewport.Buffer viewport) {
        vkCmdSetViewport(vkCommandBuffer, 0, viewport);
    }

    public void cmdSetScissor(VkRect2D.Buffer rect2D) {
        vkCmdSetScissor(vkCommandBuffer, 0, rect2D);
    }

    public void cmdPushConstants(ByteBuffer constants, VkPipeline pipeline, int stage, int offset) {
        vkCmdPushConstants(vkCommandBuffer, pipeline.getVkPipelineLayout(), stage, offset, constants);
    }


    VkCommandBuffer getVkCommandBuffer() {
        return vkCommandBuffer;
    }

    public record InheritanceInfo(int depthFormat, int[] colorFormats, int rasterizationSamples){}
}
