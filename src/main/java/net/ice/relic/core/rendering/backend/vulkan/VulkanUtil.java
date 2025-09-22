package net.ice.relic.core.rendering.backend.vulkan;

import static org.lwjgl.vulkan.VK10.VK_NOT_READY;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public class VulkanUtil {

    public static void checkVulkan(int result, String message) {
        if(result != VK_SUCCESS) {
            String errorCode = switch(result) {
                case VK_NOT_READY -> "VK_NOT_READY";
                default -> "Error Code Not Mapped.";
            };
            throw new RuntimeException(message + ": " + result + " [" + errorCode + "]");
        }
    }
}
