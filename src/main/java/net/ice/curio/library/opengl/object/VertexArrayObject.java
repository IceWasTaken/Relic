package net.ice.curio.library.opengl.object;

import net.ice.curio.library.opengl.wrapper.enums.Format;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;

public class VertexArrayObject {

    private final int handle;

    public VertexArrayObject() {
        this.handle = glCreateVertexArrays();
    }

    public void bind() {
        glBindVertexArray(handle);
    }

    @Deprecated
    public void enableVertexAttributeArray(int index) {
        glEnableVertexAttribArray(index);
    }

    @Deprecated
    public void disableVertexAttribArray(int index) {
        glDisableVertexAttribArray(index);
    }

    @Deprecated
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