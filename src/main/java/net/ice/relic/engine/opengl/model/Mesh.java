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
    private final int vboId;
    private final int eboId;
    private final int vertexCount;
    private final int textureId;
    private final float emissiveStrength;
    private final float[] emissiveColor;
    private final boolean hasEmissiveMap;
    private final int emissiveTextureId;
    private final int normalTextureID;

    public Mesh(List<Float> vertices, List<Integer> indices, int textureId, float emissiveStrength, float[] emissiveColor, boolean hasEmissiveMap, int emissiveTextureId, int normalTextureID) {
        this.vertexCount = indices.size();
        this.textureId = textureId;
        this.emissiveStrength = emissiveStrength;
        this.emissiveColor = emissiveColor;
        this.hasEmissiveMap = hasEmissiveMap;
        this.emissiveTextureId = emissiveTextureId;
        this.normalTextureID = normalTextureID;

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);

        FloatBuffer vertexBuffer = memAllocFloat(vertices.size());
        for (Float v : vertices) vertexBuffer.put(v);
        vertexBuffer.flip();
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);

        IntBuffer indexBuffer = memAllocInt(indices.size());
        for (Integer i : indices) indexBuffer.put(i);
        indexBuffer.flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        int stride = 8 * Float.BYTES;
        setupAttribute(0, 3, GL_FLOAT, false, stride, 0);                     // Position
        setupAttribute(1, 3, GL_FLOAT, false, stride, 3 * Float.BYTES);      // Normal
        setupAttribute(2, 2, GL_FLOAT, false, stride, 6 * Float.BYTES);      // TexCoords

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        memFree(vertexBuffer);
        memFree(indexBuffer);
    }

    private void setupAttribute(int index, int size, int type, boolean normalized, int stride, long pointer) {
        glVertexAttribPointer(index, size, type, normalized, stride, pointer);
        glEnableVertexAttribArray(index);
    }

    public void render(int shaderProgramId) {
        if (textureId != -1) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, textureId);
        }

        if (hasEmissiveMap && emissiveTextureId != -1) {
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, emissiveTextureId);
        }

        if (normalTextureID != -1) {
            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, normalTextureID);
        }

        glUniform1f(glGetUniformLocation(shaderProgramId, "emissiveStrength"), emissiveStrength);
        glUniform3f(glGetUniformLocation(shaderProgramId, "emissiveColor"), emissiveColor[0], emissiveColor[1], emissiveColor[2]);
        glUniform1i(glGetUniformLocation(shaderProgramId, "hasEmissiveMap"), hasEmissiveMap ? 1 : 0);
        glUniform1i(glGetUniformLocation(shaderProgramId, "emissiveMap"), 1);
        glUniform1i(glGetUniformLocation(shaderProgramId, "normalMap"), 2);

        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        // Unbind textures
        if (textureId != -1) glBindTexture(GL_TEXTURE_2D, 0);
        if (emissiveTextureId != -1) glBindTexture(GL_TEXTURE_2D, 0);
        if (normalTextureID != -1) glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void cleanup() {
        glDeleteBuffers(vboId);
        glDeleteBuffers(eboId);
        glDeleteVertexArrays(vaoId);
        if (textureId != -1) {
            glDeleteTextures(textureId);
        }
        if (emissiveTextureId != -1) {
            glDeleteTextures(emissiveTextureId);
        }
        if (normalTextureID != -1) {
            glDeleteTextures(normalTextureID);
        }
    }
}
