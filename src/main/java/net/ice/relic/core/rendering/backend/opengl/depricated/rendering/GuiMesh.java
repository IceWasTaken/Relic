package net.ice.relic.core.rendering.backend.opengl.depricated.rendering;

import imgui.ImDrawData;
import net.ice.curio.library.opengl.object.VertexArrayObject;
import net.ice.curio.library.opengl.object.buffer.IndexBufferObject;
import net.ice.curio.library.opengl.object.buffer.VertexBufferObject;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GuiMesh {

    private IndexBufferObject indicesVBO;
    private VertexBufferObject verticesVBO;

    private VertexArrayObject VAO;

    public GuiMesh() {
        VAO = new VertexArrayObject();
        VAO.bind();

        verticesVBO = new VertexBufferObject();
        verticesVBO.bind();
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, ImDrawData.sizeOfImDrawVert(), 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, ImDrawData.sizeOfImDrawVert(), 8);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 4, GL_UNSIGNED_BYTE, true, ImDrawData.sizeOfImDrawVert(), 16);

        indicesVBO = new IndexBufferObject();

        verticesVBO.unbind();
        glBindVertexArray(0);
    }

    public void cleanup() {
        indicesVBO.cleanup();
        verticesVBO.cleanup();
        VAO.delete();
    }

    public VertexArrayObject getVAO() {
        return VAO;
    }

    public IndexBufferObject getIndicesVBO() {
        return indicesVBO;
    }

    public VertexBufferObject getVerticesVBO() {
        return verticesVBO;
    }
}
