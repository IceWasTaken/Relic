package net.ice.curio.window.backend.vulkan;

import net.ice.curio.library.vulkan.VulkanContext;
import net.ice.heirloom.Lifecycle;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.tinylog.Logger;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_B8G8R8A8_SRGB;

public class Surface implements Lifecycle {

    private VkSurfaceCapabilitiesKHR surfaceCapabilities;
    private SurfaceFormat surfaceFormat;

    private long surfaceHandle;

    public Surface(VulkanContext vulkanContext, VulkanWindow window) {
        Logger.info("Surface: Creating surface");
        try(MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer surface = stack.mallocLong(1);
            vulkanContext.getInstance().createWindowSurface(window, surface);
            this.surfaceHandle = surface.get(0);

            surfaceCapabilities = VkSurfaceCapabilitiesKHR.calloc();
            checkVulkan(vulkanContext.getPhysicalDevice().getPhysicalDeviceSurfaceCapabilities(this), "Surface: Failed to get surface capabilities");

            surfaceFormat = calcSurfaceFormat(vulkanContext);
        }
    }

    public int getSurfaceCapabilities(VkPhysicalDevice physicalDevice) {
        return KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice, surfaceHandle, surfaceCapabilities);
    }

    public int getSurfaceFormats(VkPhysicalDevice physicalDevice, IntBuffer intBuffer, VkSurfaceFormatKHR.Buffer buffer) {
        return KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surfaceHandle, intBuffer, buffer);
    }

    private SurfaceFormat calcSurfaceFormat(VulkanContext vulkanContext) {
        int format;
        int colorSpace;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer countBuffer = stack.mallocInt(1);
            checkVulkan(vulkanContext.getPhysicalDevice().getSurfaceFormats(this, countBuffer, null), "Surface: Failed to get format count");

            int formatCount = countBuffer.get(0);

            if(formatCount <= 0) {
                throw new RuntimeException("Vulkan: No surface formats retrieved");
            }

            VkSurfaceFormatKHR.Buffer surfaceFormats = VkSurfaceFormatKHR.calloc(formatCount, stack);
            checkVulkan(vulkanContext.getPhysicalDevice().getSurfaceFormats(this, countBuffer, surfaceFormats), "Surface: Failed to get surface formats");

            format = VK_FORMAT_B8G8R8A8_SRGB;
            colorSpace = surfaceFormats.get(0).colorSpace();
            for(VkSurfaceFormatKHR surfaceFormat : surfaceFormats) {
                if (surfaceFormat.format() == VK_FORMAT_B8G8R8A8_SRGB && surfaceFormat.colorSpace() == KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR) {
                    format = surfaceFormat.format();
                    colorSpace = surfaceFormat.colorSpace();
                    break;
                }
            }
        }
        return new SurfaceFormat(format, colorSpace);
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
