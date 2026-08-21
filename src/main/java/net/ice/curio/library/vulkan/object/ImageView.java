package net.ice.curio.library.vulkan.object;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkImageViewCreateInfo;

import java.nio.LongBuffer;

public class ImageView {

//	private final long vkImage;
//	private final long vkImageView;


	public ImageView() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			LongBuffer lb = stack.mallocLong(1);

//			VkImageViewCreateInfo viewCreateInfo = VkImageViewCreateInfo.calloc(stack)
//					.sType$Default()
//					.image()
		}
	}
}
