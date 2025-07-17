package net.ice.relic.engine.opengl.rendering;

import net.ice.relic.RelicApplication;
import net.ice.relic.engine.opengl.FrameBufferObject;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class GeometryBuffer {

    private static final int TEXTURE_COUNT = 4;

    private FrameBufferObject geometryBuffer;
    private int height;
    private int[] textureIDS;
    private int width;

    private RelicApplication application;

    public GeometryBuffer(RelicApplication application) {
        this.application = application;
    }

    public void init() {
        geometryBuffer = new FrameBufferObject();
        geometryBuffer.bind(GL_DRAW_FRAMEBUFFER);

        textureIDS = new int[TEXTURE_COUNT];
        glGenTextures(textureIDS);

        this.width = application.getWindow().getWidth();
        this.height = application.getWindow().getHeight();

        for (int i = 0; i < TEXTURE_COUNT; i++) {
            glBindTexture(GL_TEXTURE_2D, textureIDS[i]);
            int type;
            if (i == TEXTURE_COUNT - 1) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
                type = GL_DEPTH_ATTACHMENT;
            } else {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
                type = GL_COLOR_ATTACHMENT0 + i;
            }
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glFramebufferTexture2D(GL_FRAMEBUFFER, type, GL_TEXTURE_2D, textureIDS[i], 0);
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer drawBuffers = stack.mallocInt(TEXTURE_COUNT);
            for (int i = 0; i < TEXTURE_COUNT; i++) {
                drawBuffers.put(i, GL_COLOR_ATTACHMENT0 + i);
            }
            glDrawBuffers(drawBuffers);
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

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}