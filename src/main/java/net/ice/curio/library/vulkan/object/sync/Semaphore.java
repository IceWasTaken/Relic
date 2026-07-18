package net.ice.curio.library.vulkan.object.sync;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;

import java.nio.LongBuffer;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.vkCreateSemaphore;

public class Semaphore {

    private final long semaphoreHandle;

    public Semaphore(VkDevice device) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkSemaphoreCreateInfo semaphoreCreateInfo = VkSemaphoreCreateInfo.calloc(stack).sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);

            LongBuffer longBuffer = stack.mallocLong(1);
            checkVulkan(vkCreateSemaphore(device, semaphoreCreateInfo, null, longBuffer), "Semaphore: Failed to create semaphore");
            this.semaphoreHandle = longBuffer.get(0);
        }
    }
}
