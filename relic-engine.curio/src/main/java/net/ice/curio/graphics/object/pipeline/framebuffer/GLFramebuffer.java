package net.ice.curio.graphics.object.pipeline.framebuffer;

import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL45.*;

public class GLFramebuffer {

	private final int[] textures;
	private final int depthTexture;

	private final int format;
	private final int handle;

	public GLFramebuffer(int target, int width, int height, int texCount, int format) {
		this.textures = new int[texCount];
		this.handle = glCreateFramebuffers();
		this.format = format;

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

	public void resize(int width, int height) {
		for (int texture : textures) {
			glTextureStorage2D(texture, 1, format, width, height);
		}
		glTextureStorage2D(depthTexture, 1, GL_DEPTH_COMPONENT32F, width, height);
	}
}
