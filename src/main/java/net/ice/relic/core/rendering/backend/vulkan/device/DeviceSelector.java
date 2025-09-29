package net.ice.relic.core.rendering.backend.vulkan.device;

import net.ice.relic.core.rendering.backend.vulkan.VulkanInstance;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.tinylog.Logger;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static net.ice.relic.core.rendering.backend.vulkan.VulkanUtil.checkVulkan;
import static org.lwjgl.vulkan.VK10.vkEnumeratePhysicalDevices;

public class DeviceSelector {

    private final VulkanInstance instance;

    public DeviceSelector(VulkanInstance instance) {
        this.instance = instance;
    }

    public PhysicalDevice selectBestDevice() {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer deviceCountBuffer = stack.mallocInt(1);
            checkVulkan(vkEnumeratePhysicalDevices(instance.getInstance(), deviceCountBuffer, null), "Vulkan: Failed to enumerate physical devices.");

            int deviceCount = deviceCountBuffer.get(0);
            if(deviceCount == 0) {
                throw new RuntimeException("Vulkan: Failed to find physical devices.");
            }

            PointerBuffer physicalDevices = stack.mallocPointer(deviceCount);
            checkVulkan(vkEnumeratePhysicalDevices(instance.getInstance(), deviceCountBuffer, physicalDevices), "Vulkan: Failed to enumerate physical devices.");

            List<PhysicalDevice> candidates = new ArrayList<>();
            for (int i = 0; i < deviceCount; i++) {
                VkPhysicalDevice vkDevice = new VkPhysicalDevice(physicalDevices.get(i), instance.getInstance());
                PhysicalDevice pd = new PhysicalDevice(vkDevice, instance);
                pd.init();
                if (isDeviceSuitable(pd)) {
                    candidates.add(pd);
                }
            }

            if (candidates.isEmpty()) {
                throw new RuntimeException("No suitable Vulkan physical devices found.");
            }

            candidates.sort((a, b) -> Integer.compare(scoreDevice(b), scoreDevice(a)));
            PhysicalDevice best = candidates.get(0);
            Logger.info("Vulkan: Selected Vulkan device: {}", best.getDeviceName());
            return best;
        }
    }

    private boolean isDeviceSuitable(PhysicalDevice device) {
        return device.hasGraphicsQueueFamily() && device.hasKHRSwapChainExtension() && device.supportsExtensions(PhysicalDevice.REQUIRED_EXTENSIONS);
    }

    private int scoreDevice(PhysicalDevice device) {
        int score = 0;

        if (device.isDiscreteGpu()) score += 1000;

        score += device.getProperties().limits().maxImageDimension2D();

        if (device.getFeatures().samplerAnisotropy()) score += 500;

        return score;
    }
}
