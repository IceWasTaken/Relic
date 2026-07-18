package net.ice.curio.library.opengl.object;

import net.ice.curio.library.opengl.wrapper.enums.Format;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class VertexArrayObject {

    private final int handle;

    public VertexArrayObject() {
        this.handle = glGenVertexArrays();
    }

    public void bind() {
        glBindVertexArray(handle);
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

    public void vertexAttribPointer(int index, int size, Format format, boolean normalized, int stride, long pointer) {
        glVertexAttribPointer(index, size, format.getGLEnum(), normalized, stride, pointer);
    }

    public int getHandle() {
        return handle;
    }

    public void delete() {
        glDeleteVertexArrays(handle);
    }
}