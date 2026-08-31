package net.ice.curio.library.vulkan.object;

import net.ice.curio.graphics.enums.image.ImageFormat;
import net.ice.curio.graphics.enums.image.ImageUsage;
import net.ice.curio.graphics.object.resource.Image;
import net.ice.curio.graphics.object.resource.Texture;
import net.ice.curio.library.stb.Bitmap;
import net.ice.curio.library.vulkan.VulkanContext;

import java.nio.ByteBuffer;
import java.util.EnumSet;

import static net.ice.curio.graphics.enums.BufferUsage.TRANSFER_SOURCE;
import static net.ice.curio.graphics.enums.image.ImageUsage.*;
import static org.lwjgl.util.vma.Vma.VMA_ALLOCATION_CREATE_HOST_ACCESS_SEQUENTIAL_WRITE_BIT;
import static org.lwjgl.util.vma.Vma.VMA_MEMORY_USAGE_AUTO;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT;

public class VulkanTexture extends Texture {

    private boolean transparent;
    private boolean recordedTransition;

    private VulkanBuffer stagingBuffer;

    private final String id;
    private final VulkanImage image;
    //private final ImageView imageView;


    public VulkanTexture(VulkanContext vulkanContext, String id, Bitmap bitmap, ImageFormat format) {
        super(bitmap);

        this.id = id;
        this.recordedTransition = false;
        this.transparent = bitmap.isTransparent();

        createStagingBuffer(vulkanContext, bitmap.getData());

        //copied straight from GLTexture.java
        int levels = (int) Math.floor(log2(Math.max(width, height))) + 1;

        this.image = new VulkanImage(
                vulkanContext,
                new Image.ImageInfo()
                        .width(bitmap.getWidth())
                        .height(bitmap.getHeight())
                        .usage(EnumSet.of(TRANSFER_SRC, TRANSFER_DST, SAMPLED))
                        .format(format)
                        .mipmapLevels(levels)
        );

//        ImageView.ImageViewData imageViewData = new ImageView.ImageViewData()
//                .format(image.getFormat())
    }

    private void createStagingBuffer(VulkanContext context, ByteBuffer data) {
        int size = data.remaining();

        this.stagingBuffer = new VulkanBuffer(context, size,
                VK_BUFFER_USAGE_TRANSFER_SRC_BIT,
                VMA_MEMORY_USAGE_AUTO,
                VMA_ALLOCATION_CREATE_HOST_ACCESS_SEQUENTIAL_WRITE_BIT,
                VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT
        );

        stagingBuffer.map(context)
                .put(data)
                .flip();
        stagingBuffer.unmap(context);
    }

    @Override
    public long getHandle() {
        return 0;
    }
}
