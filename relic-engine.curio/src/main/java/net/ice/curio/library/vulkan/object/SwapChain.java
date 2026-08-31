package net.ice.curio.library.vulkan.object;

import net.ice.curio.library.vulkan.VulkanContext;
import net.ice.curio.window.Window;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class SwapChain {

	private final ImageView[] imageViews;
	private final int imageCount;
	private final VkExtent2D swapChainExtent;
	private final long vkSwapChain;

	public SwapChain(
			VulkanContext context,
			int requestedImages,
			boolean vysnc
	) {
		Logger.debug("[SwapChain]: Creating SwapChain");

		try(MemoryStack stack = MemoryStack.stackPush()) {
			VkSurfaceCapabilitiesKHR surfaceCapabilities = context.getSurface().getSurfaceCapabilities();

			int requiredImages = calcImageCount(surfaceCapabilities, requestedImages);
			this.swapChainExtent = calcExtent(context, surfaceCapabilities);

			Surface.SurfaceFormat surfaceFormat = context.getSurface().getSurfaceFormat();
			VkSwapchainCreateInfoKHR swapchainCreateInfo = VkSwapchainCreateInfoKHR.calloc(stack)
					.sType$Default()
					.surface(context.getSurface().getVkSurface())
					.minImageCount(requiredImages)
					.imageFormat(surfaceFormat.imageFormat())
					.imageColorSpace(surfaceFormat.colorSpace())
					.imageExtent(swapChainExtent)
					.imageArrayLayers(1)
					.imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT)
					.preTransform(surfaceCapabilities.currentTransform())
					.compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR)
					.clipped(true)
					.presentMode(vysnc ? VK_PRESENT_MODE_FIFO_KHR : VK_PRESENT_MODE_IMMEDIATE_KHR);

			LongBuffer lb = stack.mallocLong(1);

			checkVulkan(
					vkCreateSwapchainKHR(
							context.getDevice().getVkDevice(),
							swapchainCreateInfo,
							null,
							lb
					),
					"[SwapChain]: Failed to create swap chain"
			);

			this.vkSwapChain = lb.get(0);

			this.imageViews = createImageViews(context, stack, surfaceFormat.imageFormat());
			this.imageCount = imageViews.length;
		}
	}

	public boolean presentImage(Queue queue, Semaphore renderCompleteSemaphore, int index) {
		boolean shouldResize = false;
		try(MemoryStack stack = MemoryStack.stackPush()) {
			VkPresentInfoKHR present = VkPresentInfoKHR.calloc(stack)
					.sType$Default()
					.pWaitSemaphores(stack.longs(renderCompleteSemaphore.getVkSemaphore()))
					.swapchainCount(1)
					.pSwapchains(stack.longs(vkSwapChain))
					.pImageIndices(stack.ints(index));

			int code = vkQueuePresentKHR(queue.getVkQueue(), present);

			switch(code) {
				case VK_ERROR_OUT_OF_DATE_KHR -> shouldResize = true;
				case VK_SUBOPTIMAL_KHR, VK_SUCCESS -> {}
				default -> throw new RuntimeException("[SwapChain]: Failed to acquire image: " + code);
			}
		}
		return shouldResize;
	}

	public int acquireNextImage(VulkanContext context, Semaphore imgSemaphore) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer ib = stack.mallocInt(1);

			int code = vkAcquireNextImageKHR(
					context.getDevice().getVkDevice(),
					vkSwapChain,
					0L,
					imgSemaphore.getVkSemaphore(),
					MemoryUtil.NULL,
					ib
			);

			switch(code) {
				case VK_ERROR_OUT_OF_DATE_KHR -> {
					return -1;
				}
				case VK_SUBOPTIMAL_KHR, VK_SUCCESS -> {
					return ib.get(0);
				}
				default -> throw new RuntimeException("[SwapChain]: Failed to acquire image: " + code);
			}
		}
	}

	private int calcImageCount(VkSurfaceCapabilitiesKHR surfaceCapabilities, int requestedImages) {
		int maxImages = surfaceCapabilities.maxImageCount();
		int minImages = surfaceCapabilities.minImageCount();
		int result = minImages;

		if(maxImages != 0) {
			result = Math.min(requestedImages, minImages);
		}

		result = Math.max(result, maxImages);

		Logger.debug(
				"[SwapChain]: Requested {} images, got {} images. Surface capable of {} minImages, {} maxImages",
				requestedImages,
				result,
				minImages,
				maxImages
		);

		return result;
	}

	private VkExtent2D calcExtent(VulkanContext context, VkSurfaceCapabilitiesKHR surfaceCapabilities) {
		VkExtent2D result = VkExtent2D.calloc();
		if(surfaceCapabilities.currentExtent().width() == 0xFFFF_FFFF) {
			//undefined
			Window window = context.getCurio().getWindow();

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

	private ImageView[] createImageViews(VulkanContext context, MemoryStack stack, int format) {
		IntBuffer ib = stack.mallocInt(1);

		//standard enumeration
		checkVulkan(
				vkGetSwapchainImagesKHR(
						context.getDevice().getVkDevice(),
						vkSwapChain,
						ib,
						null
				), "[SwapChain]: Failed to enumerate SwapChain images"
		);

		int imageCount = ib.get(0);
		LongBuffer scImages = stack.mallocLong(imageCount);

		checkVulkan(
				vkGetSwapchainImagesKHR(
						context.getDevice().getVkDevice(),
						vkSwapChain,
						ib,
						scImages
				), "[SwapChain]: Failed to enumerate SwapChain images"
		);

		ImageView[] result = new ImageView[imageCount];
		ImageView.ImageViewData imageViewData = new ImageView.ImageViewData().format(format).aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);

		for (int i = 0; i < imageCount; i++) {
			result[i] = new ImageView(context, scImages.get(i), imageViewData);
		}

		return result;
	}

	public int getImageCount() {
		return imageCount;
	}

	public ImageView[] getImageViews() {
		return imageViews;
	}
}
