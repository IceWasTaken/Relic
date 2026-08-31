package net.ice.curio.library.vulkan.utils;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkExtensionProperties;
import org.lwjgl.vulkan.VkLayerProperties;
import org.tinylog.Logger;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.lwjgl.vulkan.VK10.vkEnumerateInstanceExtensionProperties;
import static org.lwjgl.vulkan.VK10.vkEnumerateInstanceLayerProperties;

public class ValidationExtensionUtils {


    private static final List<String> REQUESTED_EXTENSIONS = List.of(
            "VK_EXT_debug_utils"
    );

    public static Set<String> getInstanceExtensions() {
        Set<String> instanceExtensions = new HashSet<>();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer extensionCountBuffer = stack.callocInt(1);
            vkEnumerateInstanceExtensionProperties((String) null, extensionCountBuffer, null);
            int extensionCount = extensionCountBuffer.get(0);

            Logger.info("ExtensionUtils: Found [{}] supported extensions.", extensionCount);

            VkExtensionProperties.Buffer extensionsBuffer = VkExtensionProperties.calloc(extensionCount, stack);
            vkEnumerateInstanceExtensionProperties((String) null, extensionCountBuffer, extensionsBuffer);
            for (int i = 0; i < extensionCount; i++) {
                VkExtensionProperties props = extensionsBuffer.get(i);
                String extensionName = props.extensionNameString();
                instanceExtensions.add(extensionName);
                Logger.info("ExtensionUtils: Found supported extension [{}]", extensionName);
            }
        }
        return instanceExtensions;
    }


}
