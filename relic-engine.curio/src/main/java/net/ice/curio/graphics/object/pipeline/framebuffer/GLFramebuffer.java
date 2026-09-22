package net.ice.curio.graphics.object.pipeline.framebuffer;

import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL45.*;

public class GLFramebuffer {

	private final int[] textures;
	private final int depthTexture;

	private final int format;
	private final int handle;
	private final int target;

	public GLFramebuffer(int target, int width, int height, int texCount, int format) {
		this.textures = new int[texCount];
		this.handle = glCreateFramebuffers();
		this.format = format;
		this.target = target;

		for (int i = 0; i < texCount; i++) {
			int texture = glCreateTextures(target);
			glTextureStorage2D(texture, 1, format, width, height);
			glTextureParameteri(texture, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTextureParameteri(texture, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			glNamedFramebufferTexture(handle, GL_COLOR_ATTACHMENT0 + i, texture, 0);

			textures[i] = texture;
		}

		this.depthTexture = glCreateTextures(target);
		glTextureStorage2D(depthTexture, 1, GL_DEPTH_COMPONENT32F, width, height);
		glTextureParameteri(depthTexture, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTextureParameteri(depthTexture, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glNamedFramebufferTexture(handle, GL_DEPTH_ATTACHMENT, depthTexture, 0);

		try(MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer drawBuffers = stack.mallocInt(texCount);

			for (int i = 0; i < texCount; i++) {
				drawBuffers.put(i, GL_COLOR_ATTACHMENT0 + i);
			}

			glNamedFramebufferDrawBuffers(handle, drawBuffers);
		}

		assertComplete();
	}

	public void cleanup() {
		glDeleteTextures(textures);
		glDeleteFramebuffers(handle);
	}

	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, handle);
	}

	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	public void bindTextures(int starting) {
		for (int i = 0; i < textures.length; i++) {
			glBindTextureUnit(starting + i, textures[i]);
		}
	}

	public GLFramebuffer resize(int width, int height) {
		cleanup();
		return new GLFramebuffer(target, width, height, textures.length, format);
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
			default -> throw new RuntimeException("[GLFramebuffer]: Unknown framebuffer status: " + status);
		}
	}
}
