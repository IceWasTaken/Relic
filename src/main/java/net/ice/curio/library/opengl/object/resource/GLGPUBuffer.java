package net.ice.curio.library.opengl.object.resource;

import net.ice.curio.graphics.enums.BufferUsage;
import net.ice.curio.graphics.object.resource.GPUBuffer;

import java.nio.ByteBuffer;
import java.util.EnumSet;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL45.*;

public final class GLGPUBuffer extends GPUBuffer {

    private final int bufferID;

    public GLGPUBuffer(long size, BufferUsage usage) {
        super(size, EnumSet.of(usage));

        this.bufferID = glCreateBuffers();

        glNamedBufferData(bufferID, size, GL_DYNAMIC_DRAW);
    }

    @Override
    public void upload(ByteBuffer data, long offset) {
        glNamedBufferSubData(bufferID, offset, data);
    }

    @Override
    public ByteBuffer map() {
        return glMapNamedBuffer(bufferID, GL_WRITE_ONLY);
    }

    @Override
    public void unmap() {
        glUnmapNamedBuffer(bufferID);
    }

    @Override
    public void destroy() {
        glDeleteBuffers(bufferID);
    }
}
