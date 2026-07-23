package net.ice.relic.core.rendering.backend.opengl.framebuffers;

import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL45.*;

public class SwapBuffer {

	private final int swapTexture;
	private final int depthTexture;

	private final int handle;

	public SwapBuffer(int width, int height) {
		this.handle = glCreateFramebuffers();

		this.swapTexture = glCreateTextures(GL_TEXTURE_2D);
		this.depthTexture = glCreateTextures(GL_TEXTURE_2D);

		glTextureStorage2D(swapTexture, 1, GL_RGBA32F, width, height);
		glTextureParameteri(swapTexture, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTextureParameteri(swapTexture, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		glTextureStorage2D(depthTexture, 1, GL_DEPTH_COMPONENT32F, width, height);
		glTextureParameteri(depthTexture, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTextureParameteri(depthTexture, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		glNamedFramebufferTexture(handle, GL_COLOR_ATTACHMENT0, swapTexture, 0);
		glNamedFramebufferTexture(handle, GL_DEPTH_ATTACHMENT, depthTexture, 0);

		try(MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer drawBuffers = stack.mallocInt(1);
			drawBuffers.put(0, GL_COLOR_ATTACHMENT0);
			glNamedFramebufferDrawBuffers(handle, drawBuffers);
		}
	}

	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}


	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, handle);
	}

	public void unbind() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public void bindTextures(int inxex) {
		glBindTextureUnit(inxex, swapTexture);
	}
}
