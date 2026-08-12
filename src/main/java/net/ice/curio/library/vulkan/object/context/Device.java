package net.ice.curio.library.vulkan.object.context;

import net.ice.curio.library.vulkan.object.sync.Fence;
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

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK11.VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2;
import static org.lwjgl.vulkan.VK12.VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_FEATURES;
import static org.lwjgl.vulkan.VK13.VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_3_FEATURES;

public class Device implements Lifecycle {

    public static final Set<String> REQUIRED_EXTENSIONS = PhysicalDevice.REQUIRED_EXTENSIONS;
    public static final Set<String> OPTIONAL_EXTENSIONS = Set.of(

    );

    private final boolean samplerAnisotropy;
    private final boolean depthClamp;

    private final VkDevice vkDevice;

    public Device(PhysicalDevice physicalDevice) {
        Logger.info("Device: Creating device.");

        try(MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer requiredExtensions = createRequiredExtensions(physicalDevice, stack);

            VkQueueFamilyProperties.Buffer queuePropertiesBuffer = physicalDevice.getVkQueueFamilyProperties();
            VkDeviceQueueCreateInfo.Buffer deviceQueueCreateInfo = VkDeviceQueueCreateInfo.calloc(queuePropertiesBuffer.capacity(), stack);

            int i = 0;
            for(VkQueueFamilyProperties queueFamilyProperties : queuePropertiesBuffer) {
                FloatBuffer priorities = stack.callocFloat(queuePropertiesBuffer.get(i).queueCount());
                deviceQueueCreateInfo.get(i)
                        .sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                        .queueFamilyIndex(i)
                        .pQueuePriorities(priorities);
                i++;
            }

            VkPhysicalDeviceVulkan12Features vulkan12Features = VkPhysicalDeviceVulkan12Features.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_FEATURES)
                    .scalarBlockLayout(true);

            VkPhysicalDeviceVulkan13Features vulkan13Features = VkPhysicalDeviceVulkan13Features.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_3_FEATURES)
                    .dynamicRendering(true)
                    .synchronization2(true);

            VkPhysicalDeviceFeatures2 vulkan2Features = VkPhysicalDeviceFeatures2.calloc(stack).sType(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2);
            VkPhysicalDeviceFeatures vulkanFeatures = vulkan2Features.features();

            VkPhysicalDeviceFeatures supportedFeatures = physicalDevice.getVkPhysicalDeviceFeatures();
            samplerAnisotropy = supportedFeatures.samplerAnisotropy();
            if (samplerAnisotropy) {
                vulkanFeatures.samplerAnisotropy(true);
            }

            vulkanFeatures.geometryShader(true);
            depthClamp = supportedFeatures.depthClamp();
            vulkanFeatures.depthClamp(depthClamp);
            vulkan2Features.pNext(vulkan12Features.address());
            vulkan12Features.pNext(vulkan13Features.address());

            VkDeviceCreateInfo deviceCreateInfo = VkDeviceCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                    .pNext(vulkan2Features.address())
                    .ppEnabledExtensionNames(requiredExtensions)
                    .pQueueCreateInfos(deviceQueueCreateInfo);

            vkDevice = physicalDevice.createLogicalDevice(deviceCreateInfo);
        }
    }

    public Fence createFence(boolean signaled) {
        return new Fence(vkDevice, signaled);
    }
    public void waitForFence(Fence fence) {
        fence.waitForFence(vkDevice);
    }
    public void resetFence(Fence fence) {
        fence.reset(vkDevice);
    }
    public void destroyFence(Fence fence) {
        fence.destroyFence(vkDevice);
    }

    public Queue createQueue(int queueFamilyIndex, int index) {
        return new Queue(vkDevice, queueFamilyIndex, index);
    }

    private PointerBuffer createRequiredExtensions(PhysicalDevice physicalDevice, MemoryStack stack) {
        Set<String> deviceExtensions = getAllExtensions(physicalDevice);


        List<ByteBuffer> extensions = new ArrayList<>();
        for(String extension : REQUIRED_EXTENSIONS) {
            extensions.add(stack.ASCII(extension));
        }
        for(String extension : deviceExtensions) {
            if(OPTIONAL_EXTENSIONS.contains(extension)) {
                extensions.add(stack.ASCII(extension));
            }
        }
        PointerBuffer requiredExtensions = stack.mallocPointer(extensions.size());
        extensions.forEach(requiredExtensions::put);
        requiredExtensions.flip();
        return requiredExtensions;

    }
    private Set<String> getAllExtensions(PhysicalDevice physicalDevice) {
        Set<String> extensions = new HashSet<>();
        try(MemoryStack stack = MemoryStack.stackPush()) {
            for(VkExtensionProperties properties : physicalDevice.getVkDeviceExtensions()) {
                String name = properties.extensionNameString();
                extensions.add(name);
                Logger.info("Device: Found supported extension: [{}]", name);
            }
        }
        return extensions;
    }

    @Override
    public void cleanup() {
        Logger.debug("Device: Destroying Device.");
        vkDestroyDevice(vkDevice, null);
    }

    public void waitIdle() {
        vkDeviceWaitIdle(vkDevice);
    }
}
