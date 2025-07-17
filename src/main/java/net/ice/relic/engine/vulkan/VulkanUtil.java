package net.ice.relic.engine.vulkan;

import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public class VulkanUtil {

    public static void checkVulkanResult(int result, String message) {
        if(result != VK_SUCCESS) {
            throw new RuntimeException(message + ": " + result);
        }
    }
}
