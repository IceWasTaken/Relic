package net.ice.relic.core.window.backend.vulkan;

import net.ice.relic.core.interfaces.Cleanable;
import net.ice.relic.core.rendering.backend.vulkan.VulkanInstance;
import net.ice.relic.core.rendering.backend.vulkan.device.Device;
import net.ice.relic.core.window.Window;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.tinylog.Logger;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static net.ice.relic.core.rendering.backend.vulkan.VulkanUtil.checkVulkan;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_B8G8R8A8_SRGB;

public class Surface implements Cleanable {

    private VkSurfaceCapabilitiesKHR surfaceCapabilities;
    private SurfaceFormat surfaceFormat;

    private long surfaceHandle;

    public Surface() {

    }

    public void init(VulkanInstance vulkanInstance, Window window, Device device) {
        Logger.info("Vulkan: Creating surface.");
        try(MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer handleBuffer = stack.callocLong(1);
            glfwCreateWindowSurface(vulkanInstance.getInstance(), window.getWindowHandle(), null, handleBuffer);
            this.surfaceHandle = handleBuffer.get(0);

            surfaceCapabilities = VkSurfaceCapabilitiesKHR.calloc();
            checkVulkan(vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device.getPhysicalDevice().getVkPhysicalDevice(), surfaceHandle, surfaceCapabilities), "Vulkan: Failed to get surface capabilities.");

            surfaceFormat = getSurfaceFormats(device);
        }
    }

    private SurfaceFormat getSurfaceFormats(Device device) {
        int imageFormat;
        int colorSpace;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer result = stack.mallocInt(1);

            checkVulkan(vkGetPhysicalDeviceSurfaceFormatsKHR(device.getPhysicalDevice().getVkPhysicalDevice(), surfaceHandle, result, null), "Vulkan: Failed to get surface format count.");

            int formatCount = result.get(0);
            if(formatCount <= 0) {
                throw new RuntimeException("Vulkan: Unable to retrieve surface formats.");
            }

            VkSurfaceFormatKHR.Buffer surfaceFormats  = VkSurfaceFormatKHR.calloc(formatCount, stack);
            checkVulkan(vkGetPhysicalDeviceSurfaceFormatsKHR(device.getPhysicalDevice().getVkPhysicalDevice(), surfaceHandle, result, surfaceFormats), "Vulkan: Failed to get surface formats.");

            imageFormat = VK_FORMAT_B8G8R8A8_SRGB;
            colorSpace = surfaceFormats.get(0).colorSpace();

            for (int i = 0; i < formatCount; i++) {
                VkSurfaceFormatKHR surfaceFormatKHR = surfaceFormats.get(i);

                if (surfaceFormatKHR.format() == VK_FORMAT_B8G8R8A8_SRGB && surfaceFormatKHR.colorSpace() == VK_COLOR_SPACE_SRGB_NONLINEAR_KHR) {
                    imageFormat = surfaceFormatKHR.format();
                    colorSpace = surfaceFormatKHR.colorSpace();
                    break;
                }
            }
        }

        return new SurfaceFormat(imageFormat, colorSpace);
    }

    @Override
    public void cleanup() {
        Logger.info("Vulkan: Destroying surface");
        surfaceCapabilities.free();
        //vkDestroySurfaceKHR();
    }

    public SurfaceFormat getSurfaceFormat() {
        return surfaceFormat;
    }

    public VkSurfaceCapabilitiesKHR getSurfaceCapabilities() {
        return surfaceCapabilities;
    }

    public long getSurfaceHandle() {
        return surfaceHandle;
    }



    public static class SurfaceFormat {

        private final int imageFormat;
        private final int colorSpace;

        public SurfaceFormat(int imageFormat, int colorSpace) {
            this.imageFormat = imageFormat;
            this.colorSpace = colorSpace;
        }

        public int getColorSpace() {
            return colorSpace;
        }

        public int getImageFormat() {
            return imageFormat;
        }
    }
}
