package net.ice.relic.core.rendering.backend.opengl;

import net.ice.relic.core.rendering.buffer.VertexBuffer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;

public class VertexBufferObject implements VertexBuffer {

    private final int id;

    public VertexBufferObject() {
        this.id = glGenBuffers();
    }

    @Override
    public void bind(int type) {
        glBindBuffer(type, id);
    }

    public static void unbind(int type) {
        glBindBuffer(type, 0);
    }

    public void bufferDataFloat(int target, FloatBuffer data, int usage) {
        glBufferData(target, data, usage);
    }

    public void bufferDataInt(int target, IntBuffer data, int usage) {
        glBufferData(target, data, usage);
    }

    public void bufferData(int target, ByteBuffer data, int usage) {
        glBufferData(target, data, usage);
    }

    public void bufferSubData(int type, long offset, FloatBuffer data) {
        glBufferSubData(type, offset, data);
    }

    public int getId() {
        return id;
    }



    @Override
    public void unbind() {

    }

    @Override
    public void bufferData() {

    }

    public void delete() {
        glDeleteBuffers(id);
    }
}