package net.ice.curio.library.vulkan.utils;

import net.ice.curio.graphics.exception.OutOfGraphicsMemoryException;

import static org.lwjgl.vulkan.VK10.*;

public class VulkanUtils {

    public static void checkVulkan(int vkResult, String message) {
//        if (vkResult != 0) {
//            String errCode = switch (vkResult) {
//                case VK_NOT_READY -> "VK_NOT_READY";
//                case VK_TIMEOUT -> "VK_TIMEOUT";
//                case VK_EVENT_SET -> "VK_EVENT_SET";
//                case VK_EVENT_RESET -> "VK_EVENT_RESET";
//                case VK_INCOMPLETE -> "VK_INCOMPLETE";
//                case VK_ERROR_OUT_OF_HOST_MEMORY -> "VK_ERROR_OUT_OF_HOST_MEMORY";
//                case VK_ERROR_OUT_OF_DEVICE_MEMORY -> "VK_ERROR_OUT_OF_DEVICE_MEMORY";
//                case VK_ERROR_INITIALIZATION_FAILED -> "VK_ERROR_INITIALIZATION_FAILED";
//                case VK_ERROR_DEVICE_LOST -> "VK_ERROR_DEVICE_LOST";
//                case VK_ERROR_MEMORY_MAP_FAILED -> "VK_ERROR_MEMORY_MAP_FAILED";
//                case VK_ERROR_LAYER_NOT_PRESENT -> "VK_ERROR_LAYER_NOT_PRESENT";
//                case VK_ERROR_EXTENSION_NOT_PRESENT -> "VK_ERROR_EXTENSION_NOT_PRESENT";
//                case VK_ERROR_FEATURE_NOT_PRESENT -> "VK_ERROR_FEATURE_NOT_PRESENT";
//                case VK_ERROR_INCOMPATIBLE_DRIVER -> "VK_ERROR_INCOMPATIBLE_DRIVER";
//                case VK_ERROR_TOO_MANY_OBJECTS -> "VK_ERROR_TOO_MANY_OBJECTS";
//                case VK_ERROR_FORMAT_NOT_SUPPORTED -> "VK_ERROR_FORMAT_NOT_SUPPORTED";
//                case VK_ERROR_FRAGMENTED_POOL -> "VK_ERROR_FRAGMENTED_POOL";
//                case VK_ERROR_UNKNOWN -> "VK_ERROR_UNKNOWN";
//                default -> "Not mapped";
//            };
//            throw new RuntimeException(message + " " + errCode + " [" + vkResult + "]");
//        }

        if(vkResult != 0) {
            throw switch(vkResult) {
                case VK_ERROR_OUT_OF_DEVICE_MEMORY -> new OutOfGraphicsMemoryException(format(message, "Ran out of graphics memory"));
	            default -> throw new IllegalStateException("Unexpected value: " + vkResult);
            };
        }
    }

    private static String format(String message, String message2) {
        return message + " - " + message2;
    }
}
