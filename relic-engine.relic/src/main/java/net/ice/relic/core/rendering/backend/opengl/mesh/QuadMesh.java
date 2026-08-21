package net.ice.relic.core.rendering.backend.opengl.mesh;

import net.ice.curio.library.opengl.object.VertexArrayObject;
import net.ice.curio.library.opengl.object.GLBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL45.*;

public class QuadMesh {

    private int vertexCount;

    private VertexArrayObject vertexArrayObject;
    private List<GLBuffer> meshVBOs;

    private final GLBuffer vertexBuffer;
    private final GLBuffer textureCoordinateBuffer;
    private final GLBuffer indexBuffer;

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
        this.vertexArrayObject = new VertexArrayObject();
        this.vertexBuffer = new GLBuffer(positions.length * 4L, GL_MAP_WRITE_BIT);
        this.textureCoordinateBuffer = new GLBuffer(textCoords.length * 4L, GL_MAP_WRITE_BIT);
        this.indexBuffer = new GLBuffer(indices.length * 4L, GL_MAP_WRITE_BIT);

        vertexCount = indices.length;

        vertexArrayObject.bind();

        vertexBuffer.putFloat(positions);
        textureCoordinateBuffer.putFloat(textCoords);
        indexBuffer.putInt(indices);


        vertexArrayObject.vertexBuffer(0, vertexBuffer, 0, 12);
        vertexArrayObject.vertexBuffer(1, textureCoordinateBuffer, 0, 8);

        vertexArrayObject.attributeFormat(0, 3, GL_FLOAT, false, 0);
        vertexArrayObject.attributeFormat(1, 2, GL_FLOAT, false, 0);

        vertexArrayObject.attributeBinding(0, 0);
        vertexArrayObject.attributeBinding(1, 1);

        vertexArrayObject.enableAttribute(0);
        vertexArrayObject.enableAttribute(1);

        vertexArrayObject.elementBuffer(indexBuffer);

        vertexBuffer.unmap();
        textureCoordinateBuffer.unmap();
        indexBuffer.unmap();

        glBindVertexArray(0);
    }

    public void cleanup() {
        vertexArrayObject.delete();
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public VertexArrayObject getMeshVAO() {
        return vertexArrayObject;
    }
}
