package net.ice.curio.library.vulkan.object;

import net.ice.curio.graphics.object.resource.Image;
import net.ice.curio.library.vulkan.VulkanContext;

import net.ice.curio.library.vulkan.utils.VulkanUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.vulkan.VkImageCreateInfo;

import java.nio.LongBuffer;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.*;
import static org.lwjgl.util.vma.Vma.*;
import static org.lwjgl.vulkan.VK10.*;

public final class VulkanImage extends Image {

    private final int format;

    private final long vkImage;
    private final long allocation;

    public VulkanImage(VulkanContext vulkanContext, ImageInfo imageInfo) {
	    super(vulkanContext, imageInfo);

        this.format = VulkanUtils.getFormat(imageInfo.getImageFormat());

		try(MemoryStack stack = MemoryStack.stackPush()) {


            VkImageCreateInfo imageCreateInfo = VkImageCreateInfo.calloc(stack)
                    .sType$Default()
                    .imageType(getImageType(imageType))
                    .format(VulkanUtils.getFormat(imageInfo.getImageFormat()))
                    .extent(ex -> ex.width(width).height(height).depth(1))
                    .mipLevels(mipmapLevels)
                    .arrayLayers(imageInfo.getLayers())
                    .samples(VK_SAMPLE_COUNT_1_BIT)

                    .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                    .sharingMode(VK_SHARING_MODE_EXCLUSIVE)
                    .tiling(VK_IMAGE_TILING_OPTIMAL)

                    .usage(VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT);

            VmaAllocationCreateInfo allocationCreateInfo = VmaAllocationCreateInfo.calloc(1, stack)
                    .get(0)
                    .usage(VMA_MEMORY_USAGE_AUTO)
                    .flags(VMA_ALLOCATION_CREATE_DEDICATED_MEMORY_BIT)
                    .priority(1.0f);

            PointerBuffer allocation = stack.callocPointer(1);
            LongBuffer longBuffer = stack.mallocLong(1);

            checkVulkan(vmaCreateImage(
                    vulkanContext.getVMAInstance().getVmaHandle(),
                    imageCreateInfo,
                    allocationCreateInfo,
                    longBuffer,
                    allocation,
                    null
            ), "[VulkanImage]: Failed to create image");

            this.vkImage = longBuffer.get(0);
            this.allocation = allocation.get(0);
        }
    }


    public void cleanup(VulkanContext vulkanContext) {
        //vulkanContext.getVMAInstance().(vkImage, allocation);
        super.cleanup();
    }

    public int getFormat() {
        return format;
    }

    long getVkImage() {
        return vkImage;
    }
}
