package net.ice.relic.core.rendering.backend.opengl.depricated.rendering;

import net.ice.curio.library.opengl.object.VertexArrayObject;
import net.ice.curio.library.opengl.object.buffer.GLBuffer;
import net.ice.curio.library.opengl.object.buffer.IndexBufferObject;
import net.ice.curio.library.opengl.object.buffer.VertexBufferObject;
import net.ice.curio.library.opengl.wrapper.enums.Usage;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class QuadMesh {

    private int vertexCount;

    private VertexArrayObject meshVAO;
    private List<GLBuffer> meshVBOs;

    public QuadMesh() {
        meshVBOs = new ArrayList<>();
        float[] positions = new float[]{
                -1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,};
        float[] textCoords = new float[]{
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,};
        int[] indices = new int[]{0, 2, 1, 1, 2, 3};
        vertexCount = indices.length;

        this.meshVAO = new VertexArrayObject();
        meshVAO.bind();

        VertexBufferObject vertexVBO = new VertexBufferObject();
        meshVBOs.add(vertexVBO);
        FloatBuffer positionsBuffer = MemoryUtil.memCallocFloat(positions.length);
        positionsBuffer.put(positions);
        positionsBuffer.flip();
        vertexVBO.bind();
        vertexVBO.bufferData(positionsBuffer, Usage.STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        VertexBufferObject textureCoordinateVBO = new VertexBufferObject();
        meshVBOs.add(textureCoordinateVBO);
        FloatBuffer textCoordsBuffer = MemoryUtil.memCallocFloat(textCoords.length);
        textCoordsBuffer.put(textCoords);
        textCoordsBuffer.flip();
        textureCoordinateVBO.bind();
        textureCoordinateVBO.bufferData(textCoordsBuffer, Usage.STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        IndexBufferObject indicesVBO = new IndexBufferObject();
        meshVBOs.add(indicesVBO);
        IntBuffer indicesBuffer = MemoryUtil.memCallocInt(indices.length);
        indicesBuffer.put(indices);
        indicesBuffer.flip();
        indicesVBO.bind();
        indicesVBO.bufferData(indicesBuffer, Usage.STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        meshVAO.unbind();

        MemoryUtil.memFree(positionsBuffer);
        MemoryUtil.memFree(textCoordsBuffer);
        MemoryUtil.memFree(indicesBuffer);
    }

    public void cleanup() {
        meshVBOs.forEach(b -> glDeleteBuffers(b.getHandle()));
        meshVAO.delete();
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public VertexArrayObject getMeshVAO() {
        return meshVAO;
    }
}
