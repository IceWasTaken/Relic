package net.ice.relic.engine.opengl.model;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Mesh {
    private final int vaoId;
    private final List<Integer> vboIds;
    private final int indexCount;
    private final Material material;

    public Mesh(int vaoId, List<Integer> vboIds, int indexCount, Material material) {
        this.vaoId = vaoId;
        this.vboIds = vboIds;
        this.indexCount = indexCount;
        this.material = material;
    }

    public void render() {
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0); // Position
        glEnableVertexAttribArray(1); // Normal
        glEnableVertexAttribArray(2); // UV
        glEnableVertexAttribArray(3); // Tangent

        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glBindVertexArray(0);
    }

    public void cleanup() {
        for (int vbo : vboIds) {
            glDeleteBuffers(vbo);
        }
        glDeleteVertexArrays(vaoId);
    }

    public Material getMaterial() {
        return material;
    }
}