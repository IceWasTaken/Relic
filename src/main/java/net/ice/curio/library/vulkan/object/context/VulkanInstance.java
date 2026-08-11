package net.ice.curio.library.vulkan.object.context;

import net.ice.curio.library.vulkan.enums.PhysicalDeviceType;
import net.ice.curio.library.vulkan.utils.DebugUtils;
import net.ice.curio.library.vulkan.utils.ValidationExtensionUtils;
import net.ice.curio.window.backend.vulkan.VulkanWindow;
import net.ice.relic.application.ApplicationProperties;
import net.ice.heirloom.Lifecycle;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.EXTDebugUtils.vkCreateDebugUtilsMessengerEXT;
import static org.lwjgl.vulkan.EXTDebugUtils.vkDestroyDebugUtilsMessengerEXT;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK13.VK_API_VERSION_1_3;

public class VulkanInstance implements Lifecycle {

    private final String ERROR_INSTANCE_CREATION_FAILURE = "VulkanInstance: Error creating instance.";
    private final String ERROR_PHYS_DEVICE_ENUMERATION = "VulkanInstance: Error enumerating physical devices.";
    private final String ERROR_GET_PHYS_DEVICE = "VulkanInstance: Error getting physical devices.";

    private final VkInstance vkInstance;

    private DebugUtils debugUtils;

    public VulkanInstance(ApplicationProperties applicationProperties) {
        Logger.info("VulkanInstance: Creating instance.");
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkApplicationInfo applicationInfo = VkApplicationInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                    .pApplicationName(stack.UTF8(applicationProperties.applicationName()))
                    .applicationVersion(1)
                    .pEngineName(stack.UTF8("Relic"))
                    .engineVersion(1)
                    .apiVersion(VK_API_VERSION_1_3);

            List<String> validationLayers = ValidationExtensionUtils.getSupportedLayers();
            boolean canValidate = applicationProperties.debug();

            if(applicationProperties.debug() && validationLayers.isEmpty()) {
                canValidate = false;
                Logger.warn("VulkanInstance: Validation requested but not supported.");
            }
            Logger.info("VulkanInstance: Validation is {}.", canValidate ? "enabled" : "disabled");

            PointerBuffer requiredLayers;
            if(canValidate) {
                requiredLayers = stack.mallocPointer(validationLayers.size());
                validationLayers.forEach((layer) -> {
                    Logger.info("VulkanInstance: Using validation layer: [{}].", layer);
                    requiredLayers.put(stack.ASCII(layer));
                });
            } else {
                requiredLayers = null;
            }

            Set<String> instanceExtension = ValidationExtensionUtils.getInstanceExtensions();

            PointerBuffer glfwExtensions = VulkanWindow.getExtensions();
            ArrayList<ByteBuffer> additionalExtensions = new ArrayList<>();
            if(canValidate) {
                additionalExtensions.add(stack.UTF8(EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME));
            }

            PointerBuffer requiredExtensions = stack.mallocPointer(glfwExtensions.remaining() + additionalExtensions.size());
            requiredExtensions.put(glfwExtensions);
            additionalExtensions.forEach((requiredExtensions::put));
            requiredExtensions.flip();

            long extension = 0;
            if(canValidate) {
                debugUtils = new DebugUtils();
                extension = debugUtils.getExtensionAddress();
            }

            VkInstanceCreateInfo instanceCreateInfo = VkInstanceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                    .pNext(extension)
                    .pApplicationInfo(applicationInfo)
                    .ppEnabledLayerNames(requiredLayers)
                    .ppEnabledExtensionNames(requiredExtensions);

            PointerBuffer instance = stack.mallocPointer(1);
            checkVulkan(vkCreateInstance(instanceCreateInfo, null, instance), ERROR_INSTANCE_CREATION_FAILURE);
            vkInstance = new VkInstance(instance.get(0), instanceCreateInfo);

            if(canValidate) {
                debugUtils.init(this);
            }
        }
    }

    public PhysicalDevice createPhysicalDevice(String prefName) {
        Logger.info("VulkanInstance: Selecting physical device.");
        PhysicalDevice result = null;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer intBuffer = stack.mallocInt(1);

            checkVulkan(vkEnumeratePhysicalDevices(vkInstance, intBuffer, null), ERROR_PHYS_DEVICE_ENUMERATION);

            int deviceCount = intBuffer.get(0);
            PointerBuffer physicalDevicesBuffer = stack.mallocPointer(deviceCount);

            Logger.info("VulkanInstance: Detected {} physical devices.", deviceCount);

            checkVulkan(vkEnumeratePhysicalDevices(vkInstance, intBuffer, physicalDevicesBuffer), ERROR_GET_PHYS_DEVICE);

            List<PhysicalDevice> physicalDevices = new ArrayList<>();
            for (int i = 0; i < deviceCount; i++) {
                VkPhysicalDevice vkPhysicalDevice = new VkPhysicalDevice(physicalDevicesBuffer.get(i), vkInstance);
                PhysicalDevice physicalDevice = new PhysicalDevice(vkPhysicalDevice);

                String deviceName = physicalDevice.getPhysicalDeviceProperties().getDeviceName();
                if(!physicalDevice.hasGraphicsQueueFamily()) {
                    Logger.info("VulkanInstance: Device [{}] does not support graphics queue family.", deviceName);
                    physicalDevice.cleanup();
                    continue;
                }

                if(!physicalDevice.supportsExtensions(PhysicalDevice.REQUIRED_EXTENSIONS)) {
                    Logger.info("VulkanInstance: Device [{}] does not support required extensions.");
                    physicalDevice.cleanup();
                    continue;
                }

                if(prefName != null && prefName.equals(deviceName)) {
                    result = physicalDevice;
                    break;
                }
                if(physicalDevice.getPhysicalDeviceProperties().getPhysicalDeviceType() == PhysicalDeviceType.DISCRETE) {
                    physicalDevices.addFirst(physicalDevice);
                } else {
                    physicalDevices.add(physicalDevice);
                }
            }

            result = result == null && !physicalDevices.isEmpty() ? physicalDevices.removeFirst() : result;

            physicalDevices.forEach(PhysicalDevice::cleanup);

            if(result == null) {
                throw new RuntimeException("VulkanInstance: No suitable physical devices found.");
            }
            Logger.info("VulkanInstance: Device [{}] selected.", result.getPhysicalDeviceProperties().getDeviceName());
        }

        return result;
    }

    @Override
    public void cleanup() {
        Logger.info("VulkanInstance: Destroying Instance.");
        debugUtils.cleanup(this);

        vkDestroyInstance(vkInstance, null);
    }


    public void createWindowSurface(VulkanWindow window, LongBuffer surface) {
        window.createSurface(vkInstance, surface);
    }

    public int createDebugUtilsMessenger(VkDebugUtilsMessengerCreateInfoEXT createInfo, LongBuffer longBuffer) {
        return vkCreateDebugUtilsMessengerEXT(vkInstance, createInfo, null, longBuffer);
    }
    public void destroyDebugUtilsMessenger(long handle) {
        vkDestroyDebugUtilsMessengerEXT(vkInstance, handle, null);
    }

}
