package net.ice.relic.core.rendering.backend.opengl.mesh;

import imgui.ImDrawData;
import imgui.ImGui;
import net.ice.curio.library.opengl.object.VertexArrayObject;
import net.ice.curio.library.opengl.object.buffer.GLBuffer;
import net.ice.curio.library.opengl.wrapper.enums.Usage;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL45.*;

public class GuiMesh {

    private final VertexArrayObject vao;
    private final GLBuffer vertexBuffer;
    private final GLBuffer indexBuffer;

    public GuiMesh() {
        this.vao = new VertexArrayObject();
        this.vertexBuffer = new GLBuffer();
        this.indexBuffer = new GLBuffer();

        vao.bind();

        glVertexArrayVertexBuffer(vao.getHandle(), 0, vertexBuffer.getHandle(), 0L, 20);

        glVertexArrayAttribFormat(vao.getHandle(), 0, 2, GL_FLOAT, false, 0); //inPos
        glVertexArrayAttribFormat(vao.getHandle(), 1, 2, GL_FLOAT, false, 8); //inTextCoords
        glVertexArrayAttribFormat(vao.getHandle(), 2, 4, GL_UNSIGNED_BYTE, true, 16); //inColor

        glVertexArrayAttribBinding(vao.getHandle(), 0, 0);
        glVertexArrayAttribBinding(vao.getHandle(), 1, 0);
        glVertexArrayAttribBinding(vao.getHandle(), 2, 0);

        glEnableVertexArrayAttrib(vao.getHandle(), 0);
        glEnableVertexArrayAttrib(vao.getHandle(), 1);
        glEnableVertexArrayAttrib(vao.getHandle(), 2);

        glVertexArrayElementBuffer(vao.getHandle(), indexBuffer.getHandle());

        glBindVertexArray(0);
    }

    //congratulations
    //i spent 3 hours debugging this
    //both getCmdListVtx... and getCmdListIdx... share the same goddamned bytebuffer
    public void updateBuffers(int index) {
        ImDrawData drawData = ImGui.getDrawData();
        vertexBuffer.bufferData(drawData.getCmdListVtxBufferData(index), Usage.STREAM_DRAW);
        indexBuffer.bufferData(drawData.getCmdListIdxBufferData(index), Usage.STREAM_DRAW);
    }

    public void cleanup() {
        indexBuffer.cleanup();
        vertexBuffer.cleanup();
        vao.delete();
    }

    public VertexArrayObject getVAO() {
        return vao;
    }

    public GLBuffer getIndexBuffer() {
        return indexBuffer;
    }

    public GLBuffer getVertexBuffer() {
        return vertexBuffer;
    }
}
