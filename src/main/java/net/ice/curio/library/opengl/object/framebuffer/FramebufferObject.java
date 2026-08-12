package net.ice.curio.library.opengl.object.framebuffer;

import net.ice.curio.library.opengl.wrapper.enums.FramebufferTarget;
import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS;
import static org.lwjgl.opengl.GL45.*;

public class FramebufferObject {

    private final int handle;
    private final int[] textures;

    public FramebufferObject(Vector2i size, int count, FramebufferImageSettings settings) {
        this.handle = glCreateFramebuffers();

        glCreateTextures(GL_TEXTURE_2D, textures = new int[count]);

        for (int i = 0; i < count; i++) {
            int type;

            int textureHandle = textures[i];

            if (i == count - 1) {
                //depth
                glTextureStorage2D(textureHandle, 1, GL_DEPTH_COMPONENT32F, size.x, size.y);
                type = GL_DEPTH_ATTACHMENT;
            } else {
                //color
                glTextureStorage2D(textureHandle, 1, settings.internalFormat, size.x, size.y);
                type = GL_COLOR_ATTACHMENT0 + i;
            }

            glTextureParameteri(textureHandle, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTextureParameteri(textureHandle, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

            glNamedFramebufferTexture(handle, type, textures[i], 0);
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            int colorAttachments = count - 1;

            IntBuffer drawBuffers = stack.mallocInt(colorAttachments);

            for (int i = 0; i < colorAttachments; i++) {
                drawBuffers.put(i, GL_COLOR_ATTACHMENT0 + i);
            }

            glNamedFramebufferDrawBuffers(handle, drawBuffers);
        }
        assertComplete();
    }

    public FramebufferObject(Vector2i size, int count) {
        this(size, count, new FramebufferImageSettings(GL_RGBA32F, GL_RGBA, GL_FLOAT));
    }

    public void cleanup() {
        glDeleteFramebuffers(handle);
    }

    public void bind(FramebufferTarget target) {
        glBindFramebuffer(target.getGlEnum(), handle);
    }

    public void unbind(FramebufferTarget target) {
        glBindFramebuffer(target.getGlEnum(), 0);
    }

    public int[] getTextures() {
        return textures;
    }

    public void assertComplete() {
        int status = glCheckNamedFramebufferStatus(handle, GL_FRAMEBUFFER);
        switch (status) {

            case GL_FRAMEBUFFER_UNDEFINED -> throw new RuntimeException("Specified framebuffer is the default read or draw framebuffer, but the default framebuffer does not exist.");
            case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> throw new RuntimeException("Framebuffer attachment points are framebuffer incomplete.");
            case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT  -> throw new RuntimeException("Framebuffer does not have at least one image attached to it.");
            case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER  -> throw new RuntimeException("Value of GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE is GL_NONE for any color attachment point(s) named by GL_DRAW_BUFFERi.");
            case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER  -> throw new RuntimeException("GL_READ_BUFFER is not GL_NONE and the value of GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE is GL_NONE for the color attachment point named by GL_READ_BUFFER");
            case GL_FRAMEBUFFER_UNSUPPORTED  -> throw new RuntimeException("Combination of internal formats of the attached images violates an implementation-dependent set of restrictions.");
            case GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE  -> throw new RuntimeException("Value of GL_RENDERBUFFER_SAMPLES is not the same for all attached renderbuffers; if the value of GL_TEXTURE_SAMPLES is the not same for all attached textures; or, if the attached images are a mix of renderbuffers and textures, the value of GL_RENDERBUFFER_SAMPLES does not match the value of GL_TEXTURE_SAMPLES.");
            case GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS  -> throw new RuntimeException("Framebuffer attachment is layered, and any populated attachment is not layered, or if all populated color attachments are not from textures of the same target.");
            case GL_FRAMEBUFFER_COMPLETE -> {
                return;
            }
            default -> {
                throw new RuntimeException("Unknown framebuffer status: " + status);
            }
        }
    }

    public record FramebufferImageSettings(int internalFormat, int format, int type) {

    }
}
