package net.ice.relic.engine.opengl;

import static org.lwjgl.opengl.GL30.*;

public class VertexArrayObject {

    private final int id;

    public VertexArrayObject() {
        this.id = glGenVertexArrays();
    }

    public void bind() {
        glBindVertexArray(id);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void enableVertexAttributeArray(int index) {
        glEnableVertexAttribArray(index);
    }

    public void disableVertexAttribArray(int index) {
        glDisableVertexAttribArray(index);
    }

    public void vertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer) {
        glVertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    public int getId() {
        return id;
    }

    public void delete() {
        glDeleteVertexArrays(id);
    }
}