package net.ice.relic.core.rendering.backend.opengl;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

public class ShaderStorageBufferObject {

    private final int handle;
    private final ByteBuffer data;

    public ShaderStorageBufferObject(Builder builder) {
        this.handle = glGenBuffers();
        this.data = builder.data;
    }

     public void bind() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, handle);
     }

     public void unbind() {
         glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
     }

     public void bindBase(int index) {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, index, handle);
     }

     public void delete() {
        glDeleteBuffers(handle);
     }

     public void bufferData(int glEnum) {
        glBufferData(GL_SHADER_STORAGE_BUFFER, data, glEnum);
     }

     public static class Builder {

        private static final int INITIAL_CAPACITY = 4096;

        private ByteBuffer data;
        private int size;
        private int bindingIndex;

        public Builder() {
            this.size = 0;
            this.data = ByteBuffer.allocateDirect(INITIAL_CAPACITY).order(ByteOrder.nativeOrder());
        }

        public Builder addFloat(float value) {
            size += Float.BYTES;
            data.putFloat(value);
            return this;
        }

        public Builder addInt(int value) {
            size += Integer.BYTES;
            data.putInt(value);
            return this;
        }

        public Builder addLong(long value) {
            size += Long.BYTES;
            data.putLong(value);
            return this;
        }

        public Builder addVec3f(Vector3f value) {
            size += 3 * Float.BYTES;
            data.putFloat(value.x);
            data.putFloat(value.y);
            data.putFloat(value.z);
            return this;
        }

        public Builder addVec4f(Vector4f value) {
            size += 4 * Float.BYTES;
            data.putFloat(value.x);
            data.putFloat(value.y);
            data.putFloat(value.z);
            data.putFloat(value.w);
            return this;
        }

        public Builder addPadding(int bytes) {
            for (int i = 0; i < bytes; i++) {
                data.put((byte) 0);
            }
            size += bytes;
            return this;
        }

        public ShaderStorageBufferObject build() {
            data.rewind();
            return new ShaderStorageBufferObject(this);
        }

        public ShaderStorageBufferObject buildAndBufferData(int glEnum, int bindingIndex) {
            ShaderStorageBufferObject shaderStorageBufferObject = new ShaderStorageBufferObject(this);
            shaderStorageBufferObject.bind();
            shaderStorageBufferObject.bufferData(glEnum);
            shaderStorageBufferObject.bindBase(bindingIndex);
            return shaderStorageBufferObject;
        }
     }
}


