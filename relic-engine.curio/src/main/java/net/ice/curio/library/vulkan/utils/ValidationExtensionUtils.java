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

    private static final List<String> REQUESTED_LAYERS = List.of(
            "VK_LAYER_KHRONOS_validation"
    );

    private static final List<String> REQUESTED_EXTENSIONS = List.of(
            "VK_EXT_debug_utils"
    );

    public static List<String> getSupportedLayers() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer layerCountBuffer = stack.callocInt(1);
            vkEnumerateInstanceLayerProperties(layerCountBuffer, null);
            int layerCount = layerCountBuffer.get(0);

            Logger.info("ValidationUtils: Found [{}] supported layers.", layerCount);

            VkLayerProperties.Buffer propertiesBuffer = VkLayerProperties.calloc(layerCount, stack);
            vkEnumerateInstanceLayerProperties(layerCountBuffer, propertiesBuffer);

            List<String> layersToUse = new ArrayList<>();
            for (int i = 0; i < layerCount; i++) {
                VkLayerProperties properties = propertiesBuffer.get(i);
                String layerName = properties.layerNameString();
                Logger.info("ValidationUtils: Found supported layer: [{}]", layerName);
                if(REQUESTED_LAYERS.contains(layerName)) {
                    layersToUse.add(layerName);
                    Logger.info("ValidationUtils: Using supported layer: [{}]", layerName);
                }
            }
            return layersToUse;
        }
    }

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
