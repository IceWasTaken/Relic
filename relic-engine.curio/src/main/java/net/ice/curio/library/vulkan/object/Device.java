package net.ice.curio.library.vulkan.object;

import net.ice.curio.library.vulkan.VulkanContext;
import net.ice.heirloom.Lifecycle;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.VK10.*;

public class Device implements Lifecycle {

    public static final Set<String> REQUIRED_EXTENSIONS = PhysicalDevice.REQUIRED_EXTENSIONS;
    public static final Set<String> OPTIONAL_EXTENSIONS = Set.of(

    );

    private final VkDevice vkDevice;

    public Device(VulkanContext context) {
        Logger.info("[Device]: Creating device");

        try(MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer extensions = createDeviceExtensions(context.getPhysicalDevice(), stack);

            VkQueueFamilyProperties.Buffer queuePropertiesBuffer = context.getPhysicalDevice().getVkQueueFamilyProperties();
            int queueFamilyCount = queuePropertiesBuffer.capacity();
            VkDeviceQueueCreateInfo.Buffer deviceQueueCreateInfo = VkDeviceQueueCreateInfo.calloc(queueFamilyCount, stack);

            for (int i = 0; i < queueFamilyCount; i++) {
                FloatBuffer p = stack.callocFloat(queuePropertiesBuffer.get(i).queueCount());
                deviceQueueCreateInfo.get(i).sType$Default().queueFamilyIndex(i).pQueuePriorities(p);
            }

            VkPhysicalDeviceVulkan13Features vk13Features = VkPhysicalDeviceVulkan13Features.calloc(stack)
                    .sType$Default()
                    .dynamicRendering(true)
                    .synchronization2(true);

            VkPhysicalDeviceFeatures2 features2 = VkPhysicalDeviceFeatures2.calloc(stack).sType$Default().pNext(vk13Features.address());
            
            VkDeviceCreateInfo deviceCreateInfo = VkDeviceCreateInfo.calloc(stack)
                    .sType$Default()
                    .pNext(features2.address())
                    .ppEnabledExtensionNames(extensions)
                    .pQueueCreateInfos(deviceQueueCreateInfo);

            PointerBuffer pb = stack.mallocPointer(1);
            checkVulkan(vkCreateDevice(context.getPhysicalDevice().getVkPhysicalDevice(), deviceCreateInfo, null, pb), "[Device]: Failed to create device");

            this.vkDevice = new VkDevice(pb.get(0), context.getPhysicalDevice().getVkPhysicalDevice(), deviceCreateInfo);
        }
    }


    private PointerBuffer createDeviceExtensions(PhysicalDevice physicalDevice, MemoryStack stack) {
        List<ByteBuffer> extensions = new ArrayList<>();
        Set<String> reqCopy = new HashSet<>(REQUIRED_EXTENSIONS);

        for(String extension : getAllExtensions(physicalDevice)) {
            if(reqCopy.contains(extension)) {
                extensions.add(stack.ASCII(extension));
                reqCopy.remove(extension);
            } else if(OPTIONAL_EXTENSIONS.contains(extension)) {
                extensions.add(stack.ASCII(extension));
            }
        }

        if(!reqCopy.isEmpty()) {
            throw new IllegalStateException(
                    "[Device]: Required Vulkan extensions not supported: " + reqCopy
            );
        }

        PointerBuffer result = stack.mallocPointer(extensions.size());
        extensions.forEach(result::put);

        return result.flip();
    }

    private Set<String> getAllExtensions(PhysicalDevice physicalDevice) {
        Set<String> extensions = new HashSet<>();
        try(MemoryStack stack = MemoryStack.stackPush()) {
            for(VkExtensionProperties properties : physicalDevice.getVkDeviceExtensions()) {
                String name = properties.extensionNameString();
                extensions.add(name);
                Logger.info("[Device]: Found supported extension: [{}]", name);
            }
        }
        return extensions;
    }

    @Override
    public void cleanup() {
        Logger.debug("Device: Destroying Device.");
        vkDestroyDevice(vkDevice, null);
    }

    VkDevice getVkDevice() {
        return vkDevice;
    }
}
