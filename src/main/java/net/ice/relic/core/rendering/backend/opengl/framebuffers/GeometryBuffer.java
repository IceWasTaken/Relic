package net.ice.relic.core.rendering.backend.opengl.framebuffers;

import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL45.*;

public class GeometryBuffer {

	private final int posTexture;
	private final int albedoTexture;
	private final int normalTexture;
	private final int pbrTexture;

	private final int depthTexture;

	private final int handle;

	public GeometryBuffer(int width, int height) {
		this.handle = glCreateFramebuffers();

		this.posTexture = glCreateTextures(GL_TEXTURE_2D);
		this.albedoTexture = glCreateTextures(GL_TEXTURE_2D);
		this.normalTexture = glCreateTextures(GL_TEXTURE_2D);
		this.pbrTexture = glCreateTextures(GL_TEXTURE_2D);
		this.depthTexture = glCreateTextures(GL_TEXTURE_2D);

		glTextureStorage2D(posTexture, 1, GL_RGBA32F, width, height);
		glTextureParameteri(posTexture, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTextureParameteri(posTexture, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		glTextureStorage2D(albedoTexture, 1, GL_RGBA32F, width, height);
		glTextureParameteri(albedoTexture, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTextureParameteri(albedoTexture, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		glTextureStorage2D(normalTexture, 1, GL_RGBA32F, width, height);
		glTextureParameteri(normalTexture, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTextureParameteri(normalTexture, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		glTextureStorage2D(pbrTexture, 1, GL_RGBA32F, width, height);
		glTextureParameteri(pbrTexture, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTextureParameteri(pbrTexture, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		glTextureStorage2D(depthTexture, 1, GL_DEPTH_COMPONENT32F, width, height);
		glTextureParameteri(depthTexture, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTextureParameteri(depthTexture, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		glNamedFramebufferTexture(handle, GL_COLOR_ATTACHMENT0, posTexture, 0);
		glNamedFramebufferTexture(handle, GL_COLOR_ATTACHMENT1, albedoTexture, 0);
		glNamedFramebufferTexture(handle, GL_COLOR_ATTACHMENT2, normalTexture, 0);
		glNamedFramebufferTexture(handle, GL_COLOR_ATTACHMENT3, pbrTexture, 0);
		glNamedFramebufferTexture(handle, GL_DEPTH_ATTACHMENT, depthTexture, 0);

		try(MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer drawBuffers = stack.mallocInt(4);

			drawBuffers.put(0, GL_COLOR_ATTACHMENT0);
			drawBuffers.put(1, GL_COLOR_ATTACHMENT1);
			drawBuffers.put(2, GL_COLOR_ATTACHMENT2);
			drawBuffers.put(3, GL_COLOR_ATTACHMENT3);

			glNamedFramebufferDrawBuffers(handle, drawBuffers);
		}
		assertComplete();
	}

	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, handle);
	}

	public void unbind() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
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

	public void bindTextures() {
		glBindTextureUnit(0, posTexture);
		glBindTextureUnit(1, albedoTexture);
		glBindTextureUnit(2, normalTexture);
		glBindTextureUnit(3, pbrTexture);
	}
}
