package net.ice.curio.library.vulkan.object.resource;

import net.ice.curio.graphics.object.resource.GPUBuffer;
import net.ice.curio.graphics.object.resource.Texture;
import net.ice.curio.library.stb.Bitmap;

public class VulkanTexture extends Texture {

    private GPUBuffer buffer;

    protected VulkanTexture(Bitmap image) {
        super(image);
    }

    @Override
    public long getHandle() {
        return 0;
    }
}
