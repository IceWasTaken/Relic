package net.ice.relic.core.rendering.backend.vulkan.sync;

import net.ice.relic.core.rendering.backend.vulkan.device.Device;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;

import java.nio.LongBuffer;

import static net.ice.relic.core.rendering.backend.vulkan.VulkanUtil.checkVulkan;
import static org.lwjgl.vulkan.VK10.vkCreateSemaphore;
import static org.lwjgl.vulkan.VK10.vkDestroySemaphore;

public class Semaphore  {

    private long semaphoreHandle;

    public Semaphore() {}

    public Semaphore init(Device device) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkSemaphoreCreateInfo semaphoreCreateInfo = VkSemaphoreCreateInfo.create().sType$Default();
            LongBuffer longBuffer = stack.mallocLong(1);

            checkVulkan(vkCreateSemaphore(device.getDevice(), semaphoreCreateInfo, null, longBuffer), "Vulkan: Failed to create semaphore");
            this.semaphoreHandle = longBuffer.get(0);
        }
        return this;
    }


    public void cleanup(Device device) {
        vkDestroySemaphore(device.getDevice(), semaphoreHandle, null);
    }

    public long getSemaphoreHandle() {
        return semaphoreHandle;
    }
}

