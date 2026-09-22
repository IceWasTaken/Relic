package net.ice.curio.library.vulkan.object;

import net.ice.curio.graphics.CommandBuffer;
import net.ice.curio.graphics.enums.image.ImageFormat;
import net.ice.curio.graphics.enums.image.ImageType;
import net.ice.curio.graphics.object.resource.Image;
import net.ice.curio.library.vulkan.VulkanContext;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.*;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK13.vkCmdPipelineBarrier2;

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

	public void imageBarrier(
			MemoryStack stack,
			VulkanCommandBuffer commandBuffer,
			int oldLayout,
			int newLayout,
			long srcStage,
			long dstStage,
			long srcAccess,
			long dstAccess
	) {
		VkImageMemoryBarrier2.Buffer imgBarrier = VkImageMemoryBarrier2.calloc(1, stack)
				.sType$Default()
				.oldLayout(oldLayout)
				.newLayout(newLayout)
				.srcStageMask(srcStage)
				.dstStageMask(dstStage)
				.srcAccessMask(srcAccess)
				.dstStageMask(dstAccess)
				.srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
				.dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED)
				.subresourceRange(range -> range
						.aspectMask(aspectMask)
						.baseMipLevel(0)
						.levelCount(VK_REMAINING_MIP_LEVELS)
						.baseArrayLayer(0)
						.layerCount(VK_REMAINING_ARRAY_LAYERS))
				.image(vkImage);

		VkDependencyInfo dependencyInfo = VkDependencyInfo.calloc(stack)
				.sType$Default()
				.pImageMemoryBarriers(imgBarrier);

		vkCmdPipelineBarrier2(commandBuffer.getVkCommandBuffer(), dependencyInfo);
	}

	public VkRenderingAttachmentInfo.Buffer createAttachmentInfo(
			int layout,
			int loadOp,
			int storeOp,
			VkClearValue clearValue
	) {
		return VkRenderingAttachmentInfo.calloc(1)
				.sType$Default()
				.imageView(vkImageView)
				.imageLayout(layout)
				.loadOp(loadOp)
				.storeOp(storeOp)
				.clearValue(clearValue);

	}

	public void cleanup(VulkanContext vulkanContext) {

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
