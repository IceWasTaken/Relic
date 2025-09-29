package net.ice.relic.core.rendering.backend.vulkan.device;

import net.ice.relic.core.interfaces.Initializable;
import net.ice.relic.core.system.SystemInfo;
import net.ice.relic.core.system.enums.OSType;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static net.ice.relic.core.rendering.backend.vulkan.VulkanUtil.checkVulkan;
import static org.lwjgl.vulkan.KHRPortabilitySubset.VK_KHR_PORTABILITY_SUBSET_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK11.VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2;
import static org.lwjgl.vulkan.VK13.VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_3_FEATURES;

public class Device implements Initializable {

    private final PhysicalDevice physicalDevice;
    private VkDevice device;
    private boolean samplerAnisotropy;

    public Device(PhysicalDevice physicalDevice) {
        this.physicalDevice = physicalDevice;
    }

    @Override
    public void init() {
        Logger.info("Vulkan: Creating logical device for [{}]", physicalDevice.getDeviceName());

        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer requiredExtensions = createReqExtensions(physicalDevice, stack);

            VkQueueFamilyProperties.Buffer queues = physicalDevice.getQueueFamilies();
            int queueFamilyCount = queues.capacity();
            VkDeviceQueueCreateInfo.Buffer queueInfos = VkDeviceQueueCreateInfo.calloc(queueFamilyCount, stack);

            for (int i = 0; i < queueFamilyCount; i++) {
                FloatBuffer priorities = stack.callocFloat(queues.get(i).queueCount());
                queueInfos.get(i)
                        .sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                        .queueFamilyIndex(i)
                        .pQueuePriorities(priorities);
            }

            VkPhysicalDeviceVulkan13Features features13 = VkPhysicalDeviceVulkan13Features.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_3_FEATURES)
                    .dynamicRendering(true)
                    .synchronization2(true);


            VkPhysicalDeviceFeatures2 features2 = VkPhysicalDeviceFeatures2.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2)
                    .pNext(features13.address());

            VkDeviceCreateInfo createInfo = VkDeviceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                    .pNext(features2.address())
                    .ppEnabledExtensionNames(requiredExtensions)
                    .pQueueCreateInfos(queueInfos);


            PointerBuffer pDevice = stack.mallocPointer(1);
            checkVulkan(vkCreateDevice(physicalDevice.getVkPhysicalDevice(), createInfo, null, pDevice),
                    "Failed to create logical device");


            device = new VkDevice(pDevice.get(0), physicalDevice.getVkPhysicalDevice(), createInfo);
        }
    }

    private PointerBuffer createReqExtensions(PhysicalDevice physDevice, MemoryStack stack) {
        Set<String> deviceExtensions = getDeviceExtensions(physDevice);
        boolean usePortability = deviceExtensions.contains(VK_KHR_PORTABILITY_SUBSET_EXTENSION_NAME) && SystemInfo.getOSType() == OSType.MAC;

        var extsList = new ArrayList<ByteBuffer>();
        for (String extension : PhysicalDevice.REQUIRED_EXTENSIONS) {
            extsList.add(stack.ASCII(extension));
        }
        if (usePortability) {
            extsList.add(stack.ASCII(VK_KHR_PORTABILITY_SUBSET_EXTENSION_NAME));
        }

        PointerBuffer requiredExtensions = stack.mallocPointer(extsList.size());
        extsList.forEach(requiredExtensions::put);
        requiredExtensions.flip();

        return requiredExtensions;
    }

    private Set<String> getDeviceExtensions(PhysicalDevice physDevice) {
        Set<String> deviceExtensions = new HashSet<>();
        try (var stack = MemoryStack.stackPush()) {
            IntBuffer numExtensionsBuf = stack.callocInt(1);
            vkEnumerateDeviceExtensionProperties(physDevice.getVkPhysicalDevice(), (String) null, numExtensionsBuf, null);
            int numExtensions = numExtensionsBuf.get(0);
            Logger.trace("Device supports [{}] extensions", numExtensions);

            var propsBuff = VkExtensionProperties.calloc(numExtensions, stack);
            vkEnumerateDeviceExtensionProperties(physDevice.getVkPhysicalDevice(), (String) null, numExtensionsBuf, propsBuff);
            for (int i = 0; i < numExtensions; i++) {
                VkExtensionProperties props = propsBuff.get(i);
                String extensionName = props.extensionNameString();
                deviceExtensions.add(extensionName);
                Logger.trace("Supported device extension [{}]", extensionName);
            }
        }
        return deviceExtensions;
    }

    public VkDevice getDevice() {
        return device;
    }

    public PhysicalDevice getPhysicalDevice() {
        return physicalDevice;
    }
}
