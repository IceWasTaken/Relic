package net.ice.curio.graphics.object.resource;

import net.ice.curio.graphics.enums.BufferUsage;

import java.nio.ByteBuffer;
import java.util.EnumSet;

/// What it sounds like. An area of GPU memory that can be written to. <p>
/// OpenGL implementation: {@link net.ice.curio.library.opengl.object.resource.GLGPUBuffer} <p>
/// Vulkan implementation: {@link net.ice.curio.library.vulkan.object.resource.VulkanGPUBuffer} <p>
public abstract class GPUBuffer {

    protected final long size;
    protected final EnumSet<BufferUsage> usage;

    protected GPUBuffer(long size, EnumSet<BufferUsage> usage) {
        this.size = size;
        this.usage = usage;
    }

    public long getSize() {
        return size;
    }

    public abstract void upload(ByteBuffer data, long offset);

    public abstract ByteBuffer map();
    public abstract void unmap();

    public abstract void destroy();
}
