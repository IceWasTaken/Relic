package net.ice.curio.library.vulkan.utils;

import net.ice.curio.library.vulkan.object.context.VulkanInstance;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackDataEXT;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCreateInfoEXT;
import org.tinylog.Logger;

import java.nio.LongBuffer;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.VK10.VK_FALSE;

public class DebugUtils {

    private final String CHECK_DEBUG_MESSENGER_CREATION_FAILURE = "VulkanInstance: Error creating debug utilities.";

    private VkDebugUtilsMessengerCreateInfoEXT debugUtilsCreateInfo;

    private long debugHandle = 0;

    public DebugUtils() {
        debugUtilsCreateInfo = VkDebugUtilsMessengerCreateInfoEXT
                .calloc()
                .sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT)
                .messageSeverity(VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT)
                .messageType(VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT)
                .pfnUserCallback((messageSeverity, messageTypes, pCallbackData, pUserData) -> {
                    VkDebugUtilsMessengerCallbackDataEXT callbackDataEXT = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);
                    if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT) != 0) {
                        Logger.info("VkDebugUtilsCallback, {}", callbackDataEXT.pMessageString());
                    } else if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT) != 0) {
                        Logger.warn("VkDebugUtilsCallback, {}", callbackDataEXT.pMessageString());
                    } else if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT) != 0) {
                        Logger.error("VkDebugUtilsCallback, {}", callbackDataEXT.pMessageString());
                    } else {
                        Logger.debug("VkDebugUtilsCallback, {}", callbackDataEXT.pMessageString());
                    }
                    return VK_FALSE;
                });
    }

    public void init(VulkanInstance instance) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer longBuffer = stack.mallocLong(1);
            checkVulkan(instance.createDebugUtilsMessenger(debugUtilsCreateInfo, longBuffer), CHECK_DEBUG_MESSENGER_CREATION_FAILURE);
            debugHandle = longBuffer.get(0);
        }
    }

    public void cleanup(VulkanInstance instance) {
        if(debugHandle != 0) {
            instance.destroyDebugUtilsMessenger(debugHandle);
        }

        if(debugUtilsCreateInfo != null) {
            debugUtilsCreateInfo.pfnUserCallback().free();
            debugUtilsCreateInfo.free();
        }
    }

    public long getExtensionAddress() {
        return debugUtilsCreateInfo.address();
    }
}
