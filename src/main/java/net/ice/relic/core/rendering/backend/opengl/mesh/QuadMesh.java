package net.ice.relic.core.rendering.backend.opengl.mesh;

import net.ice.curio.library.opengl.object.VertexArrayObject;
import net.ice.curio.library.opengl.object.buffer.GLBuffer;
import net.ice.curio.library.opengl.wrapper.enums.Usage;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.opengl.GL45.glVertexArrayAttribBinding;

public class QuadMesh {

    private int vertexCount;

    private VertexArrayObject meshVAO;
    private List<GLBuffer> meshVBOs;

    private final float[] positions = new float[]{
            -1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
    };
    private final float[] textCoords = new float[]{
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };

    private final int[] indices = new int[]{
            0, 2, 1,
            1, 2, 3
    };

    public QuadMesh() {
        meshVBOs = new ArrayList<>();

        vertexCount = indices.length;

        this.meshVAO = new VertexArrayObject();
        meshVAO.bind();

        GLBuffer vertexVBO = new GLBuffer();
        meshVBOs.add(vertexVBO);
        FloatBuffer positionsBuffer = MemoryUtil.memCallocFloat(positions.length).put(positions).flip();
        vertexVBO.bufferData(positionsBuffer, Usage.STATIC_DRAW);

        GLBuffer textureCoordinateVBO = new GLBuffer();
        meshVBOs.add(textureCoordinateVBO);
        FloatBuffer textCoordsBuffer = MemoryUtil.memCallocFloat(textCoords.length).put(textCoords).flip();
        textureCoordinateVBO.bufferData(textCoordsBuffer, Usage.STATIC_DRAW);

        glVertexArrayVertexBuffer(meshVAO.getHandle(), 0, vertexVBO.getHandle(), 0, 12);
        glVertexArrayVertexBuffer(meshVAO.getHandle(), 1, textureCoordinateVBO.getHandle(), 0, 8);

        glVertexArrayAttribFormat(meshVAO.getHandle(), 0, 3, GL_FLOAT, false, 0);
        glVertexArrayAttribFormat(meshVAO.getHandle(), 1, 2, GL_FLOAT, false, 0);

        glVertexArrayAttribBinding(meshVAO.getHandle(), 0, 0);
        glVertexArrayAttribBinding(meshVAO.getHandle(), 1, 1);

        glEnableVertexArrayAttrib(meshVAO.getHandle(), 0);
        glEnableVertexArrayAttrib(meshVAO.getHandle(), 1);

        GLBuffer indexBuffer = new GLBuffer();
        meshVBOs.add(indexBuffer);
        IntBuffer indicesBuffer = MemoryUtil.memCallocInt(indices.length).put(indices).flip();
        indexBuffer.bufferData(indicesBuffer, Usage.STATIC_DRAW);

        glVertexArrayElementBuffer(meshVAO.getHandle(), indexBuffer.getHandle());

        glBindVertexArray(0);

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
