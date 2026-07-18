package net.ice.curio.library.opengl.object.buffer;

import net.ice.curio.library.opengl.wrapper.enums.BufferTarget;
import net.ice.curio.library.opengl.wrapper.enums.Usage;
import net.ice.heirloom.Lifecycle;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.*;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindBufferBase;

@Deprecated
public abstract class GLBuffer implements Lifecycle {

    private final int handle;
    private final BufferTarget target;

    /// [GL Wiki Reference](https://wikis.khronos.org/opengl/Buffer_Object)
    protected GLBuffer(BufferTarget bufferTarget) {
        this.handle = glGenBuffers();
        this.target = bufferTarget;
    }

    /// [GL Wiki Reference](https://wikis.khronos.org/opengl/Buffer_Object)
    protected GLBuffer(int target) {
        this(BufferTarget.fromValue(target));
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBindBuffer.xhtml)
    public void bind() {
        glBindBuffer(target.getGLEnum(), handle);
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBindBuffer.xhtml)
    public void unbind() {
        glBindBuffer(target.getGLEnum(), 0);
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBindBufferBase.xhtml)
    public void bindBase(int index) {
        glBindBufferBase(target.getGLEnum(), index, handle);
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glDeleteBuffers.xhtml)
    @Override
    public void cleanup() {
        glDeleteBuffers(handle);
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferData.xhtml)
    public void bufferData(ByteBuffer data, Usage usage) {
        glBufferData(target.getGLEnum(), data, usage.getGLEnum());
    }
    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferData.xhtml)
    public void bufferData(ShortBuffer data, Usage usage) {
        glBufferData(target.getGLEnum(), data, usage.getGLEnum());
    }
    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferData.xhtml)
    public void bufferData(IntBuffer data, Usage usage) {
        glBufferData(target.getGLEnum(), data, usage.getGLEnum());
    }
    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferData.xhtml)
    public void bufferData(LongBuffer data, Usage usage) {
        glBufferData(target.getGLEnum(), data, usage.getGLEnum());
    }
    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferData.xhtml)
    public void bufferData(FloatBuffer data, Usage usage) {
        glBufferData(target.getGLEnum(), data, usage.getGLEnum());
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferSubData.xhtml)
    public void bufferSubData(long offset, int[] data) {
        glBufferSubData(target.getGLEnum(), offset, data);
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferSubData.xhtml)
    public void bufferSubData(long offset, float[] data) {
        glBufferSubData(target.getGLEnum(), offset, data);
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferSubData.xhtml)
    public void bufferSubData(long offset, short[] data) {
        glBufferSubData(target.getGLEnum(), offset, data);
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferSubData.xhtml)
    public void bufferSubData(long offset, double[] data) {
        glBufferSubData(target.getGLEnum(), offset, data);
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferSubData.xhtml)
    public void bufferSubData(long offset, long[] data) {
        glBufferSubData(target.getGLEnum(), offset, data);
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferSubData.xhtml)
    public void bufferSubData(long offset, byte[] data) {
        glBufferSubData(target.getGLEnum(), offset, ByteBuffer.wrap(data));
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferSubData.xhtml)
    public void bufferSubData(long offset, IntBuffer data) {
        glBufferSubData(target.getGLEnum(), offset, data);
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferSubData.xhtml)
    public void bufferSubData(long offset, FloatBuffer data) {
        glBufferSubData(target.getGLEnum(), offset, data);
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferSubData.xhtml)
    public void bufferSubData(long offset, ShortBuffer data) {
        glBufferSubData(target.getGLEnum(), offset, data);
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferSubData.xhtml)
    public void bufferSubData(long offset, DoubleBuffer data) {
        glBufferSubData(target.getGLEnum(), offset, data);
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferSubData.xhtml)
    public void bufferSubData(long offset, LongBuffer data) {
        glBufferSubData(target.getGLEnum(), offset, data);
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferSubData.xhtml)
    public void bufferSubData(long offset, ByteBuffer data) {
        glBufferSubData(target.getGLEnum(), offset, data);
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferSubData.xhtml)
    public void bufferSubData(long offset, Vector3f data) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            glBufferSubData(target.getGLEnum(), offset, data.get(stack.mallocFloat(3)));
        }
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferSubData.xhtml)
    public void bufferSubData(long offset, Vector4f data) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            glBufferSubData(target.getGLEnum(), offset, data.get(stack.mallocFloat(4)));
        }
    }

    /// [GL Reference](https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBufferSubData.xhtml)
    public void bufferSubData(long offset, Matrix4f data) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            glBufferSubData(target.getGLEnum(), offset, data.get(stack.mallocFloat(16)));
        }
    }

    public final int getHandle() {
        return handle;
    }
}

