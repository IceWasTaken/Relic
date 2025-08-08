package net.ice.relic.engine.opengl;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL45.glCreateBuffers;

public class ShaderStorageBufferObject {

    private final int handle;

    public ShaderStorageBufferObject() {
        this.handle = glGenBuffers();
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

     public void bufferDataLong(LongBuffer data, int glEnum) {
        glBufferData(GL_SHADER_STORAGE_BUFFER, data, glEnum);
     }

     public void bufferDataInt(IntBuffer data, int glEnum) {
        glBufferData(GL_SHADER_STORAGE_BUFFER, data, glEnum);
     }

     public void bufferDataByteBuffer(ByteBuffer data, int glEnum) {
        glBufferData(GL_SHADER_STORAGE_BUFFER, data, glEnum);
     }

     //rewrite
     public void bufferDataVec4f(Vector4f data, int glEnum) {
        float[] dumb = new float[4];
        dumb[0] = data.x;
        dumb[1] = data.y;
        dumb[2] = data.z;
        dumb[3] = data.w;
        glBufferData(GL_SHADER_STORAGE_BUFFER, dumb, glEnum);
     }

     public void bufferDataVec3f(Vector3f data, int glEnum) {
        glBufferData(GL_SHADER_STORAGE_BUFFER, new float[]{data.x, data.y, data.z}, glEnum);
     }

     public void bufferMatrix4f(Matrix4f matrix4f, int glEnum) {
        glBufferData(GL_SHADER_STORAGE_BUFFER, matrix4fToFloatArray(matrix4f), glEnum);
     }

     private float[] matrix4fToFloatArray(Matrix4f matrix4f) {
        float[] data = new float[16];
        data[0] = matrix4f.m00();
        data[1] = matrix4f.m01();
        data[2] = matrix4f.m02();
        data[3] = matrix4f.m03();

        data[4] = matrix4f.m10();
        data[5] = matrix4f.m11();
        data[6] = matrix4f.m12();
        data[7] = matrix4f.m13();

        data[8] = matrix4f.m20();
        data[9] = matrix4f.m21();
        data[10] = matrix4f.m22();
        data[11] = matrix4f.m23();

        data[12] = matrix4f.m30();
        data[13] = matrix4f.m31();
        data[14] = matrix4f.m32();
        data[15] = matrix4f.m33();
        return data;
     }
}
