package net.ice.relic.core.rendering.backend.opengl.depricated.rendering.buffer;

import net.ice.heirloom.Lifecycle;
import net.ice.relic.application.RelicApplication;
import net.ice.curio.library.opengl.object.framebuffer.FramebufferObject;
import net.ice.curio.library.opengl.wrapper.enums.FramebufferTarget;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static net.ice.curio.library.opengl.wrapper.enums.FramebufferTarget.DRAW_FRAMEBUFFER;
import static net.ice.curio.library.opengl.wrapper.enums.FramebufferTarget.FRAMEBUFFER;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

@Deprecated
public class GeometryBuffer implements Lifecycle {

    private static final int TEXTURE_COUNT = 5;

    private FramebufferObject framebufferObject;
    private int[] textureIDS;
    private int width;
    private int height;

    private RelicApplication application;

    public GeometryBuffer(RelicApplication application) {
        this.application = application;
    }

    @Override
    public void init() {
        //framebufferObject = new FramebufferObject();
        framebufferObject.bind(DRAW_FRAMEBUFFER);
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
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
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
        framebufferObject.assertComplete();
        framebufferObject.unbind(FRAMEBUFFER);
    }

    public void bind(FramebufferTarget target) {
        framebufferObject.bind(target);
    }

    public int[] getTextureIDS() {
        return textureIDS;
    }
    public FramebufferObject getFrameBuffer() {
        return framebufferObject;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}