package net.ice.curio.library.vulkan.object.context;

import net.ice.curio.library.vulkan.object.properties.PhysicalDeviceProperties;
import net.ice.curio.window.backend.vulkan.Surface;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;

import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK11.VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2;
import static org.lwjgl.vulkan.VK11.vkGetPhysicalDeviceProperties2;

public class PhysicalDevice {

    private final String ERROR_DEVICE_EXTENSION_PROPERTIES_ENUMERATION_FAILURE = "PhysicalDevice: Failed to get number of device extension properties.";
    private final String ERROR_DEVICE_EXTENSION_PROPERTIES_FAILURE = "PhysicalDevice: Failed to get extension properties.";

    public static final Set<String> REQUIRED_EXTENSIONS = Set.of(
            "VK_KHR_swapchain"
    );

    private final VkPhysicalDeviceMemoryProperties vkMemoryProperties;
    private final VkPhysicalDeviceFeatures vkPhysicalDeviceFeatures;

    private final VkQueueFamilyProperties.Buffer vkQueueFamilyProperties;
    private final VkExtensionProperties.Buffer vkDeviceExtensions;

    private final PhysicalDeviceProperties physicalDeviceProperties;

    private final VkPhysicalDevice vkPhysicalDevice;

    public PhysicalDevice(VkPhysicalDevice vkPhysicalDevice) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            this.vkPhysicalDevice = vkPhysicalDevice;

            IntBuffer intBuffer = stack.mallocInt(1);

            VkPhysicalDeviceProperties2 vkPhysicalDeviceProperties = VkPhysicalDeviceProperties2.calloc().sType(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2);
            vkGetPhysicalDeviceProperties2(vkPhysicalDevice, vkPhysicalDeviceProperties);
            this.physicalDeviceProperties = new PhysicalDeviceProperties(vkPhysicalDeviceProperties);

            checkVulkan(vkEnumerateDeviceExtensionProperties(vkPhysicalDevice, (String) null, intBuffer, null), ERROR_DEVICE_EXTENSION_PROPERTIES_ENUMERATION_FAILURE);
            this.vkDeviceExtensions = VkExtensionProperties.calloc(intBuffer.get(0));
            checkVulkan(vkEnumerateDeviceExtensionProperties(vkPhysicalDevice, (String) null, intBuffer, vkDeviceExtensions), ERROR_DEVICE_EXTENSION_PROPERTIES_FAILURE);

            vkGetPhysicalDeviceQueueFamilyProperties(vkPhysicalDevice, intBuffer, null);
            this.vkQueueFamilyProperties = VkQueueFamilyProperties.calloc(intBuffer.get(0));
            vkGetPhysicalDeviceQueueFamilyProperties(vkPhysicalDevice, intBuffer, vkQueueFamilyProperties);

            this.vkPhysicalDeviceFeatures = VkPhysicalDeviceFeatures.calloc();
            vkGetPhysicalDeviceFeatures(vkPhysicalDevice, vkPhysicalDeviceFeatures);

            this.vkMemoryProperties = VkPhysicalDeviceMemoryProperties.calloc();
            vkGetPhysicalDeviceMemoryProperties(vkPhysicalDevice, vkMemoryProperties);
        }
    }

    public VkDevice createLogicalDevice(VkDeviceCreateInfo createInfo) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer pointerBuffer = stack.mallocPointer(1);
            checkVulkan(vkCreateDevice(vkPhysicalDevice, createInfo, null, pointerBuffer), "PhysicalDevice: Failed to create logical device.");
            return new VkDevice(pointerBuffer.get(0), vkPhysicalDevice, createInfo);
        }
    }

    public void logPhysicalDeviceProperties() {

    }

    public int getPhysicalDeviceSurfaceCapabilities(Surface surface) {
        return surface.getSurfaceCapabilities(vkPhysicalDevice);
    }

    public int getSurfaceFormats(Surface surface, IntBuffer intBuffer, VkSurfaceFormatKHR.Buffer buffer) {
        return surface.getSurfaceFormats(vkPhysicalDevice, intBuffer, buffer);
    }


    public VkExtensionProperties.Buffer getVkDeviceExtensions() {
        return vkDeviceExtensions;
    }

    public VkQueueFamilyProperties.Buffer getVkQueueFamilyProperties() {
        return vkQueueFamilyProperties;
    }

    public VkPhysicalDeviceFeatures getVkPhysicalDeviceFeatures() {
        return vkPhysicalDeviceFeatures;
    }

    public void cleanup() {
        Logger.info("Destroying physical device [{}]", physicalDeviceProperties.getDeviceName());
        vkMemoryProperties.free();
        vkPhysicalDeviceFeatures.free();
        vkQueueFamilyProperties.free();
        vkDeviceExtensions.free();
        physicalDeviceProperties.cleanup();
    }

    public boolean hasGraphicsQueueFamily() {
        boolean result = false;
        int queueFamilyCount = vkQueueFamilyProperties != null ? vkQueueFamilyProperties.capacity() : 0;
        for (int i = 0; i < queueFamilyCount; i++) {
            VkQueueFamilyProperties familyProperties = vkQueueFamilyProperties.get(i);
            if((familyProperties.queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean supportsExtensions(Set<String> extensions) {
        Set<String> extensionsCopy = new HashSet<>(extensions);

        int extensionCount = vkDeviceExtensions != null ? vkDeviceExtensions.capacity() : 0;
        for (int i = 0; i < extensionCount; i++) {
            String extensionName = vkDeviceExtensions.get(i).extensionNameString();
            extensionsCopy.remove(extensionName);
        }

        boolean result = extensionsCopy.isEmpty();
        if(!result) {
            Logger.info("Extension [{}] is not supported by device [{}]", extensionsCopy.iterator().next(), physicalDeviceProperties.getDeviceName());
        }
        return result;
    }

    public PhysicalDeviceProperties getPhysicalDeviceProperties() {
        return physicalDeviceProperties;
    }
}
