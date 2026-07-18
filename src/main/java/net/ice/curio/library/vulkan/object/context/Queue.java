package net.ice.curio.library.vulkan.object.context;

import net.ice.curio.library.vulkan.object.sync.Fence;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.VK10.vkGetDeviceQueue;
import static org.lwjgl.vulkan.VK10.vkQueueWaitIdle;
import static org.lwjgl.vulkan.VK13.VK_STRUCTURE_TYPE_SUBMIT_INFO_2;
import static org.lwjgl.vulkan.VK13.vkQueueSubmit2;

public class Queue {

    private final int queueFamilyIndex;
    private final VkQueue vkQueue;

    public Queue(VkDevice device, int queueFamilyIndex, int queueIndex) {
        Logger.info("Queue: Creating new queue with index [{}], family index [{}]", queueFamilyIndex, queueIndex);

        this.queueFamilyIndex = queueFamilyIndex;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer queueBuffer = stack.mallocPointer(1);
            vkGetDeviceQueue(device, queueFamilyIndex, queueIndex, queueBuffer);
            long queue = queueBuffer.get(0);
            this.vkQueue = new VkQueue(queue, device);
        }
    }

    public void waitIdle() {
        vkQueueWaitIdle(vkQueue);
    }

    public void submit(
            VkCommandBufferSubmitInfo.Buffer commandBuffers,
            VkSemaphoreSubmitInfo.Buffer waitSemaphores,
            VkSemaphoreSubmitInfo.Buffer signalSemaphores,
            Fence fence
    ) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkSubmitInfo2.Buffer submitInfo = VkSubmitInfo2.calloc(1, stack)
                    .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO_2)
                    .pCommandBufferInfos(commandBuffers)
                    .pSignalSemaphoreInfos(signalSemaphores);
            if(waitSemaphores != null) {
                submitInfo.pWaitSemaphoreInfos(waitSemaphores);
            }

            checkVulkan(vkQueueSubmit2(vkQueue, submitInfo, fence.getHandle()), "Queue: Failed to submit command to queue");
        }
    }

    public int getQueueFamilyIndex() {
        return queueFamilyIndex;
    }
}
