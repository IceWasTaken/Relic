package net.ice.curio.library.vulkan.object;

import net.ice.curio.library.vulkan.VulkanContext;
import net.ice.curio.window.backend.vulkan.VulkanWindow;
import net.ice.heirloom.Lifecycle;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;
import org.tinylog.Logger;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_B8G8R8A8_SRGB;

public class Surface implements Lifecycle {

    private VkSurfaceCapabilitiesKHR surfaceCapabilities;
    private SurfaceFormat surfaceFormat;
    private long vkSurface;

    public Surface(VulkanContext context) {
        Logger.info("[Surface]: Creating surface");
        try(MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer surface = stack.mallocLong(1);
            ((VulkanWindow) context.getCurio().getWindow()).createSurface(context.getInstance().getVkInstance(), surface);

            this.vkSurface = surface.get(0);

            surfaceCapabilities = VkSurfaceCapabilitiesKHR.calloc();
            checkVulkan(
                    KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR(
                            context.getPhysicalDevice().getVkPhysicalDevice(),
                            vkSurface,
                            surfaceCapabilities
                    ),
                    "[Surface]: Failed to get surface capabilities"
            );

            surfaceFormat = calcSurfaceFormat(context);
        }
    }

    private SurfaceFormat calcSurfaceFormat(VulkanContext context) {
        int format;
        int colorSpace;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer countBuffer = stack.mallocInt(1);

            checkVulkan(
                    KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(
                            context.getPhysicalDevice().getVkPhysicalDevice(),
                            vkSurface,
                            countBuffer,
                            null
                    ),
                    "[Surface]: Unable to enumerate formats"
            );

            int formatCount = countBuffer.get(0);
            if(formatCount <= 0) {
                throw new RuntimeException("[Surface]: No surface formats retrieved");
            }

            VkSurfaceFormatKHR.Buffer surfaceFormats = VkSurfaceFormatKHR.calloc(formatCount, stack);

            checkVulkan(
                    KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(
                            context.getPhysicalDevice().getVkPhysicalDevice(),
                            vkSurface,
                            countBuffer,
                            surfaceFormats
                    ),
                    "[Surface]: Unable to retrieve formats"
            );

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

    public VkSurfaceCapabilitiesKHR getSurfaceCapabilities() {
        return surfaceCapabilities;
    }

    long getVkSurface() {
        return vkSurface;
    }

    public record SurfaceFormat(int imageFormat, int colorSpace) {}
}
