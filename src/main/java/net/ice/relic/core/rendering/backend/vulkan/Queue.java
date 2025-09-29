package net.ice.relic.core.rendering.backend.vulkan;

import net.ice.relic.core.rendering.backend.vulkan.device.Device;
import net.ice.relic.core.rendering.backend.vulkan.sync.Fence;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;

import java.nio.IntBuffer;

import static net.ice.relic.core.rendering.backend.vulkan.VulkanUtil.checkVulkan;
import static org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK13.VK_STRUCTURE_TYPE_SUBMIT_INFO_2;
import static org.lwjgl.vulkan.VK13.vkQueueSubmit2;

public class Queue {

    private final VkQueue vkQueue;

    public Queue(Device device, int queueFamilyIndex, int queueIndex) {
        Logger.info("Vulkan: Creating queue.");

        try(MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer pointerBuffer = stack.callocPointer(1);
            vkGetDeviceQueue(device.getDevice(), queueFamilyIndex, queueIndex, pointerBuffer);
            this.vkQueue = new VkQueue(pointerBuffer.get(0), device.getDevice());
        }
    }

    public void submit(VkCommandBufferSubmitInfo.Buffer commandBuffers, VkSemaphoreSubmitInfo.Buffer waitSemaphores, VkSemaphoreSubmitInfo.Buffer signalSemaphores, Fence fence) {
        try(MemoryStack stack = MemoryStack.stackPush()) {

            VkSubmitInfo2.Buffer vkSubmitInfo = VkSubmitInfo2.calloc(1, stack)
                    .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO_2)
                    .pCommandBufferInfos(commandBuffers)
                    .pSignalSemaphoreInfos(signalSemaphores);

            if(waitSemaphores != null) {
                vkSubmitInfo.pWaitSemaphoreInfos(waitSemaphores);
            }

            long fenceHandle = fence != null ? fence.getFenceHandle() : VK_NULL_HANDLE;
            checkVulkan(vkQueueSubmit2(vkQueue, vkSubmitInfo, fenceHandle), "Vulkan: Failed to submit to queue.");
        }
    }

    public VkQueue getVkQueue() {
        return vkQueue;
    }

    public static class GraphicsQueue extends Queue {

        public GraphicsQueue(Device device, int queueIndex) {
            super(device, getGraphicsQueueFamilyIndex(device), queueIndex);
        }

        private static int getGraphicsQueueFamilyIndex(Device device) {
            int index = -1;
            VkQueueFamilyProperties.Buffer queuePropsBuff = device.getPhysicalDevice().getQueueFamilies();
            int numQueuesFamilies = queuePropsBuff.capacity();
            for (int i = 0; i < numQueuesFamilies; i++) {
                VkQueueFamilyProperties props = queuePropsBuff.get(i);
                boolean graphicsQueue = (props.queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0;
                if (graphicsQueue) {
                    index = i;
                    break;
                }
            }

            if (index < 0) {
                throw new RuntimeException("Failed to get graphics Queue family index");
            }
            return index;
        }
    }

    public static class PresentQueue extends Queue {

        public PresentQueue(Device device, int queueIndex, VulkanManager vulkanManager) {
            super(device, getPresentQueueFamilyIndex(device, vulkanManager), queueIndex);
        }

        private static int getPresentQueueFamilyIndex(Device device, VulkanManager vulkanManager) {
            int index = -1;
            try (var stack = MemoryStack.stackPush()) {
                VkQueueFamilyProperties.Buffer queuePropsBuff = device.getPhysicalDevice().getQueueFamilies();
                int numQueuesFamilies = queuePropsBuff.capacity();
                IntBuffer intBuff = stack.mallocInt(1);
                for (int i = 0; i < numQueuesFamilies; i++) {
                    vkGetPhysicalDeviceSurfaceSupportKHR(device.getPhysicalDevice().getVkPhysicalDevice(), i, vulkanManager.getSurface().getSurfaceHandle(), intBuff);
                    boolean supportsPresentation = intBuff.get(0) == VK_TRUE;
                    if (supportsPresentation) {
                        index = i;
                        break;
                    }
                }
            }

            if (index < 0) {
                throw new RuntimeException("Failed to get Presentation Queue family index");
            }
            return index;
        }
    }
}
