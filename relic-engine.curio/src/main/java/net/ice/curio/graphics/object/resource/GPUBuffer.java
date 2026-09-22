package net.ice.curio.graphics.object.resource;

import net.ice.curio.graphics.enums.BufferAccess;
import net.ice.curio.graphics.enums.BufferFlags;
import net.ice.curio.graphics.enums.BufferUsage;

import java.nio.ByteBuffer;
import java.util.EnumSet;


public abstract class GPUBuffer {

    protected final long size;
    protected final EnumSet<BufferFlags> flags;

    protected GPUBuffer(long size, EnumSet<BufferFlags> flags) {
        this.size = size;
        this.flags = flags;
    }

    public long getSize() {
        return size;
    }

    //public abstract void upload(ByteBuffer data, long offset);

    protected abstract ByteBuffer map(long offset, EnumSet<BufferFlags> bufferAccess);


    public abstract void unmap();

    public abstract void destroy();
}
