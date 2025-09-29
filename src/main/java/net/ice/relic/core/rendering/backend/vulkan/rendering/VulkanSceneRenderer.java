package net.ice.relic.core.rendering.backend.vulkan.rendering;

import net.ice.relic.core.rendering.backend.VertexBufferStructure;
import net.ice.relic.core.rendering.backend.vulkan.SwapChain;
import net.ice.relic.core.rendering.backend.vulkan.VulkanManager;
import net.ice.relic.core.rendering.backend.vulkan.command.CommandBuffer;
import net.ice.relic.core.rendering.backend.vulkan.model.VulkanMesh;
import net.ice.relic.core.rendering.backend.vulkan.model.VulkanModel;
import net.ice.relic.core.rendering.backend.vulkan.pipeline.Pipeline;
import net.ice.relic.core.rendering.backend.vulkan.pipeline.PipelineInfo;
import net.ice.relic.core.rendering.backend.vulkan.shader.VulkanShader;
import net.ice.relic.core.rendering.shader.ShaderCompiler;
import net.ice.relic.core.rendering.shader.ShaderType;
import net.ice.relic.core.resource.Resource;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.shaderc.Shaderc;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;
import java.util.List;

import static org.lwjgl.vulkan.KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR;
import static org.lwjgl.vulkan.KHRSynchronization2.VK_IMAGE_LAYOUT_ATTACHMENT_OPTIMAL_KHR;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK13.*;

public class VulkanSceneRenderer {

    private final Resource VERTEX_SHADER_RESOURCE_GLSL = Resource.getResourceWithDefaultNamespace("data/rendering/vulkan/shaders/scene.vert");
    private final Resource VERTEX_SHADER_RESOURCE_SPV = Resource.getResource("", "resources/compiled/shaders/scene.vert.spv");
    private final Resource FRAGMENT_SHADER_RESOURCE_GLSL = Resource.getResourceWithDefaultNamespace("data/rendering/vulkan/shaders/scene.frag");
    private final Resource FRAGMENT_SHADER_RESOURCE_SPV = Resource.getResource("", "resources/compiled/shaders/scene.frag.spv");

    private Pipeline pipeline;

    private VkRenderingInfo[] renderingInfo;
    private VkClearValue clrValueColor;
    private VkRenderingAttachmentInfo.Buffer[] attInfoColor;

    public VulkanSceneRenderer() {

    }

    public void init(VulkanManager vulkanManager) {
        this.clrValueColor = VkClearValue.calloc().color(c -> c.float32(0, 0.0f).float32(1, 0.0f).float32(2, 0.0f).float32(3, 0.0f));
        this.attInfoColor = createColorAttachmentsInfo(vulkanManager, clrValueColor);
        this.renderingInfo = createRenderInfo(vulkanManager, attInfoColor);

        VulkanShader[] shader = createShaderModules(vulkanManager);
        this.pipeline = createPipeline(vulkanManager, shader);
    }

    public void render(VulkanManager manager, CommandBuffer commandBuffer, int index, List<VulkanModel> vulkanModels) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            SwapChain swapChain = manager.getSwapChain();
            long swapChainImage = swapChain.getImageView(index).getVkImage();
            VkCommandBuffer commandHandle = commandBuffer.getCommandBuffer();

            imgBarrier(stack, commandHandle, swapChainImage,
                    VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
                    VK_PIPELINE_STAGE_2_COLOR_ATTACHMENT_OUTPUT_BIT, VK_PIPELINE_STAGE_2_COLOR_ATTACHMENT_OUTPUT_BIT,
                    VK_ACCESS_2_NONE, VK_ACCESS_2_COLOR_ATTACHMENT_WRITE_BIT,
                    VK_IMAGE_ASPECT_COLOR_BIT);

            vkCmdBeginRendering(commandBuffer.getCommandBuffer(), renderingInfo[index]);
            vkCmdBindPipeline(commandHandle, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline.getVkPipeline());

            VkExtent2D swapChainExtent = swapChain.getExtent2D();
            int width = swapChainExtent.width();
            int height = swapChainExtent.height();
            var viewport = VkViewport.calloc(1, stack)
                    .x(0)
                    .y(height)
                    .height(-height)
                    .width(width)
                    .minDepth(0.0f)
                    .maxDepth(1.0f);
            vkCmdSetViewport(commandHandle, 0, viewport);

            var scissor = VkRect2D.calloc(1, stack)
                    .extent(it -> it.width(width).height(height))
                    .offset(it -> it.x(0).y(0));
            vkCmdSetScissor(commandHandle, 0, scissor);

            LongBuffer offsets = stack.mallocLong(1).put(0, 0L);
            LongBuffer vertexBuffer = stack.mallocLong(1);

            for (VulkanModel vulkanModel : vulkanModels) {
                for (VulkanMesh mesh : vulkanModel.getVulkanMeshList()) {
                    vertexBuffer.put(0, mesh.verticesBuffer().getBuffer());
                    vkCmdBindVertexBuffers(commandHandle, 0, vertexBuffer, offsets);
                    vkCmdBindIndexBuffer(commandHandle, mesh.indicesBuffer().getBuffer(), 0, VK_INDEX_TYPE_UINT32);
                    vkCmdDrawIndexed(commandHandle, mesh.numIndices(), 1, 0, 0, 0);
                }
            }

