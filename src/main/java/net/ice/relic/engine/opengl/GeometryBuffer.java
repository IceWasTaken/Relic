package net.ice.relic.engine.opengl;

import net.ice.relic.engine.RelicApplication;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class GeometryBuffer {

    private static final int TEXTURE_COUNT = 4;

    private int[] textureIDS;

    private FrameBufferObject geometryBuffer;
    private RelicApplication application;

    public GeometryBuffer(RelicApplication application) {
        this.application = application;
        this.textureIDS = new int[TEXTURE_COUNT];
    }

    public void init() {
        this.geometryBuffer = new FrameBufferObject();
        geometryBuffer.bind(GL_DRAW_FRAMEBUFFER);
        glGenTextures(textureIDS);

        for (int i = 0; i < TEXTURE_COUNT; i++) {
            glBindTexture(GL_TEXTURE_2D, textureIDS[i]);
            int type;
            if (i == TEXTURE_COUNT - 1) {
                // Last texture is depth buffer
                glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, application.getWindow().getWindowSize().x, application.getWindow().getWindowSize().y, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
                type = GL_DEPTH_ATTACHMENT;
            } else {
                // First 5 are color attachments
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, application.getWindow().getWindowSize().x, application.getWindow().getWindowSize().y, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
                type = GL_COLOR_ATTACHMENT0 + i;
            }
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glFramebufferTexture2D(GL_FRAMEBUFFER, type, GL_TEXTURE_2D, textureIDS[i], 0);
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer drawBuffers = stack.mallocInt(TEXTURE_COUNT - 1); // only color attachments
            for (int i = 0; i < TEXTURE_COUNT - 1; i++) {
                drawBuffers.put(i, GL_COLOR_ATTACHMENT0 + i);
            }
            glDrawBuffers(drawBuffers);
        }
        geometryBuffer.unbindFrameBuffer();
    }

    public void resize(int width, int height) {
        glDeleteTextures(textureIDS); // Clean up old textures
        textureIDS = new int[TEXTURE_COUNT];
        glGenTextures(textureIDS);

        geometryBuffer.bind(GL_FRAMEBUFFER);
        for (int i = 0; i < TEXTURE_COUNT; i++) {
            glBindTexture(GL_TEXTURE_2D, textureIDS[i]);
            int type;
            if (i == TEXTURE_COUNT - 1) {
                // Last texture is depth buffer
                glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
                type = GL_DEPTH_ATTACHMENT;
            } else {
                // First 5 are color attachments
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
                type = GL_COLOR_ATTACHMENT0 + i;
            }
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glFramebufferTexture2D(GL_FRAMEBUFFER, type, GL_TEXTURE_2D, textureIDS[i], 0);
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer drawBuffers = stack.mallocInt(TEXTURE_COUNT - 1);
            for (int i = 0; i < TEXTURE_COUNT - 1; i++) {
                drawBuffers.put(i, GL_COLOR_ATTACHMENT0 + i);
            }
            glDrawBuffers(drawBuffers);
        }

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Resized G-buffer is incomplete");
        }

        geometryBuffer.unbindFrameBuffer();
    }


    public void bind(int target) {
        glBindFramebuffer(target, geometryBuffer.getFboID());
    }

    public int[] getTextureIDS() {
        return textureIDS;
    }
    public FrameBufferObject getGeometryBuffer() {
        return geometryBuffer;
    }

}
