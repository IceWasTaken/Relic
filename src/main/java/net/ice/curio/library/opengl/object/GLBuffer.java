package net.ice.curio.library.opengl.object;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL45.*;

public final class GLBuffer {

    private final int handle;
    private final long size;
    private ByteBuffer mapping;

    /// [GL Wiki Reference](https://wikis.khronos.org/opengl/Buffer_Object)
    public GLBuffer(long size, int flags) {
        this.size = size;
        this.handle = glCreateBuffers();

        glNamedBufferStorage(handle, size, flags);

        this.mapping = map(flags);

        //not 100% sure if needed, included because it's not like i'm recreating this object each frame
        this.mapping.order(ByteOrder.nativeOrder());
    }

    public void bind(int target) {
        glBindBuffer(target, handle);
    }

    public void bindBase(int target, int index) {
        glBindBufferBase(target, index, handle);
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

    public void destroy() {
        glDeleteBuffers(handle);
    }

    public void put(int pos, byte value) {
        mapping.put(pos, value);
    }
    public void put(byte value) {
        mapping.put(value);
    }

    public void put(byte[] value) {
        mapping.put(value);
    }
    public void put(int pos, byte[] value) {
        mapping.put(pos, value);
    }

    public void put(ByteBuffer value) {
        mapping.put(value);
    }

    public void putShort(int pos, short value) {
        mapping.putShort(pos, value);
    }
    public void putInt(int pos, int value) {
        mapping.putInt(pos, value);
    }
    public void putLong(int pos, long value) {
        mapping.putLong(pos, value);
    }
    public void putFloat(int pos, float value) {
        mapping.putFloat(pos, value);
    }
    public void putDouble(int pos, double value) {
        mapping.putDouble(pos, value);
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
    }
    public void putInt(int value) {
        mapping.putInt(value);
    }
    public void putLong(long value) {
        mapping.putLong(value);
    }
    public void putFloat(float value) {
        mapping.putFloat(value);
    }
    public void putDouble(double value) {
        mapping.putDouble(value);
    }

    public void putShort(int pos, short[] value) {
        mapping.asShortBuffer().put(pos, value);
    }
    public void putInt(int pos, int[] value) {
        mapping.asIntBuffer().put(pos, value);
    }
    public void putLong(int pos, long[] value) {
        mapping.asLongBuffer().put(pos, value);
    }
    public void putFloat(int pos, float[] value) {
        mapping.asFloatBuffer().put(pos / 4, value);
    }
    public void putDouble(int pos, double[] value) {
        mapping.asDoubleBuffer().put(pos, value);
    }

    public void putShort(short[] value) {
        mapping.asShortBuffer().put(value);
    }
    public void putInt(int[] value) {
        mapping.asIntBuffer().put(value);
        mapping.position(mapping.position() + value.length * 4);
    }
    public void putLong(long[] value) {
        mapping.asLongBuffer().put(value);
    }
    public void putFloat(float[] value) {
        mapping.asFloatBuffer().put(value);
        mapping.position(mapping.position() + value.length * 4);
    }
    public void putDouble(double[] value) {
        mapping.asDoubleBuffer().put(value);
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

    public void resetPos() {
        mapping.position(0);
    }

    public void flip() {
        mapping.flip();
    }

    public int remaining() {
        return mapping.limit() - mapping.position();
    }


    int getHandle() {
        return handle;
    }
}

