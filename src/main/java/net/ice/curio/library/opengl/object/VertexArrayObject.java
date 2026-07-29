package net.ice.curio.library.opengl.object;

import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;

public class VertexArrayObject {

    private final int handle;

    public VertexArrayObject() {
        this.handle = glCreateVertexArrays();
    }

    public void bind() {
        glBindVertexArray(handle);
    }

    public int getHandle() {
        return handle;
    }

    public void delete() {
        glDeleteVertexArrays(handle);
    }
}