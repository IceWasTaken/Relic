package net.ice.relic.engine.vulkan;

import net.ice.relic.core.util.OSUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.ice.relic.engine.vulkan.VulkanUtil.checkVulkanResult;
import static org.lwjgl.vulkan.KHRPortabilitySubset.VK_KHR_PORTABILITY_SUBSET_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;

public class Device {

    private final VkDevice device;
    private final PhysicalDevice physicalDevice;

    private final boolean samplerAnistropySupport;

    public Device(PhysicalDevice physicalDevice) {
        Logger.info("Vulkan: Initializing Vulkan device.");

        this.physicalDevice = physicalDevice;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            Set<String> extensions = getDeviceExtensions();

            boolean usePortability = extensions.contains(VK_KHR_PORTABILITY_SUBSET_EXTENSION_NAME) && OSUtil.getOSType() == OSUtil.OSType.MAC;

            int extensionCount = usePortability ? 2 : 1;
            PointerBuffer requiredExtensions = stack.mallocPointer(extensionCount);
            requiredExtensions.put(stack.ASCII(KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME));
            if(usePortability) {
                requiredExtensions.put(stack.ASCII(VK_KHR_PORTABILITY_SUBSET_EXTENSION_NAME));
            }
            requiredExtensions.flip();

            VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.calloc(stack);
            VkPhysicalDeviceFeatures supportedFeatures = this.physicalDevice.getPhysicalDeviceFeatures();
            samplerAnistropySupport = supportedFeatures.samplerAnisotropy();
            if(samplerAnistropySupport) {
                features.samplerAnisotropy(true);
            }

            VkQueueFamilyProperties.Buffer queuePropertiesBuffer = physicalDevice.getQueueFamilyProperties();
            int queueCount = queuePropertiesBuffer.capacity();
            VkDeviceQueueCreateInfo.Buffer queueCreateInfoBuffer = VkDeviceQueueCreateInfo.calloc(queueCount, stack);
            for (int i = 0; i < queueCount; i++) {
                FloatBuffer priorities = stack.callocFloat(queuePropertiesBuffer.get(i).queueCount());
                queueCreateInfoBuffer.get(i)
                        .sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                        .queueFamilyIndex(i)
                        .pQueuePriorities();
            }

            VkDeviceCreateInfo deviceCreateInfo = VkDeviceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                    .ppEnabledExtensionNames(requiredExtensions)
                    .pEnabledFeatures(features)
                    .pQueueCreateInfos(queueCreateInfoBuffer);

            PointerBuffer pDevice = stack.mallocPointer(1);
            checkVulkanResult(vkCreateDevice(physicalDevice.getPhysicalDevice(), deviceCreateInfo, null, pDevice), "Failed to create device");
            device = new VkDevice(pDevice.get(0), physicalDevice.getPhysicalDevice(), deviceCreateInfo);
        }

    }

    private Set<String> getDeviceExtensions() {
        Set<String> deviceExtensions = new HashSet<>();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer numExtensionsBuf = stack.callocInt(1);
            vkEnumerateDeviceExtensionProperties(physicalDevice.getPhysicalDevice(), (String) null, numExtensionsBuf, null);
            int numExtensions = numExtensionsBuf.get(0);
            Logger.debug("Vulkan: Device supports [{}] extensions", numExtensions);

            VkExtensionProperties.Buffer propsBuff = VkExtensionProperties.calloc(numExtensions, stack);
            vkEnumerateDeviceExtensionProperties(physicalDevice.getPhysicalDevice(), (String) null, numExtensionsBuf, propsBuff);
            for (int i = 0; i < numExtensions; i++) {
                VkExtensionProperties props = propsBuff.get(i);
                String extensionName = props.extensionNameString();
                deviceExtensions.add(extensionName);
                Logger.debug("Vulkan: Supported device extension [{}]", extensionName);
            }
        }
        return deviceExtensions;
    }


    public static class PhysicalDevice {

        private final VkPhysicalDevice physicalDevice;

        private final VkPhysicalDeviceProperties physicalDeviceProperties;
        private final VkExtensionProperties.Buffer deviceExtensions;
        private final VkQueueFamilyProperties.Buffer queueFamilyProperties;
        private final VkPhysicalDeviceFeatures physicalDeviceFeatures;
        private final VkPhysicalDeviceMemoryProperties physicalDeviceMemoryProperties;

        private PhysicalDevice(VkPhysicalDevice physicalDevice) {
            try(MemoryStack stack = MemoryStack.stackPush()) {
                this.physicalDevice = physicalDevice;

                IntBuffer intBuffer = stack.mallocInt(1);

                //Properties
                physicalDeviceProperties = VkPhysicalDeviceProperties.calloc();
                vkGetPhysicalDeviceProperties(physicalDevice, physicalDeviceProperties);

                //Device Extensions
                checkVulkanResult(vkEnumerateDeviceExtensionProperties(physicalDevice, (String) null, intBuffer, null), "Failed to get number of device extensions");
                deviceExtensions = VkExtensionProperties.calloc(intBuffer.get(0));
                checkVulkanResult(vkEnumerateDeviceExtensionProperties(physicalDevice, (String) null, intBuffer, deviceExtensions), "Failed to get device extensions");

                //Queue Families
                vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, intBuffer, null);
                queueFamilyProperties = VkQueueFamilyProperties.calloc(intBuffer.get(0));
                vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, intBuffer, queueFamilyProperties);

                //Features
                physicalDeviceFeatures = VkPhysicalDeviceFeatures.calloc();
                vkGetPhysicalDeviceFeatures(physicalDevice, physicalDeviceFeatures);

                //Memory Properties
                physicalDeviceMemoryProperties = VkPhysicalDeviceMemoryProperties.calloc();
                vkGetPhysicalDeviceMemoryProperties(physicalDevice, physicalDeviceMemoryProperties);
            }
        }

        public static PhysicalDevice createPhysicalDevice(VulkanInstance instance, String prefDeviceName) {
            Logger.info("Vulkan: Selecting physical device.");
            PhysicalDevice deviceSelected = null;

            try(MemoryStack stack = MemoryStack.stackPush()) {
                PointerBuffer physicalDevices = getPhysicalDevices(instance, stack);
                int deviceCount = physicalDevices != null ? physicalDevices.capacity() : 0;
                if(deviceCount <= 0) {
                    throw new RuntimeException("Failed to find any physical devices.");
                }

                List<PhysicalDevice> devices = new ArrayList<>();
                for(int i = 0; i < deviceCount; i++) {
                    VkPhysicalDevice vkPhysicalDevice = new VkPhysicalDevice(physicalDevices.get(i), instance.getInstance());
                    PhysicalDevice physicalDevice = new PhysicalDevice(vkPhysicalDevice);

                    String name = physicalDevice.getDeviceName();
                    if(physicalDevice.hasGraphicsQueueFamily() && physicalDevice.hasKHRSwapChainExtension()) {
                        Logger.debug("Vulkan: Found physical device [{}]", name);
                        if(prefDeviceName != null && prefDeviceName.equals(name)) {
                            deviceSelected = physicalDevice;
                            break;
                        }
                        devices.add(physicalDevice);
                    } else {
                        Logger.debug("Vulkan: Skipping physical device [{}]", name);
                        physicalDevice.cleanup();
                    }
                }

                deviceSelected = deviceSelected == null && !devices.isEmpty() ? devices.remove(0) : deviceSelected;

                for(PhysicalDevice device : devices) {
                    device.cleanup();
                }

                if(deviceSelected == null) {
                    throw new RuntimeException("Failed to find a suitable physical device.");
                }

                Logger.info("Vulkan: Selected physical device [{}]", deviceSelected.getDeviceName());
            }
            return deviceSelected;
        }

        protected static PointerBuffer getPhysicalDevices(VulkanInstance instance, MemoryStack stack) {
            PointerBuffer devices;

            IntBuffer intBuffer = stack.mallocInt(1);
            checkVulkanResult(vkEnumeratePhysicalDevices(instance.getInstance(), intBuffer, null), "Failed to get number of physical devices");
            int deviceCount = intBuffer.get(0);
            Logger.info("Vulkan: Found {} physical devices.", deviceCount);

            devices = stack.mallocPointer(deviceCount);
            checkVulkanResult(vkEnumeratePhysicalDevices(instance.getInstance(), intBuffer, devices), "Failed to get physical devices");
            return devices;
        }

        private boolean hasGraphicsQueueFamily() {
            boolean result = false;
            int queueFamilyCount = queueFamilyProperties != null ? queueFamilyProperties.capacity() : 0;
            for(int i = 0; i < queueFamilyCount; i++) {
                VkQueueFamilyProperties queueFamilyProperties = this.queueFamilyProperties.get(i);
                if((queueFamilyProperties.queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {
                    result = true;
                    break;
                }
            }
            return result;
        }

        private boolean hasKHRSwapChainExtension() {
            boolean result = false;
            int numExtensions = deviceExtensions != null ? deviceExtensions.capacity() : 0;
            for (int i = 0; i < numExtensions; i++) {
                String extensionName = deviceExtensions.get(i).extensionNameString();
                if (KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME.equals(extensionName)) {
                    result = true;
                    break;
                }
            }
            return result;
        }

        public void cleanup() {
            Logger.info("Vulkan: Destroying physical device [{}]", physicalDeviceProperties.deviceNameString());
            queueFamilyProperties.free();
            deviceExtensions.free();
            physicalDeviceProperties.free();
        }

        public String getDeviceName() {
            return physicalDeviceProperties.deviceNameString();
        }

        public VkPhysicalDevice getPhysicalDevice() {
            return physicalDevice;
        }

        public VkPhysicalDeviceFeatures getPhysicalDeviceFeatures() {
            return physicalDeviceFeatures;
        }

        public VkQueueFamilyProperties.Buffer getQueueFamilyProperties() {
            return queueFamilyProperties;
        }
    }
}
