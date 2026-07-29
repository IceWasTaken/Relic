package net.ice.curio.library.opengl.object.buffer;

import net.ice.curio.library.opengl.wrapper.enums.Usage;
import net.ice.heirloom.Lifecycle;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.*;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL45.*;

public class GLBuffer implements Lifecycle {

    private final int handle;

    /// [GL Wiki Reference](https://wikis.khronos.org/opengl/Buffer_Object)
    public GLBuffer() {
        this.handle = glCreateBuffers();
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBindBufferBase.xhtml)
    public void bindBase(int target, int index) {
        glBindBufferBase(target, index, handle);
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glDeleteBuffers.xhtml)
    @Override
    public void cleanup() {
        glDeleteBuffers(handle);
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferData.xhtml)

    public void bufferData(ByteBuffer data, Usage usage) {
        glNamedBufferData(handle, data, usage.getGLEnum());
    }
    public void bufferData(ShortBuffer data, Usage usage) {
        glNamedBufferData(handle, data, usage.getGLEnum());
    }
    public void bufferData(IntBuffer data, Usage usage) {
        glNamedBufferData(handle, data, usage.getGLEnum());
    }
    public void bufferData(LongBuffer data, Usage usage) {
        glNamedBufferData(handle, data, usage.getGLEnum());
    }
    public void bufferData(FloatBuffer data, Usage usage) {
        glNamedBufferData(handle, data, usage.getGLEnum());
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferSubData.xhtml)

    public GLBuffer bufferSubData(long offset, int data) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            glNamedBufferSubData(handle, offset, stack.mallocInt(1).put(data).position(0));
        }
        return this;
    }

    public GLBuffer bufferSubData(long offset, float data) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            glNamedBufferSubData(handle, offset, stack.mallocFloat(1).put(data).position(0));
        }
        return this;
    }

    public GLBuffer bufferSubData(long offset, short data) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            glNamedBufferSubData(handle, offset, stack.mallocShort(1).put(data).position(0));
        }
        return this;
    }

    public GLBuffer bufferSubData(long offset, double data) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            glNamedBufferSubData(handle, offset, stack.mallocDouble(1).put(data).position(0));
        }
        return this;
    }

    public GLBuffer bufferSubData(long offset, long data) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            glNamedBufferSubData(handle, offset, stack.mallocLong(1).put(data).position(0));
        }
        return this;
    }

    public GLBuffer bufferSubData(long offset, byte data) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            glNamedBufferSubData(handle, offset, stack.malloc(1).put(data).position(0));
        }
        return this;
    }

    public GLBuffer bufferSubData(long offset, int[] data) {
        glNamedBufferSubData(handle, offset, data);
        return this;
    }

    public GLBuffer bufferSubData(long offset, float[] data) {
        glNamedBufferSubData(handle, offset, data);
        return this;
    }

    public GLBuffer bufferSubData(long offset, short[] data) {
        glNamedBufferSubData(handle, offset, data);
        return this;
    }

    public GLBuffer bufferSubData(long offset, double[] data) {
        glNamedBufferSubData(handle, offset, data);
        return this;
    }

    public GLBuffer bufferSubData(long offset, long[] data) {
        glNamedBufferSubData(handle, offset, data);
        return this;
    }

    public GLBuffer bufferSubData(long offset, byte[] data) {
        glNamedBufferSubData(handle, offset, ByteBuffer.wrap(data));
        return this;
    }

    public GLBuffer bufferSubData(long offset, IntBuffer data) {
        glNamedBufferSubData(handle, offset, data);
        return this;
    }

    public GLBuffer bufferSubData(long offset, FloatBuffer data) {
        glNamedBufferSubData(handle, offset, data);
        return this;
    }

    public GLBuffer bufferSubData(long offset, ShortBuffer data) {
        glNamedBufferSubData(handle, offset, data);
        return this;
    }

    public GLBuffer bufferSubData(long offset, DoubleBuffer data) {
        glNamedBufferSubData(handle, offset, data);
        return this;
    }

    public GLBuffer bufferSubData(long offset, LongBuffer data) {
        glNamedBufferSubData(handle, offset, data);
        return this;
    }

    public GLBuffer bufferSubData(long offset, ByteBuffer data) {
        glNamedBufferSubData(handle, offset, data);
        return this;
    }

    public GLBuffer bufferSubData(long offset, Vector2f data) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            glNamedBufferSubData(handle, offset, data.get(stack.mallocFloat(2)));
        }
        return this;
    }


    public GLBuffer bufferSubData(long offset, Vector3f data) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            glNamedBufferSubData(handle, offset, data.get(stack.mallocFloat(3)));
        }
        return this;
    }

    public GLBuffer bufferSubData(long offset, Vector4f data) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            glNamedBufferSubData(handle, offset, data.get(stack.mallocFloat(4)));
        }
        return this;
    }

    public GLBuffer bufferSubData(long offset, Matrix4f data) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            glNamedBufferSubData(handle, offset, data.get(stack.mallocFloat(16)));
        }
        return this;
    }

    public final int getHandle() {
        return handle;
    }
}

