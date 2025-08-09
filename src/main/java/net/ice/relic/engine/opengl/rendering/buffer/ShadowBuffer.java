package net.ice.relic.engine.opengl.rendering.buffer;

import net.ice.relic.engine.opengl.FrameBufferObject;
import net.ice.relic.core.Shadow;
import net.ice.relic.engine.opengl.model.texture.ArrayTexture;
import org.joml.Vector2i;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL30.*;

public class ShadowBuffer {

    public static final Vector2i SHADOW_MAP_SIZE = new Vector2i(4096, 4096);

    private final FrameBufferObject shadowMapFBO;
    private final ArrayTexture shadowMapArrayTexture;

    public ShadowBuffer() {
        this.shadowMapFBO = new FrameBufferObject();
        this.shadowMapArrayTexture = new ArrayTexture(Shadow.SHADOW_MAP_COUNT, SHADOW_MAP_SIZE, GL_DEPTH_COMPONENT);

        shadowMapFBO.bindFrameBuffer();
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowMapArrayTexture.getIds()[0], 0);

        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Failed to create shadow map FBO");
        }

        shadowMapFBO.unbindFrameBuffer();
    }


    public void bindTextures(int start) {
        for (int i = 0; i < Shadow.SHADOW_MAP_COUNT; i++) {
            glActiveTexture(start + i);
            glBindTexture(GL_TEXTURE_2D, shadowMapArrayTexture.getIds()[i]);
        }
    }

    public void cleanup() {
        shadowMapArrayTexture.cleanup();
        shadowMapFBO.delete();
    }

    public void shadowViewport() {
        glViewport(0, 0, SHADOW_MAP_SIZE.x, SHADOW_MAP_SIZE.y);
    }


    public FrameBufferObject getShadowMapFBO() {
        return shadowMapFBO;
    }

    public ArrayTexture getShadowMapArrayTexture() {
        return shadowMapArrayTexture;
    }
}
