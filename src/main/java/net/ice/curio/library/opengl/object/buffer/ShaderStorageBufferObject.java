package net.ice.curio.library.opengl.object.buffer;

import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.library.opengl.wrapper.enums.BufferTarget;
import net.ice.curio.library.opengl.wrapper.enums.Usage;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.*;

import static org.lwjgl.opengl.GL15.*;

public class ShaderStorageBufferObject extends GLBuffer {

    private final Struct struct;

    public ShaderStorageBufferObject(Struct struct) {
        super(BufferTarget.SHADER_STORAGE);
        this.struct = struct;
    }

    public ShaderStorageBufferObject(Struct struct, int arraySize) {
        super(BufferTarget.SHADER_STORAGE);

        this.struct = struct;
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
        int base = struct.getStride() * arrayIndex;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            bufferSubData(struct.getOffset(offset) + base, stack.mallocInt(1).put(value).position(0));
        }
        return this;
    }

    public ShaderStorageBufferObject setFloat(int offset, int arrayIndex, float value) {
        int base = struct.getStride() * arrayIndex;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            bufferSubData(struct.getOffset(offset) + base, stack.mallocFloat(1).put(value).position(0));
        }
        return this;
    }

    public ShaderStorageBufferObject setLong(int offset, int arrayIndex, long value) {
        int base = struct.getStride() * arrayIndex;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            bufferSubData(struct.getOffset(offset) + base, stack.mallocLong(1).put(value).position(0));
        }
        return this;
    }

    public ShaderStorageBufferObject setVec2(int offset, int arrayIndex, Vector2f value) {
        int base = struct.getStride() * arrayIndex;
        bufferSubData(struct.getOffset(offset) + base, value);
        return this;
    }

    public ShaderStorageBufferObject setVec3(int offset, int arrayIndex, Vector3f value) {
        int base = struct.getStride() * arrayIndex;
        bufferSubData(struct.getOffset(offset) + base, value);
        return this;
    }

    public ShaderStorageBufferObject setVec4(int offset, int arrayIndex, Vector4f value) {
        int base = struct.getStride() * arrayIndex;
        bufferSubData(struct.getOffset(offset) + base, value);
        return this;
    }

    public ShaderStorageBufferObject setMat4x4(int offset, int arrayIndex, Matrix4f value) {
        int base = struct.getStride() * arrayIndex;
        bufferSubData(struct.getOffset(offset) + base, value);
        return this;
    }

    public void syncFromGPU() {

    }

    public Struct getStruct() {
        return struct;
    }
}


