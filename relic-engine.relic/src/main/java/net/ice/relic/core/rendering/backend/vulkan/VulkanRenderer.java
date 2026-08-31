package net.ice.relic.core.rendering.backend.vulkan;

import net.ice.curio.graphics.CommandBuffer;
import net.ice.curio.library.vulkan.VulkanContext;
import net.ice.curio.library.vulkan.object.*;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.rendering.backend.Renderer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandBufferSubmitInfo;
import org.lwjgl.vulkan.VkSemaphoreSubmitInfo;

import static org.lwjgl.vulkan.VK13.VK_PIPELINE_STAGE_2_BOTTOM_OF_PIPE_BIT;
import static org.lwjgl.vulkan.VK13.VK_PIPELINE_STAGE_2_COLOR_ATTACHMENT_OUTPUT_BIT;

public class VulkanRenderer extends Renderer implements Lifecycle {

    public static final int FRAMES_IN_FLIGHT = 2;

    private final CommandPool[] commandPools;
    private final VulkanCommandBuffer[] commandBuffers;
    private final Semaphore[] presentationCompleteSemaphores;
    private final Semaphore[] renderCompleteSemaphores;
    private final Fence[] fences;

    private final Queue.GraphicsEnabledQueue graphicsEnabledQueue;
    private final Queue.PresentationQueue presentationQueue;

    private int currentFrame;

    public VulkanRenderer(RelicApplication relicApplication) {
        super(relicApplication);

        VulkanContext context = (VulkanContext) relicApplication.getCurio().getGraphicsContext();

        this.currentFrame = 0;

        this.graphicsEnabledQueue = new Queue.GraphicsEnabledQueue(context, 0);
        this.presentationQueue = new Queue.PresentationQueue(context, 0);

        this.commandPools = new CommandPool[FRAMES_IN_FLIGHT];
        this.commandBuffers = new VulkanCommandBuffer[FRAMES_IN_FLIGHT];
        this.presentationCompleteSemaphores = new Semaphore[FRAMES_IN_FLIGHT];
        this.renderCompleteSemaphores = new Semaphore[context.getSwapChain().getImageCount()];
        this.fences = new Fence[FRAMES_IN_FLIGHT];

        for (int i = 0; i < FRAMES_IN_FLIGHT; i++) {
            commandPools[i] = new CommandPool(context, graphicsEnabledQueue.getQueueFamilyIndex(), false);
            commandBuffers[i] = new VulkanCommandBuffer(context, commandPools[i], true, true);
            fences[i] = new Fence(context, true);
            presentationCompleteSemaphores[i] = new Semaphore(context);
        }

        for (int i = 0; i < context.getSwapChain().getImageCount(); i++) {
            renderCompleteSemaphores[i] = new Semaphore(context);
        }


    }

    @Override
    public void render() {
        VulkanContext context = (VulkanContext) application.getCurio().getGraphicsContext();

        SwapChain swapChain = context.getSwapChain();

        fences[currentFrame].waitForFence(context);

        //begin recording
        commandPools[currentFrame].reset(context);
        commandBuffers[currentFrame].beginRecording();

        int imageIndex = swapChain.acquireNextImage(context, presentationCompleteSemaphores[currentFrame]);
        if(imageIndex < 0) {
            return;
        }

        commandBuffers[currentFrame].endRecording();

        //submit commands
        try(MemoryStack stack = MemoryStack.stackPush()) {
            Fence fence = fences[currentFrame];
            fence.reset(context);

            VkCommandBufferSubmitInfo.Buffer commands =
                    commandBuffers[currentFrame].generateSubmitInfo(stack);
            VkSemaphoreSubmitInfo.Buffer waitSemaphores =
                    presentationCompleteSemaphores[currentFrame].generateSubmitInfo(stack, VK_PIPELINE_STAGE_2_COLOR_ATTACHMENT_OUTPUT_BIT);
            VkSemaphoreSubmitInfo.Buffer signalSemaphores =
                    renderCompleteSemaphores[imageIndex].generateSubmitInfo(stack, VK_PIPELINE_STAGE_2_BOTTOM_OF_PIPE_BIT);

            graphicsEnabledQueue.submit(commands, waitSemaphores, signalSemaphores, fence);
        }

        swapChain.presentImage(presentationQueue, renderCompleteSemaphores[imageIndex], imageIndex);

        currentFrame = (currentFrame + 1) % FRAMES_IN_FLIGHT;
    }

    @Override
    public void resize(int width, int height) {

    }
}
