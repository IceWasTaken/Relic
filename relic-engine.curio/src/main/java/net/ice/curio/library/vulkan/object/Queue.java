package net.ice.curio.library.vulkan.object;

import net.ice.curio.library.vulkan.VulkanContext;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;

import java.nio.IntBuffer;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK13.vkQueueSubmit2;

public class Queue {

    private final int queueFamilyIndex;
    private final VkQueue vkQueue;

    public Queue(VulkanContext context, int queueFamilyIndex, int queueIndex) {
        Logger.info("[Queue]: Creating new queue");

        this.queueFamilyIndex = queueFamilyIndex;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer queueBuffer = stack.mallocPointer(1);
            vkGetDeviceQueue(context.getDevice().getVkDevice(), queueFamilyIndex, queueIndex, queueBuffer);
            long queue = queueBuffer.get(0);
            this.vkQueue = new VkQueue(queue, context.getDevice().getVkDevice());
        }
    }

    public void submit(
            VkCommandBufferSubmitInfo.Buffer commandBuffers,
            VkSemaphoreSubmitInfo.Buffer waitSemaphores,
            VkSemaphoreSubmitInfo.Buffer signalSemaphores,
            Fence fence
    ) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkSubmitInfo2.Buffer submitInfo = VkSubmitInfo2.calloc(1, stack)
                    .sType$Default()
                    .pCommandBufferInfos(commandBuffers)
                    .pSignalSemaphoreInfos(signalSemaphores);
            if(waitSemaphores != null) {
                submitInfo.pWaitSemaphoreInfos(waitSemaphores);
            }
            checkVulkan(
                    vkQueueSubmit2(
                            vkQueue,
                            submitInfo,
                            fence != null ? fence.getHandle() : VK_NULL_HANDLE
                    ),
                    "[Queue]: Failed to submit commands to queue"
            );
        }
    }

    public void waitIdle() {
        vkQueueWaitIdle(vkQueue);
    }

    public int getQueueFamilyIndex() {
        return queueFamilyIndex;
    }

    public static class GraphicsEnabledQueue extends Queue {

        public GraphicsEnabledQueue(VulkanContext context, int queueIndex) {
	        super(context, getQueueFamilyIndex(context), queueIndex);
		}

        private static int getQueueFamilyIndex(VulkanContext context) {
            int i = 0;
            for(VkQueueFamilyProperties properties : context.getPhysicalDevice().getVkQueueFamilyProperties()) {
                if((properties.queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {
                    return i;
                }
                i++;
            }

            throw new RuntimeException("[Queue]: Failed to get graphics-enabled queue family index");
        }
    }

    public static class PresentationQueue extends Queue {

        public PresentationQueue(VulkanContext context, int queueIndex) {
            super(context, getPresentationQueueFamilyIndex(context), queueIndex);
        }

        private static int getPresentationQueueFamilyIndex(VulkanContext context) {
            int i = 0;
            try(MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer ib = stack.mallocInt(1);

                for(VkQueueFamilyProperties properties : context.getPhysicalDevice().getVkQueueFamilyProperties()) {
                    vkGetPhysicalDeviceSurfaceSupportKHR(
                            context.getPhysicalDevice().getVkPhysicalDevice(),
                            i,
                            context.getSurface().getVkSurface(),
                            ib
                    );

                    if(ib.get(0) == VK_TRUE) {
                        return i;
                    }
                }
            }

            throw new RuntimeException("[Queue]: Failed to get presentation queue family index");
        }
    }

    VkQueue getVkQueue() {
        return vkQueue;
    }
}