            vkCmdEndRendering(commandHandle);

            imgBarrier(stack, commandHandle, swapChainImage,
                    VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL, VK_IMAGE_LAYOUT_PRESENT_SRC_KHR,
                    VK_PIPELINE_STAGE_2_COLOR_ATTACHMENT_OUTPUT_BIT, VK_PIPELINE_STAGE_2_BOTTOM_OF_PIPE_BIT,
                    VK_ACCESS_2_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_2_COLOR_ATTACHMENT_WRITE_BIT, VK_PIPELINE_STAGE_2_NONE,
                    VK_IMAGE_ASPECT_COLOR_BIT);
        }
    }

    private Pipeline createPipeline(VulkanManager vulkanManager, VulkanShader[] shaders) {
        VertexBufferStructure vertexBufferStructure = new VertexBufferStructure();
        PipelineInfo buildInfo = new PipelineInfo(shaders, vertexBufferStructure.getVi(),
                vulkanManager.getSurface().getSurfaceFormat().getImageFormat());
        Pipeline pipeline = new Pipeline(vulkanManager, buildInfo);
        vertexBufferStructure.cleanup();
        return pipeline;
    }

    private VulkanShader[] createShaderModules(VulkanManager vkCtx) {
        ShaderCompiler.compileShaderIfChanged(VERTEX_SHADER_RESOURCE_GLSL, VERTEX_SHADER_RESOURCE_SPV, Shaderc.shaderc_glsl_vertex_shader);
        ShaderCompiler.compileShaderIfChanged(FRAGMENT_SHADER_RESOURCE_GLSL, FRAGMENT_SHADER_RESOURCE_SPV, Shaderc.shaderc_glsl_fragment_shader);

        return new VulkanShader[]{
                new VulkanShader(vkCtx.getDevice(), VERTEX_SHADER_RESOURCE_SPV, ShaderType.VERTEX),
                new VulkanShader(vkCtx.getDevice(), FRAGMENT_SHADER_RESOURCE_SPV, ShaderType.FRAGMENT),
        };
    }

    private void imgBarrier(MemoryStack stack, VkCommandBuffer commandBuffer, long image, int oldLayout, int newLayout, long srcStage, long destinationStage, long srcAccess, long destinationAccess, int mask) {
        VkImageMemoryBarrier2.Buffer barrier = VkImageMemoryBarrier2.calloc(1, stack)
                .sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER_2)
                .oldLayout(oldLayout)
                .newLayout(newLayout)
                .srcStageMask(srcStage)
                .dstStageMask(destinationStage)
                .srcAccessMask(srcAccess)
                .dstAccessMask(destinationAccess)
                .srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                .dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
                .subresourceRange(it -> it
                        .aspectMask(mask)
                        .baseMipLevel(0)
                        .levelCount(VK_REMAINING_MIP_LEVELS)
                        .baseArrayLayer(0)
                        .layerCount(VK_REMAINING_ARRAY_LAYERS))
                .image(image);

        VkDependencyInfo dependencyInfo = VkDependencyInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_DEPENDENCY_INFO)
                .pImageMemoryBarriers(barrier);

        vkCmdPipelineBarrier2(commandBuffer, dependencyInfo);
    }

    private VkRenderingInfo[] createRenderInfo(VulkanManager vkCtx, VkRenderingAttachmentInfo.Buffer[] colorAttachments) {
        SwapChain swapChain = vkCtx.getSwapChain();
        int numImages = swapChain.getImageCount();
        var result = new VkRenderingInfo[numImages];

        try (var stack = MemoryStack.stackPush()) {
            VkExtent2D extent = swapChain.getExtent2D();
            var renderArea = VkRect2D.calloc(stack).extent(extent);

            for (int i = 0; i < numImages; ++i) {
                var renderingInfo = VkRenderingInfo.calloc()
                        .sType$Default()
                        .renderArea(renderArea)
                        .layerCount(1)
                        .pColorAttachments(colorAttachments[i]);
                result[i] = renderingInfo;
            }
        }
        return result;
    }

    private static VkRenderingAttachmentInfo.Buffer[] createColorAttachmentsInfo(VulkanManager vkCtx, VkClearValue clearValue) {
        SwapChain swapChain = vkCtx.getSwapChain();
        int numImages = swapChain.getImageCount();
        var result = new VkRenderingAttachmentInfo.Buffer[numImages];

        for (int i = 0; i < numImages; ++i) {
            var attachments = VkRenderingAttachmentInfo.calloc(1)
                    .sType$Default()
                    .imageView(swapChain.getImageView(i).getVkImageView())
                    .imageLayout(VK_IMAGE_LAYOUT_ATTACHMENT_OPTIMAL_KHR)
                    .loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR)
                    .storeOp(VK_ATTACHMENT_STORE_OP_STORE)
                    .clearValue(clearValue);
            result[i] = attachments;
        }
        return result;
    }


}
