package net.ice.relic.core.rendering.backend.opengl.depricated.rendering.buffer;

import org.joml.Vector2i;

import static net.ice.relic.core.Shadows.SHADOW_MAP_COUNT;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL44.glBindTextures;

public class ShadowBuffer {

    public static final Vector2i SHADOW_MAP_SIZE = new Vector2i(4096);
    private static final int TEXTURE_COUNT = SHADOW_MAP_COUNT + 1;

    //private final FramebufferObject shadowMapFBO;
    //private int[] textureIDS;

    public ShadowBuffer() {
        //this.shadowMapFBO = new FramebufferObject();
        //textureIDS = new int[TEXTURE_COUNT];
        //glGenTextures(textureIDS);

//        shadowMapFBO.bind(FRAMEBUFFER);
//
//        for (int i = 0; i < TEXTURE_COUNT; i++) {
//            glBindTexture(GL_TEXTURE_2D, textureIDS[i]);
//            int type;
//            if (i == TEXTURE_COUNT - 1) {
//                glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, SHADOW_MAP_SIZE.x, SHADOW_MAP_SIZE.y, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
//                type = GL_DEPTH_ATTACHMENT;
//            } else {
//                glTexImage2D(GL_TEXTURE_2D, 0, GL_RG16F, SHADOW_MAP_SIZE.x, SHADOW_MAP_SIZE.y, 0, GL_RG, GL_FLOAT, (ByteBuffer) null);
//                type = GL_COLOR_ATTACHMENT0 + i;
//            }
//            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//
//            glFramebufferTexture2D(GL_FRAMEBUFFER, type, GL_TEXTURE_2D, textureIDS[i], 0);
//        }
//
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            IntBuffer drawBuffers = stack.mallocInt(TEXTURE_COUNT);
//            for (int i = 0; i < TEXTURE_COUNT; i++) {
//                drawBuffers.put(i, GL_COLOR_ATTACHMENT0 + i);
//            }
//            glDrawBuffers(drawBuffers);
//        }
//
//        shadowMapFBO.assertComplete();
//
//        shadowMapFBO.unbind(FRAMEBUFFER);
    }

    public void init() {

    }

    public void bindTextures() {
//        glActiveTexture(GL_TEXTURE4);
//        glBindTextures(GL_TEXTURE_2D, textureIDS);
    }

    public void cleanup() {
        //shadowMapArrayTexture.cleanup();
        //shadowMapFBO.delete();
    }

    public void shadowViewport() {
        glViewport(0, 0, SHADOW_MAP_SIZE.x, SHADOW_MAP_SIZE.y);
    }


//    public FramebufferObject getShadowMapFBO() {
//        return shadowMapFBO;
//    }
//
//    public int[] getShadowMapTextureIDs() {
//        return textureIDS;
//    }
}