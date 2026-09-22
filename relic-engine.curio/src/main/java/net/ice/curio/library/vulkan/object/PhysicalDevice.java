package net.ice.curio.library.vulkan.object;

import net.ice.curio.library.vulkan.VulkanContext;
import net.ice.curio.library.vulkan.enums.PhysicalDeviceType;
import net.ice.curio.library.vulkan.object.properties.PhysicalDeviceProperties;
import net.ice.heirloom.application.ApplicationProperties;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;

import java.nio.IntBuffer;
import java.util.*;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK11.VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2;
import static org.lwjgl.vulkan.VK11.vkGetPhysicalDeviceProperties2;

public class PhysicalDevice {

    private static final String ERROR_DEVICE_EXTENSION_PROPERTIES_ENUMERATION_FAILURE = "[PhysicalDevice]: Failed to get number of device extension properties";
    private static final String ERROR_DEVICE_EXTENSION_PROPERTIES_FAILURE = "[PhysicalDevice]: Failed to get extension properties";

    private static final String ERROR_PHYS_DEVICE_ENUMERATION = "[PhysicalDevice]: Error enumerating physical devices";
    private static final String ERROR_GET_PHYS_DEVICE = "[PhysicalDevice]: Error getting physical devices";

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

    public static PhysicalDevice pickDevice(VulkanContext vulkanContext) {
        PhysicalDevice result = null;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            Logger.info("[PhysicalDevice]: Selecting physical device.");

            PointerBuffer physicalDevicesBuffer = enumeratePhysicalDevices(vulkanContext, stack);
            int deviceCount = physicalDevicesBuffer.limit();

            Logger.debug("[PhysicalDevice]: Detected {} physical devices.", deviceCount);

            List<PhysicalDevice> physicalDevices = new ArrayList<>();

            for (int i = 0; i < deviceCount; i++) {
                VkPhysicalDevice vkPhysicalDevice = new VkPhysicalDevice(physicalDevicesBuffer.get(i), vulkanContext.getInstance().getVkInstance());
                PhysicalDevice physicalDevice = new PhysicalDevice(vkPhysicalDevice);

                String deviceName = physicalDevice.getPhysicalDeviceProperties().getDeviceName();

                if(!physicalDevice.hasGraphicsQueueFamily()) {
                    Logger.debug("[PhysicalDevice]: Device {} does not support graphics queue family", physicalDevice.getPhysicalDeviceProperties().getDeviceName());
                    physicalDevice.cleanup();
                    continue;
                }

                if(!physicalDevice.supportsExtensions(REQUIRED_EXTENSIONS)) {
                    Logger.debug("[PhysicalDevice]: Device {} does not support required extensions", deviceName);
                    physicalDevice.cleanup();
                    continue;
                }

                if(physicalDevice.getPhysicalDeviceProperties().getPhysicalDeviceType() == PhysicalDeviceType.DISCRETE) {
                    physicalDevices.addFirst(physicalDevice);
                } else {
                    physicalDevices.add(physicalDevice);
                }
            }

            result = chooseResult(physicalDevices, vulkanContext.getCurio().getApplicationProperties());
        }
        return result;
    }

    private static PointerBuffer enumeratePhysicalDevices(VulkanContext vulkanContext, MemoryStack stack) {
        IntBuffer deviceCountBuff = stack.mallocInt(1);

        checkVulkan(vkEnumeratePhysicalDevices(vulkanContext.getInstance().getVkInstance(), deviceCountBuff, null), ERROR_PHYS_DEVICE_ENUMERATION);

        PointerBuffer physicalDevicesBuffer = stack.mallocPointer(deviceCountBuff.get(0));
        checkVulkan(vkEnumeratePhysicalDevices(vulkanContext.getInstance().getVkInstance(), deviceCountBuff, physicalDevicesBuffer), ERROR_GET_PHYS_DEVICE);

        return physicalDevicesBuffer;
    }

    private static PhysicalDevice chooseResult(List<PhysicalDevice> devices, ApplicationProperties properties) {
        if(devices.isEmpty()) {
            throw new RuntimeException("[PhysicalDevice]: No suitable physical devices found");
        }

        PhysicalDevice result = null;
        for (PhysicalDevice device : devices) {
            String deviceName = device.getPhysicalDeviceProperties().getDeviceName();

            if(properties.hasArgument("prefer-nvidia") && deviceName.toLowerCase().contains("nvidia")) {
                result = device;
                break;
            }
            if(properties.hasArgument("prefer-amd") && deviceName.toLowerCase().contains("radeon")) {
                result = device;
                break;
            }
            if(properties.hasArgument("prefer-intel") && deviceName.toLowerCase().contains("arc")) {
                result = device;
                break;
            }
        }

        if(result == null) {
            result = devices.removeFirst();
        } else {
            devices.remove(result);
        }

        devices.forEach(PhysicalDevice::cleanup);
        Logger.info("[PhysicalDevice]: Physical device '{}' selected", result.getPhysicalDeviceProperties().getDeviceName());
        return result;
    }

    public void cleanup() {
        Logger.info("Destroying physical device [{}]", physicalDeviceProperties.getDeviceName());
        vkMemoryProperties.free();
        vkPhysicalDeviceFeatures.free();
        vkQueueFamilyProperties.free();
        vkDeviceExtensions.free();
        physicalDeviceProperties.cleanup();
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

    public boolean hasGraphicsQueueFamily() {
        for(VkQueueFamilyProperties properties : vkQueueFamilyProperties) {
            if((properties.queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean supportsExtensions(Set<String> extensions) {
        Set<String> extensionsCopy = new HashSet<>(extensions);

        for(VkExtensionProperties properties : vkDeviceExtensions) {
            extensionsCopy.remove(properties.extensionNameString());
        }

		return extensionsCopy.isEmpty();
	}

    public PhysicalDeviceProperties getPhysicalDeviceProperties() {
        return physicalDeviceProperties;
    }

    VkPhysicalDevice getVkPhysicalDevice() {
        return vkPhysicalDevice;
    }
}
