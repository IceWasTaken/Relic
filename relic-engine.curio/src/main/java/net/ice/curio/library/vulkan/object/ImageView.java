package net.ice.curio.library.vulkan.object;

import net.ice.curio.graphics.enums.image.ImageFormat;
import net.ice.curio.graphics.enums.image.ImageType;
import net.ice.curio.graphics.object.resource.Image;
import net.ice.curio.library.vulkan.VulkanContext;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkImageViewCreateInfo;

import java.nio.LongBuffer;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.*;
import static org.lwjgl.vulkan.VK10.*;

public class ImageView {

	//private final boolean depthImage;

	private final int aspectMask;
	//private final int layerCount;
	private final int mipmapLevels;

	private final long vkImage;
	private final long vkImageView;

	public ImageView(VulkanContext vulkanContext, VulkanImage image, ImageViewData imageViewData) {
		this(vulkanContext, image.getVkImage(), imageViewData);
	}

	public ImageView(
			VulkanContext vulkanContext,
			long image,
			ImageViewData imageViewData
			//boolean depthImage
	) {
		//this.depthImage = depthImage;
		this.aspectMask = imageViewData.aspectMask;
		//this.layerCount = imageViewData.layerCount;
		this.mipmapLevels = imageViewData.mipmapLevels;

		this.vkImage = image;

		try(MemoryStack stack = MemoryStack.stackPush()) {
			LongBuffer lb = stack.mallocLong(1);

			VkImageViewCreateInfo viewCreateInfo = VkImageViewCreateInfo.calloc(stack)
					.sType$Default()
					.image(image)
					.viewType(getImageViewType(imageViewData.viewType))
					.format(getFormat(imageViewData.format))
					.subresourceRange(sr -> sr
							.aspectMask(aspectMask)
							.baseMipLevel(0)
							.levelCount(mipmapLevels)
							.baseArrayLayer(imageViewData.baseArrayLayer)
							.layerCount(imageViewData.layerCount)
					);

			checkVulkan(
					vkCreateImageView(
							vulkanContext.getDevice().getVkDevice(),
							viewCreateInfo,
							null,
							lb
					),
					"[ImageView]: Failed to create image view"
			);

			vkImageView	= lb.get(0);
		}
	}

	public int getAspectMask() {
		return aspectMask;
	}

	public int getMipmapLevels() {
		return mipmapLevels;
	}

	long getVkImage() {
		return vkImage;
	}

	long getVkImageView() {
		return vkImageView;
	}


	public static class ImageViewData {

		private int aspectMask;
		private int baseArrayLayer;
		private int layerCount;
		private int mipmapLevels;
		private ImageFormat format;
		private ImageType viewType;

		public ImageViewData() {
			this.baseArrayLayer = 0;
			this.layerCount = 1;
			this.mipmapLevels = 1;
			this.viewType = ImageType.IMAGE_2D;
		}

		public ImageView.ImageViewData aspectMask(int aspectMask) {
			this.aspectMask = aspectMask;
			return this;
		}

		public ImageView.ImageViewData baseArrayLayer(int baseArrayLayer) {
			this.baseArrayLayer = baseArrayLayer;
			return this;
		}

		public ImageView.ImageViewData format(ImageFormat format) {
			this.format = format;
			return this;
		}

		public ImageView.ImageViewData format(int format) {
			this.format = switch(format) {
				case VK_FORMAT_R8G8B8A8_SRGB -> ImageFormat.RGBA8;
				case VK_FORMAT_B8G8R8A8_SRGB -> ImageFormat.BGRA8;
				default -> throw new IllegalStateException("Unexpected value: " + format);
			};
			return this;
		}

		public ImageView.ImageViewData layerCount(int layerCount) {
			this.layerCount = layerCount;
			return this;
		}

		public ImageView.ImageViewData mipmapLevels(int mipmapLevels) {
			this.mipmapLevels = mipmapLevels;
			return this;
		}

		public ImageView.ImageViewData viewType(ImageType viewType) {
			this.viewType = viewType;
			return this;
		}
	}
}
