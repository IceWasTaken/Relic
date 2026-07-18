package net.ice.relic.core.rendering.backend.opengl.depricated.model;

import net.ice.curio.library.opengl.object.VertexArrayObject;
import net.ice.curio.library.opengl.object.buffer.GLBuffer;
import net.ice.curio.library.opengl.object.buffer.IndexBufferObject;
import net.ice.curio.library.opengl.object.buffer.VertexBufferObject;
import net.ice.curio.library.opengl.wrapper.enums.Usage;
import net.ice.relic.core.model.mesh.MeshData;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    public static final int MAX_WEIGHTS = 4;

    private int vertexCount;

    private VertexArrayObject vertexArrayObject;
    private List<GLBuffer> VBOs;

    public Mesh(MeshData data) {
        this.vertexCount = data.getIndices().length;
        this.VBOs = new ArrayList<>();

        this.vertexArrayObject = new VertexArrayObject();
        vertexArrayObject.bind();

        // Positions VBO
        GLBuffer VBO = new VertexBufferObject();
        VBOs.add(VBO);
        FloatBuffer positionsBuffer = MemoryUtil.memCallocFloat(data.getVertices().length);
        positionsBuffer.put(0, data.getVertices());
        VBO.bind();
        glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        // Normals VBO
        VBO = new VertexBufferObject();
        VBOs.add(VBO);
        FloatBuffer normalsBuffer = MemoryUtil.memCallocFloat(data.getNormals().length);
        normalsBuffer.put(0, data.getNormals());
        VBO.bind();
        glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

        // Tangents VBO
        VBO = new VertexBufferObject();
        VBOs.add(VBO);
        FloatBuffer tangentsBuffer = MemoryUtil.memCallocFloat(data.getTangents().length);
        tangentsBuffer.put(0, data.getTangents());
        VBO.bind();
        glBufferData(GL_ARRAY_BUFFER, tangentsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

        // Bitangents VBO
        VBO = new VertexBufferObject();
        VBOs.add(VBO);
        FloatBuffer bitangentsBuffer = MemoryUtil.memCallocFloat(data.getBitangents().length);
        bitangentsBuffer.put(0, data.getBitangents());
        VBO.bind();
        glBufferData(GL_ARRAY_BUFFER, bitangentsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);

        // Texture coordinates VBO
        VBO = new VertexBufferObject();
        VBOs.add(VBO);
        FloatBuffer textCoordsBuffer = MemoryUtil.memCallocFloat(data.getTextureCoords().length);
        textCoordsBuffer.put(0, data.getTextureCoords());
        VBO.bind();
        VBO.bufferData(textCoordsBuffer, Usage.STATIC_DRAW);
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(4, 2, GL_FLOAT, false, 0, 0);

        // Bone weights
        VBO = new VertexBufferObject();
        VBOs.add(VBO);
        FloatBuffer weightsBuffer = MemoryUtil.memCallocFloat(data.getWeights().length);
        weightsBuffer.put(data.getWeights()).flip();
        VBO.bind();
        glBufferData(GL_ARRAY_BUFFER, weightsBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(5, 4, GL_FLOAT, false, 0, 0);

        // Bone indices
        VBO = new VertexBufferObject();
        VBOs.add(VBO);
        IntBuffer boneIndicesBuffer = MemoryUtil.memCallocInt(data.getBoneIndices().length);
        boneIndicesBuffer.put(data.getBoneIndices()).flip();
        VBO.bind();
        glBufferData(GL_ARRAY_BUFFER, boneIndicesBuffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(6);
        glVertexAttribPointer(6, 4, GL_FLOAT, false, 0, 0);

        // Index VBO
        VBO = new IndexBufferObject();
        VBOs.add(VBO);
        IntBuffer indicesBuffer = MemoryUtil.memCallocInt(data.getIndices().length);
        indicesBuffer.put(data.getIndices());
        VBO.bind();
        VBO.bufferData(indicesBuffer, Usage.STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        MemoryUtil.memFree(positionsBuffer);
        MemoryUtil.memFree(normalsBuffer);
        MemoryUtil.memFree(tangentsBuffer);
        MemoryUtil.memFree(bitangentsBuffer);
        MemoryUtil.memFree(textCoordsBuffer);
        MemoryUtil.memFree(weightsBuffer);
        MemoryUtil.memFree(boneIndicesBuffer);
        MemoryUtil.memFree(indicesBuffer);
    }

    public void cleanup() {
        vertexArrayObject.delete();
    }

    public VertexArrayObject getVertexArrayObject() {
        return vertexArrayObject;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}