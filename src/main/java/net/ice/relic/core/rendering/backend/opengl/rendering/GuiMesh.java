package net.ice.relic.core.rendering.backend.opengl.rendering;

import imgui.ImDrawData;
import net.ice.relic.core.rendering.backend.opengl.VertexArrayObject;
import net.ice.relic.core.rendering.backend.opengl.VertexBufferObject;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class GuiMesh {

    private VertexBufferObject indicesVBO;
    private VertexBufferObject verticesVBO;

    private VertexArrayObject VAO;

    public GuiMesh() {
        VAO = new VertexArrayObject();
        VAO.bind();

        verticesVBO = new VertexBufferObject();
        verticesVBO.bind(GL_ARRAY_BUFFER);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, ImDrawData.sizeOfImDrawVert(), 0);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, ImDrawData.sizeOfImDrawVert(), 8);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 4, GL_UNSIGNED_BYTE, true, ImDrawData.sizeOfImDrawVert(), 16);

        indicesVBO = new VertexBufferObject();

        VertexBufferObject.unbind(GL_ARRAY_BUFFER);
        glBindVertexArray(0);
    }

    public void cleanup() {
        indicesVBO.delete();
        verticesVBO.delete();
        VAO.delete();
    }

    public VertexArrayObject getVAO() {
        return VAO;
    }

    public VertexBufferObject getIndicesVBO() {
        return indicesVBO;
    }

    public VertexBufferObject getVerticesVBO() {
        return verticesVBO;
    }
}
