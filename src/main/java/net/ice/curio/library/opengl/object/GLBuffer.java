package net.ice.curio.library.opengl.object;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.tinylog.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL45.*;

public final class GLBuffer {

    private final int handle;
    private final long size;
    private final int flags;
    private ByteBuffer mapping;

    private int used;

    /// [GL Wiki Reference](https://wikis.khronos.org/opengl/Buffer_Object)
    public GLBuffer(long size, int flags) {
        this.flags = flags;
        this.size = size;
        this.handle = glCreateBuffers();

        glNamedBufferStorage(handle, size, flags);

        this.mapping = map(flags);

        //not 100% sure if needed, included because it's not like i'm recreating this object each frame
        this.mapping.order(ByteOrder.nativeOrder());
    }

    public GLBuffer resize() {
        GLBuffer newBuffer = new GLBuffer(size * 2, flags);
        newBuffer.used = used;
        newBuffer.position(mapping.position());

        glCopyNamedBufferSubData(handle, newBuffer.handle, 0, 0, size);
        cleanup();

        Logger.info("[GLBuffer]: Resized buffer to {} bytes", newBuffer.size);

        return newBuffer;
    }

    public GLBuffer bind(int target) {
        glBindBuffer(target, handle);
        return this;
    }

    public GLBuffer bind(int target, int index) {
        glBindBufferBase(target, index, handle);
        return this;
    }

    public ByteBuffer map(int flags) {
        if(mapping == null) {
            this.mapping = glMapNamedBufferRange(handle, 0, size, flags);
        }
        return mapping;
    }

    public void unmap() {
        glUnmapNamedBuffer(handle);
    }

    public void cleanup() {
        if(mapping != null) {
            unmap();
        }
        glDeleteBuffers(handle);
    }

    public void put(int pos, byte value) {
        mapping.put(pos, value);
        used += 1;
    }

    public void put(byte value) {
        mapping.put(value);
        used += 1;
    }

    public void put(byte[] value) {
        mapping.put(value);
        used += value.length;
    }

    public void put(int pos, byte[] value) {
        mapping.put(pos, value);
        used += value.length;
    }

    public void put(ByteBuffer value) {
        mapping.put(value);
        used += value.limit();
    }

    public void putShort(int pos, short value) {
        mapping.putShort(pos, value);
        used += 2;
    }

    public void putInt(int pos, int value) {
        mapping.putInt(pos, value);
        used += 4;
    }

    public void putLong(int pos, long value) {
        mapping.putLong(pos, value);
        used += 8;
    }

    public void putFloat(int pos, float value) {
        mapping.putFloat(pos, value);
        used += 4;
    }

    public void putDouble(int pos, double value) {
        mapping.putDouble(pos, value);
        used += 8;
    }

    public void putVec3f(int pos, Vector3f value) {
        putFloat(pos, value.x);
        putFloat(pos + 4, value.y);
        putFloat(pos + 8, value.z);
    }
    public void putVec4f(int pos, Vector4f value) {
        putFloat(pos, value.x);
        putFloat(pos + 4, value.y);
        putFloat(pos + 8, value.z);
        putFloat(pos + 12, value.w);
    }

    public void putShort(short value) {
        mapping.putShort(value);
        used += 2;
    }

    public void putInt(int value) {
        mapping.putInt(value);
        used += 4;
    }

    public void putLong(long value) {
        mapping.putLong(value);
        used += 8;
    }

    public void putFloat(float value) {
        mapping.putFloat(value);
        used += 4;
    }

    public void putDouble(double value) {
        mapping.putDouble(value);
        used += 8;
    }

    public void putShort(int pos, short[] value) {
        mapping.asShortBuffer().put(pos / 2, value);
        used += 2 * value.length;
    }
    public void putInt(int pos, int[] value) {
        mapping.asIntBuffer().put(pos / 4, value);
        used += 4 * value.length;
    }
    public void putLong(int pos, long[] value) {
        mapping.asLongBuffer().put(pos / 8, value);
        used += 8 * value.length;
    }
    public void putFloat(int pos, float[] value) {
        mapping.asFloatBuffer().put(pos / 4, value);
        used += 4 * value.length;
    }
    public void putDouble(int pos, double[] value) {
        mapping.asDoubleBuffer().put(pos / 8, value);
        used += 8 * value.length;
    }

    public void putShort(short[] value) {
        mapping.asShortBuffer().put(value);
        mapping.position(mapping.position() + value.length * 2);
        used += 2 * value.length;
    }

    public void putInt(int[] value) {
        mapping.asIntBuffer().put(value);
        mapping.position(mapping.position() + value.length * 4);
        used += 4 * value.length;
    }

    public void putLong(long[] value) {
        mapping.asLongBuffer().put(value);
        mapping.position(mapping.position() + value.length * 8);
        used += 8 * value.length;
    }

    public void putFloat(float[] value) {
        mapping.asFloatBuffer().put(value);
        mapping.position(mapping.position() + value.length * 4);
        used += 4 * value.length;
    }

    public void putDouble(double[] value) {
        mapping.asDoubleBuffer().put(value);
        mapping.position(mapping.position() + value.length * 8);
        used += 8 * value.length;
    }

    public void putMatrix4f(int pos, Matrix4f matrix) {
        float[] arr = new float[16];
        matrix.get(arr);
        putFloat(pos, arr);
    }

    public void putMatrix4f(Matrix4f matrix) {
        float[] arr = new float[16];
        matrix.get(arr);
        putFloat(arr);
    }

    public void position(int position) {
        mapping.position(position);
    }

    public int position() {
        return mapping.position();
    }

    public void flip() {
        mapping.flip();
    }

    public int remaining() {
        return mapping.limit() - mapping.position();
    }

    public long getSize() {
        return size;
    }

    public int getUsed() {
        return used;
    }

    int getHandle() {
        return handle;
    }
}

