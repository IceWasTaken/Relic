package net.ice.relic.core.rendering.backend.vulkan;

import net.ice.relic.core.rendering.backend.vulkan.device.Device;
import net.ice.relic.core.rendering.backend.vulkan.sync.Semaphore;
import net.ice.relic.core.window.Window;
import net.ice.relic.core.window.backend.vulkan.Surface;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSwapchainCreateInfoKHR;
import org.tinylog.Logger;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static net.ice.relic.core.rendering.backend.vulkan.VulkanUtil.checkVulkan;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR;
import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_IMMEDIATE_KHR;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class SwapChain {

    private int imageCount;
    private long swapChainHandle;

    private VkExtent2D extent2D;
    private ImageView[] imageViews;

    private final int requestedImages;
    private boolean vsync;

    public SwapChain(boolean vsync, int requestedImages) {
        this.requestedImages = requestedImages;
        this.vsync = vsync;
    }

    public void init(Window window, Device device, Surface surface) {
        Logger.info("Vulkan: Creating swap chain.");
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkSurfaceCapabilitiesKHR surfaceCapabilities = surface.getSurfaceCapabilities();

            int imageCount = getImageCount(surfaceCapabilities, requestedImages);
            extent2D = getSwapChainExtent(window, surfaceCapabilities);

            Surface.SurfaceFormat surfaceFormat = surface.getSurfaceFormat();
            VkSwapchainCreateInfoKHR swapchainCreateInfo = VkSwapchainCreateInfoKHR.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
                    .surface(surface.getSurfaceHandle())
                    .minImageCount(imageCount)
                    .imageFormat(surfaceFormat.getImageFormat())
                    .imageColorSpace(surfaceFormat.getColorSpace())
                    .imageExtent(extent2D)
                    .imageArrayLayers(1)
                    .imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT)
                    .preTransform(surfaceCapabilities.currentTransform())
                    .compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR)
                    .clipped(true);


//            if(vsync) {
//                swapchainCreateInfo.presentMode(VK_PRESENT_MODE_FIFO_KHR);
//            } else {
//                swapchainCreateInfo.presentMode(VK_PRESENT_MODE_IMMEDIATE_KHR);
//            }

            swapchainCreateInfo.presentMode(VK_PRESENT_MODE_IMMEDIATE_KHR);

            LongBuffer handleBuffer = stack.mallocLong(1);
            checkVulkan(vkCreateSwapchainKHR(device.getDevice(), swapchainCreateInfo, null, handleBuffer), "Vulkan: Failed to create swapchain");
            swapChainHandle = handleBuffer.get(0);

            imageViews = createImageViews(stack, device, swapChainHandle, surfaceFormat.getImageFormat());
            this.imageCount = imageViews.length;
        }
    }

    public int getNextImage(Device device, Semaphore imageAqSem) {
        int index;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer indexBuff = stack.mallocInt(1);
            int err = vkAcquireNextImageKHR(device.getDevice(), swapChainHandle, ~0L, imageAqSem.getSemaphoreHandle(), NULL, indexBuff);
            if (err == VK_ERROR_OUT_OF_DATE_KHR) {
                return -1;
            } else if (err == VK_SUBOPTIMAL_KHR) {
            } else if (err != VK_SUCCESS) {
                throw new RuntimeException("Failed to acquire image: " + err);
            }
            index = indexBuff.get(0);
        }
        return index;
    }

    private ImageView[] createImageViews(MemoryStack stack, Device device, long swapChain, int format) {
        IntBuffer ip = stack.mallocInt(1);
        checkVulkan(vkGetSwapchainImagesKHR(device.getDevice(), swapChain, ip, null), "Vulkan: Failed to get number of surface images");
        int numImages = ip.get(0);

        LongBuffer swapChainImages = stack.mallocLong(numImages);
        checkVulkan(vkGetSwapchainImagesKHR(device.getDevice(), swapChain, ip, swapChainImages), "Vulkan: Failed to get surface images");

        var result = new ImageView[numImages];
        var imageViewData = new ImageView.Data().format(format).aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
        for (int i = 0; i < numImages; i++) {
            result[i] = new ImageView().init(device, swapChainImages.get(i), imageViewData);
        }

        return result;
    }

    private int getImageCount(VkSurfaceCapabilitiesKHR surfaceCapabilities, int requestedCount) {
        int max = surfaceCapabilities.maxImageCount();
        int min = surfaceCapabilities.minImageCount();
        int result = min;
        if(max != 0) {
            result = Math.min(requestedCount, max);
        }
        result = Math.max(result, min);
        Logger.info("Vulkan: Requested [{}] images, got [{}] images. Surface capabilities, maxImages: [{}], minImages [{}]", requestedCount, result, max, min);

        return result;
    }

    private VkExtent2D getSwapChainExtent(Window window, VkSurfaceCapabilitiesKHR surfaceCapabilities) {
        VkExtent2D result = VkExtent2D.calloc();
        if(surfaceCapabilities.currentExtent().width() == 0xFFFFFFFF) {
            int width = Math.min(window.getWidth(), surfaceCapabilities.maxImageExtent().width());
            width = Math.max(width, surfaceCapabilities.minImageExtent().width());

            int height = Math.min(window.getHeight(), surfaceCapabilities.maxImageExtent().height());
            height = Math.max(height, surfaceCapabilities.minImageExtent().height());

            result.width(width);
            result.height(height);
        } else {
            result.set(surfaceCapabilities.currentExtent());
        }
        return result;
    }

    public boolean presentImage(Queue queue, Semaphore renderCompleteSem, int imageIndex) {
        boolean resize = false;
        try (var stack = MemoryStack.stackPush()) {
            VkPresentInfoKHR present = VkPresentInfoKHR.calloc(stack)
                    .sType$Default()
                    .pWaitSemaphores(stack.longs(renderCompleteSem.getSemaphoreHandle()))
                    .swapchainCount(1)
                    .pSwapchains(stack.longs(swapChainHandle))
                    .pImageIndices(stack.ints(imageIndex));

            int err = vkQueuePresentKHR(queue.getVkQueue(), present);
            if (err == VK_ERROR_OUT_OF_DATE_KHR) {
                resize = true;
            } else if (err == VK_SUBOPTIMAL_KHR) {
                // Not optimal but swap chain can still be used
            } else if (err != VK_SUCCESS) {
                throw new RuntimeException("Failed to present KHR: " + err);
            }
        }
        return resize;
    }

    public ImageView getImageView(int pos) {
        return imageViews[pos];
    }

    public int getImageCount() {
        return imageCount;
    }

    public VkExtent2D getExtent2D() {
        return extent2D;
    }
}
