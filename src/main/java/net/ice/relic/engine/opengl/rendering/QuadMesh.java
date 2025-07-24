package net.ice.relic.engine.opengl.rendering;

import net.ice.relic.engine.opengl.VertexArrayObject;
import net.ice.relic.engine.opengl.VertexBufferObject;
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
    private List<VertexBufferObject> meshVBOs;

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
        positionsBuffer.put(0, positions);
        vertexVBO.bind(GL_ARRAY_BUFFER);
        vertexVBO.bufferDataFloat(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        VertexBufferObject textureCoordinateVBO = new VertexBufferObject();
        meshVBOs.add(textureCoordinateVBO);
        FloatBuffer textCoordsBuffer = MemoryUtil.memCallocFloat(textCoords.length);
        textCoordsBuffer.put(0, textCoords);
        textureCoordinateVBO.bind(GL_ARRAY_BUFFER);
        textureCoordinateVBO.bufferDataFloat(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        VertexBufferObject indicesVBO = new VertexBufferObject();
        meshVBOs.add(indicesVBO);
        IntBuffer indicesBuffer = MemoryUtil.memCallocInt(indices.length);
        indicesBuffer.put(0, indices);
        indicesVBO.bind(GL_ELEMENT_ARRAY_BUFFER);
        indicesVBO.bufferDataInt(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        indicesVBO.unbind(GL_ARRAY_BUFFER);
        meshVAO.unbind();

        MemoryUtil.memFree(positionsBuffer);
        MemoryUtil.memFree(textCoordsBuffer);
        MemoryUtil.memFree(indicesBuffer);
    }

    public void cleanup() {
        meshVBOs.forEach(VertexBufferObject::delete);
        meshVAO.delete();
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public VertexArrayObject getMeshVAO() {
        return meshVAO;
    }
}
