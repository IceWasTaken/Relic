package net.ice.relic.core.rendering.backend.opengl.mesh;

import imgui.ImDrawData;
import imgui.ImGui;
import net.ice.curio.graphics.enums.BufferAccess;
import net.ice.curio.graphics.enums.BufferFlags;
import net.ice.curio.graphics.memory.Fence;
import net.ice.curio.library.opengl.object.GLFence;
import net.ice.curio.library.opengl.object.VertexArrayObject;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.curio.library.opengl.wrapper.enums.Usage;

import java.nio.ByteBuffer;
import java.util.EnumSet;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL45.*;

public class GuiMesh {

    private final VertexArrayObject vertexArrayObject;

    private final GLBuffer vertexBuffer;
    private final GLBuffer indexBuffer;

    private final Fence fence;

    public GuiMesh() {
        this.fence = new GLFence();
        this.vertexArrayObject = new VertexArrayObject();
        this.vertexBuffer = new GLBuffer(4096 * 4096, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);
        this.indexBuffer = new GLBuffer(4096 * 4096, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);

        vertexArrayObject.bind();

        vertexArrayObject.vertexBuffer(0, vertexBuffer, 0,20);

        vertexArrayObject.attributeFormat(0, 2, GL_FLOAT, false, 0);
        vertexArrayObject.attributeFormat(1, 2, GL_FLOAT, false, 8);
        vertexArrayObject.attributeFormat(2, 4, GL_UNSIGNED_BYTE, true, 16);

        vertexArrayObject.attributeBinding(0, 0);
        vertexArrayObject.attributeBinding(1, 0);
        vertexArrayObject.attributeBinding(2, 0);

        vertexArrayObject.enableAttribute(0);
        vertexArrayObject.enableAttribute(1);
        vertexArrayObject.enableAttribute(2);

        vertexArrayObject.elementBuffer(indexBuffer);

        glBindVertexArray(0);
    }

    //congratulations
    //i spent 3 hours debugging this
    //both getCmdListVtx... and getCmdListIdx... share the same bytebuffer
    //FUUUUUUUCK
    public void updateBuffers(int index) {
        ImDrawData drawData = ImGui.getDrawData();
        fence.waitSync();
        vertexBuffer.position(0);
        indexBuffer.position(0);
        vertexBuffer.put(drawData.getCmdListVtxBufferData(index));
        indexBuffer.put(drawData.getCmdListIdxBufferData(index));
    }

    public void cleanup() {
//        indexBuffer.cleanup();
//        vertexBuffer.cleanup();
        vertexArrayObject.delete();
    }

    public VertexArrayObject getVAO() {
        return vertexArrayObject;
    }

    public GLBuffer getIndexBuffer() {
        return indexBuffer;
    }

    public GLBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public Fence getFence() {
        return fence;
    }
}
