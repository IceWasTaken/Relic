package net.ice.curio.library.opengl.object.resource;

import net.ice.curio.graphics.enums.BufferAccess;
import net.ice.curio.graphics.enums.BufferUsage;
import net.ice.curio.graphics.object.resource.GPUBuffer;

import java.nio.ByteBuffer;
import java.util.EnumSet;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL45.*;

//public final class GLGPUBuffer extends GPUBuffer {
//
//    private final int bufferID;
//
//    public GLGPUBuffer(long size, EnumSet<BufferUsage> usages) {
//        super(size, usages);
//
//        this.bufferID = glCreateBuffers();
//    }
//
//    @Override
//    public void upload(ByteBuffer data, long offset) {
//        glNamedBufferSubData(bufferID, offset, data);
//    }
//
//    @Override
//    public ByteBuffer map(BufferAccess bufferAccess) {
//        return glMapNamedBuffer(bufferID, GL_WRITE_ONLY);
//    }
//
//    @Override
//    public void unmap() {
//        glUnmapNamedBuffer(bufferID);
//    }
//
//    @Override
//    public void destroy() {
//        glDeleteBuffers(bufferID);
//    }
//}
