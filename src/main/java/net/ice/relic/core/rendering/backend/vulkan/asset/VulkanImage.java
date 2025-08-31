package net.ice.relic.core.rendering.backend.vulkan.asset;

import net.ice.relic.common.asset.image.Image;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.vulkan.VkImageCreateInfo;

import java.nio.LongBuffer;

import static org.lwjgl.util.vma.Vma.VMA_ALLOCATION_CREATE_DEDICATED_MEMORY_BIT;
import static org.lwjgl.util.vma.Vma.VMA_MEMORY_USAGE_AUTO;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanImage extends Image {

    private final long handle;

    public VulkanImage() {
        super("dsaghgbfshgdfhgfd");
        try(MemoryStack stack = MemoryStack.stackPush()) {

            VkImageCreateInfo imageCreateInfo = VkImageCreateInfo.calloc(stack)
                    .sType$Default()
                    .imageType(VK_IMAGE_TYPE_2D)
                    .format(VK_FORMAT_R8G8B8A8_SRGB)
                    .extent(val -> val
                            .width(width)
                            .height(height)
                            .depth(1)
                    )
                    .mipLevels(1)
                    .arrayLayers(1)
                    .samples(1)
                    .initialLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                    .sharingMode(VK_SHARING_MODE_EXCLUSIVE)
                    .tiling(VK_IMAGE_TILING_OPTIMAL);
                    //.usage();

            VmaAllocationCreateInfo.calloc(1, stack)
                    .get(0)
                    .usage(VMA_MEMORY_USAGE_AUTO)
                    .flags(VMA_ALLOCATION_CREATE_DEDICATED_MEMORY_BIT)
                    .priority(1.0f);

            PointerBuffer pointerBuffer = stack.callocPointer(1);
            LongBuffer longBuffer = stack.mallocLong(1);
            handle = pointerBuffer.get(0);



        }
    }
}
