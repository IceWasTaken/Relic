package net.ice.curio.library.opengl.object.buffer;

import net.ice.curio.library.opengl.wrapper.enums.BufferTarget;
import net.ice.curio.library.opengl.wrapper.enums.Usage;
import net.ice.curio.library.opengl.wrapper.glsl.GLSLStruct;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.*;

import static org.lwjgl.opengl.GL15.*;

public class ShaderStorageBufferObject extends GLBuffer {

    private final ByteBuffer data;
    private final GLSLStruct format;

    public ShaderStorageBufferObject(GLSLStruct format) {
        super(BufferTarget.SHADER_STORAGE);

        this.format = format;
        this.data = ByteBuffer.allocateDirect(format.getSize()).order(ByteOrder.nativeOrder());
    }

    public ShaderStorageBufferObject(GLSLStruct format, int arraySize) {
        super(BufferTarget.SHADER_STORAGE);

        this.format = format;
        this.data = ByteBuffer.allocateDirect(arraySize * format.getStride()).order(ByteOrder.nativeOrder());
    }

    public void bufferSubData(int target, long offset, int data) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            glBufferSubData(target, offset, stack.mallocInt(1).put(data).flip());
        }
    }

    public void bufferSubData(int target, long offset, float data) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            glBufferSubData(target, offset, stack.mallocFloat(1).put(data).flip());
        }
    }


    public ShaderStorageBufferObject setInt(int offset, int arrayIndex, int value) {
        int base = format.getStride() * arrayIndex;
        data.putInt(format.getOffset(offset) + base, value);
        return this;
    }

    public ShaderStorageBufferObject setFloat(int offset, int arrayIndex, float value) {
        int base = format.getStride() * arrayIndex;
        data.putFloat(format.getOffset(offset) + base, value);
        return this;
    }

    public ShaderStorageBufferObject setLong(int offset, int arrayIndex, long value) {
        int base = format.getStride() * arrayIndex;
        data.putLong(base + format.getOffset(offset), value);
        return this;
    }

    public ShaderStorageBufferObject setVec2(int offset, int arrayIndex, Vector2f value) {
        int base = format.getStride() * arrayIndex;
        data.putFloat(format.getOffset(offset) + base, value.x);
        data.putFloat(format.getOffset(offset) + base + 4, value.y);
        return this;
    }

    public ShaderStorageBufferObject setVec3(int offset, int arrayIndex, Vector3f value) {
        int base = format.getStride() * arrayIndex;
        data.putFloat(format.getOffset(offset) + base, value.x);
        data.putFloat(format.getOffset(offset) + 4 + base, value.y);
        data.putFloat(format.getOffset(offset) + 8 + base, value.z);
        return this;
    }

    public ShaderStorageBufferObject setVec4(int offset, int arrayIndex, Vector4f value) {
        int base = format.getStride() * arrayIndex;
        data.putFloat(format.getOffset(offset) + base, value.x);
        data.putFloat(format.getOffset(offset) + 4 + base, value.y);
        data.putFloat(format.getOffset(offset) + 8 + base, value.z);
        data.putFloat(format.getOffset(offset) + 12 + base, value.w);
        return this;
    }

    public ShaderStorageBufferObject setMat4x4(int offset, int arrayIndex, Matrix4f value) {
        int base = format.getStride() * arrayIndex;
        data.putFloat(format.getOffset(offset) + base, value.m00());
        data.putFloat(format.getOffset(offset) + (4) + base, value.m10());
        data.putFloat(format.getOffset(offset) + (2 * 4) + base, value.m20());
        data.putFloat(format.getOffset(offset) + (3 * 4) + base, value.m30());

        data.putFloat(format.getOffset(offset) + (4 * 4) + base, value.m01());
        data.putFloat(format.getOffset(offset) + (5 * 4) + base, value.m11());
        data.putFloat(format.getOffset(offset) + (6 * 4) + base, value.m22());
        data.putFloat(format.getOffset(offset) + (7 * 4) + base, value.m31());

        data.putFloat(format.getOffset(offset) + (8 * 4) + base, value.m02());
        data.putFloat(format.getOffset(offset) + (9 * 4) + base, value.m12());
        data.putFloat(format.getOffset(offset) + (10 * 4) + base, value.m22());
        data.putFloat(format.getOffset(offset) + (11 * 4) + base, value.m32());

        data.putFloat(format.getOffset(offset) + (12 * 4) + base, value.m03());
        data.putFloat(format.getOffset(offset) + (13 * 4) + base, value.m13());
        data.putFloat(format.getOffset(offset) + (14 * 4) + base, value.m23());
        data.putFloat(format.getOffset(offset) + (15 * 4) + base, value.m33());
        return this;
    }

    public void syncToGPU(Usage usage) {
        bufferData(data, usage);
    }

    public void syncFromGPU() {

    }

    public ByteBuffer getData() {
        return data;
    }

    public GLSLStruct getFormat() {
        return format;
    }
}


