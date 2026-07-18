package net.ice.curio.library.vulkan.object.resource;

import net.ice.curio.graphics.enums.image.ImageFormat;
import net.ice.curio.graphics.enums.image.ImageUsage;
import net.ice.curio.graphics.object.resource.Image;
import net.ice.curio.library.vulkan.VulkanContext;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.vulkan.VkImageCreateInfo;

import java.nio.LongBuffer;
import java.util.EnumSet;

import static org.lwjgl.util.vma.Vma.VMA_ALLOCATION_CREATE_DEDICATED_MEMORY_BIT;
import static org.lwjgl.util.vma.Vma.VMA_MEMORY_USAGE_AUTO;
import static org.lwjgl.vulkan.VK10.*;

public final class VulkanImage extends Image {

    private final long vkImage;
    private final long allocation;

    public VulkanImage(ImageInfo info, VulkanImageInfo vulkanImageInfo, VulkanContext vulkanContext) {
        super(info);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkImageCreateInfo imageCreateInfo = VkImageCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO)
                    .imageType(info.imageType.ordinal())
                    .format(getFormat(info.imageFormat))
                    .extent(vkExtent3D -> vkExtent3D
                            .width(info.width)
                            .height(info.height)
                            .depth(1)
                    )
                    .mipLevels(info.mipmapLevels)
                    .arrayLayers(info.layers)
                    .samples(info.sampleCount)
                    .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                    .sharingMode(VK_SHARING_MODE_EXCLUSIVE)
                    .tiling(VK_IMAGE_TILING_OPTIMAL)
                    .usage(getUsage(info.imageUsage));

            VmaAllocationCreateInfo allocationCreateInfo = VmaAllocationCreateInfo.calloc(1, stack)
                    .get(0)
                    .usage(VMA_MEMORY_USAGE_AUTO)
                    .flags(vulkanImageInfo.vmaFlags)
                    .priority(1.0f);

            PointerBuffer allocation = stack.callocPointer(1);
            LongBuffer longBuffer = stack.mallocLong(1);

            vulkanContext.getVMAInstance().createImage(imageCreateInfo, allocationCreateInfo, longBuffer, allocation);

            this.vkImage = longBuffer.get(0);
            this.allocation = allocation.get(0);
        }
    }

    public VulkanImage(ImageInfo imageInfo, VulkanContext context) {
        this(imageInfo, VulkanImageInfo.DEFAULT_INFO, context);
    }

    public int getFormat(ImageFormat format) {
        return switch (format) {
            case RGBA8 -> VK_FORMAT_R8G8B8A8_SRGB;
        };
    }

    public int getUsage(EnumSet<ImageUsage> usages) {
        int result = 0;

        for (ImageUsage usage : usages) {
            result = 1 << usage.ordinal();
        }

        return result;
    }

    public void cleanup(VulkanContext vulkanContext) {
        vulkanContext.getVMAInstance().destroyImage(vkImage, allocation);
        super.cleanup();
    }

    public static class VulkanImageInfo {

        private static final VulkanImageInfo DEFAULT_INFO = new VulkanImageInfo();

        private int vmaFlags;

        public VulkanImageInfo() {
            this.vmaFlags = VMA_ALLOCATION_CREATE_DEDICATED_MEMORY_BIT;
        }

        public VulkanImageInfo vmaFlags(int flags) {
            this.vmaFlags = flags;
            return this;
        }
    }


}
