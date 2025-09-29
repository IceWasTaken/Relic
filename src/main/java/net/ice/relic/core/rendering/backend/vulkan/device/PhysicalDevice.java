package net.ice.relic.core.rendering.backend.vulkan.device;

import net.ice.relic.core.rendering.backend.vulkan.VulkanInstance;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

import static net.ice.relic.core.rendering.backend.vulkan.VulkanUtil.checkVulkan;
import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK11.VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2;
import static org.lwjgl.vulkan.VK11.vkGetPhysicalDeviceProperties2;
import static org.lwjgl.vulkan.VK13.*;

public class PhysicalDevice {
    protected static final Set<String> REQUIRED_EXTENSIONS;

    static {
        REQUIRED_EXTENSIONS = new HashSet<>();
        REQUIRED_EXTENSIONS.add(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
    }

    private final VkPhysicalDevice physicalDevice;

    private VkPhysicalDeviceProperties2 physicalDeviceProperties;

    private VkExtensionProperties.Buffer deviceExtensions;

    private VkQueueFamilyProperties.Buffer queueFamilyProperties;

    private VkPhysicalDeviceFeatures physicalDeviceFeatures;

    private VkPhysicalDeviceMemoryProperties physicalDeviceMemoryProperties;

    public PhysicalDevice(VkPhysicalDevice physicalDevice, VulkanInstance instance) {
        this.physicalDevice = physicalDevice;
    }


    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer intBuf = stack.mallocInt(1);

            physicalDeviceProperties = VkPhysicalDeviceProperties2.calloc().sType(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2);
            vkGetPhysicalDeviceProperties2(physicalDevice, physicalDeviceProperties);

            checkVulkan(vkEnumerateDeviceExtensionProperties(physicalDevice, (ByteBuffer) null, intBuf, null), "Vulkan: Failed to query device extension count");
            deviceExtensions = VkExtensionProperties.calloc(intBuf.get(0));
            checkVulkan(vkEnumerateDeviceExtensionProperties(physicalDevice, (ByteBuffer) null, intBuf, deviceExtensions), "Vulkan: Failed to query device extensions");

            vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, intBuf, null);
            queueFamilyProperties = VkQueueFamilyProperties.calloc(intBuf.get(0));
            vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, intBuf, queueFamilyProperties);

            physicalDeviceFeatures = VkPhysicalDeviceFeatures.calloc();
            vkGetPhysicalDeviceFeatures(physicalDevice, physicalDeviceFeatures);

            physicalDeviceMemoryProperties = VkPhysicalDeviceMemoryProperties.calloc();
            vkGetPhysicalDeviceMemoryProperties(physicalDevice, physicalDeviceMemoryProperties);

            Logger.info("Vulkan: Initialized physical device [{}]", getDeviceName());
        }
    }

    public void cleanup() {
        Logger.info("Vulkan: Destroying physical device [{}]", physicalDeviceProperties.properties().deviceNameString());
        queueFamilyProperties.free();
        deviceExtensions.free();
        physicalDeviceProperties.free();
    }

    public boolean isDiscreteGpu() {
        return physicalDeviceProperties.properties().deviceType() == VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU;
    }


    public String getDeviceName() {
        return physicalDeviceProperties.properties().deviceNameString();
    }

    public boolean hasGraphicsQueueFamily() {
        for (int i = 0; i < queueFamilyProperties.capacity(); i++) {
            if ((queueFamilyProperties.get(i).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean hasKHRSwapChainExtension() {
        for (int i = 0; i < deviceExtensions.capacity(); i++) {
            if ("VK_KHR_swapchain".equals(deviceExtensions.get(i).extensionNameString())) {
                return true;
            }
        }
        return false;
    }

    public boolean supportsExtensions(Set<String> extensions) {
        var copyExtensions = new HashSet<>(extensions);
        int numExtensions = deviceExtensions != null ? deviceExtensions.capacity() : 0;
        for (int i = 0; i < numExtensions; i++) {
            String extensionName = deviceExtensions.get(i).extensionNameString();
            copyExtensions.remove(extensionName);
        }

        boolean result = copyExtensions.isEmpty();
        if (!result) {
            Logger.debug("At least [{}] extension is not supported by device [{}]", copyExtensions.iterator().next(),
                    getDeviceName());
        }
        return result;
    }

    public VkPhysicalDevice getVkPhysicalDevice() {
        return physicalDevice;
    }

    public VkPhysicalDeviceProperties getProperties() {
        return physicalDeviceProperties.properties();
    }

    public VkPhysicalDeviceFeatures getFeatures() {
        return physicalDeviceFeatures;
    }

    public VkQueueFamilyProperties.Buffer getQueueFamilies() {
        return queueFamilyProperties;
    }

    public VkPhysicalDeviceMemoryProperties getPhysicalDeviceMemoryProperties() {
        return physicalDeviceMemoryProperties;
    }
}
