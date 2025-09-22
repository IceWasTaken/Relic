package net.ice.relic.core.rendering.backend.vulkan.sync;

//public class Semaphore implements Cleanable {
//
//    private final long semaphoreHandle;
//
//    private final VulkanManager vulkanManager;
//
//    public Semaphore(VulkanManager vulkanManager) {
//        this.vulkanManager = vulkanManager;
//
//        try(MemoryStack stack = MemoryStack.stackPush()) {
//            VkSemaphoreCreateInfo semaphoreCreateInfo = VkSemaphoreCreateInfo.create().sType$Default();
//            LongBuffer longBuffer = stack.mallocLong(1);
//
//            checkVulkan(vkCreateSemaphore(vulkanManager.getDevice().getVkDevice(), semaphoreCreateInfo, null, longBuffer), "Vulkan: Failed to create semaphore.");
//            this.semaphoreHandle = longBuffer.get(0);
//        }
//    }
//
//    @Override
//    public void cleanup() {
//        vkDestroySemaphore(vulkanManager.getDevice().getVkDevice(), semaphoreHandle, null);
//    }
//}

