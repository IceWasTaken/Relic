package net.ice.curio.library.vulkan.object;

import net.ice.curio.graphics.enums.image.ImageFormat;
import net.ice.curio.graphics.enums.image.ImageUsage;
import net.ice.curio.graphics.object.resource.Image;
import net.ice.curio.library.vulkan.VulkanContext;

import java.util.EnumSet;

import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_DEPTH_BIT;

public class Attachment {

	private final VulkanImage image;
	private final ImageView imageView;
	private boolean isDepth;

	public Attachment(
			VulkanContext ctx,
			int width,
			int height,
			ImageFormat format,
			EnumSet<ImageUsage> usage
	) {
		usage.add(ImageUsage.SAMPLED);
		this.image = new VulkanImage(
				ctx,
				new Image
						.ImageInfo()
						.width(width)
						.height(height)
						.usage(usage)
						.format(format)
		);

		int aspectMask = 0;
		if((usage.contains(ImageUsage.COLOR_ATTACHMENT))) {
			aspectMask = VK_IMAGE_ASPECT_COLOR_BIT;
			isDepth = false;
		}
		if((usage.contains(ImageUsage.DEPTH_STENCIL_ATTACHMENT))) {
			aspectMask = VK_IMAGE_ASPECT_DEPTH_BIT;
			isDepth = true;
		}


		ImageView.ImageViewData imageViewData = new ImageView.ImageViewData().format(format).aspectMask(aspectMask);
		this.imageView = new ImageView(ctx, image, imageViewData);
	}

	public void cleanup(VulkanContext ctx) {
		imageView.cleanup(ctx);
		image.cleanup();
	}

	public ImageView getImageView() {
		return imageView;
	}

	public VulkanImage getImage() {
		return image;
	}

	public boolean isDepth() {
		return isDepth;
	}
}
