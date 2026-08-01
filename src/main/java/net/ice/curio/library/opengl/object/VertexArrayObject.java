package net.ice.curio.library.opengl.object;

import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL45.*;

public class VertexArrayObject {

    private final int handle;

    public VertexArrayObject() {
        this.handle = glCreateVertexArrays();
    }

    public void bind() {
        glBindVertexArray(handle);
    }

    public void elementBuffer(GLBuffer indexBuffer) {
        glVertexArrayElementBuffer(handle, indexBuffer.getHandle());
    }

    public void vertexBuffer(int bindingIndex, GLBuffer glBuffer, int offset, int stride) {
        glVertexArrayVertexBuffer(handle, bindingIndex, glBuffer.getHandle(), offset, stride);
    }

    public void attributeFormat(int index, int size, int type, boolean normalized, int relativeOffset) {
        glVertexArrayAttribFormat(handle, index, size, type, normalized, relativeOffset);
    }

    public void attributeBinding(int attributeIndex, int bindingIndex) {
        glVertexArrayAttribBinding(handle, attributeIndex, bindingIndex);
    }

    public void enableAttribute(int index) {
        glEnableVertexArrayAttrib(handle, index);
    }

    public int getHandle() {
        return handle;
    }

    public void delete() {
        glDeleteVertexArrays(handle);
    }
}