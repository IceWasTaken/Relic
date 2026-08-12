package net.ice.relic.core.rendering.backend.opengl.framebuffers;

import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.opengl.GL45.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL45.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
import static org.lwjgl.opengl.GL45.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER;
import static org.lwjgl.opengl.GL45.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
import static org.lwjgl.opengl.GL45.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE;
import static org.lwjgl.opengl.GL45.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER;
import static org.lwjgl.opengl.GL45.GL_FRAMEBUFFER_UNDEFINED;
import static org.lwjgl.opengl.GL45.GL_FRAMEBUFFER_UNSUPPORTED;
import static org.lwjgl.opengl.GL45.GL_LINEAR;
import static org.lwjgl.opengl.GL45.GL_R11F_G11F_B10F;
import static org.lwjgl.opengl.GL45.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL45.GL_TEXTURE_MIN_FILTER;

public class BloomBuffer {

	private final BloomMip[] mipTextures;

	private final int handle;

	public BloomBuffer(int width, int height, int length) {
		if(width == 0 || height == 0) {
			this.mipTextures = new BloomMip[0];
			this.handle = 0;
			return;
		}

		this.mipTextures = new BloomMip[length];

		this.handle = glCreateFramebuffers();

		Vector2i mipSize = new Vector2i(width, height);

		for (int i = 0; i < length; i++) {
			mipSize = mipSize.div(2);

			BloomMip mip = new BloomMip(mipSize, glCreateTextures(GL_TEXTURE_2D));

			glTextureStorage2D(mip.texture, 1, GL_R11F_G11F_B10F, width, height);

			glTextureParameteri(mip.texture, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTextureParameteri(mip.texture, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTextureParameteri(mip.texture, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTextureParameteri(mip.texture, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

			mipTextures[i] = mip;
		}

		glNamedFramebufferTexture(handle, GL_COLOR_ATTACHMENT0, mipTextures[0].texture, 0);

		try(MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer drawBuffers = stack.mallocInt(1);
			drawBuffers.put(0, GL_COLOR_ATTACHMENT0);
			glNamedFramebufferDrawBuffers(handle, drawBuffers);
		}

		assertComplete();
	}

	public void bindTextures(int bindPos, int index) {
		glBindTextureUnit(bindPos, mipTextures[index].texture);
	}

	public void changeTexture(int i) {
		glNamedFramebufferTexture(handle, GL_COLOR_ATTACHMENT0, mipTextures[i].texture, 0);
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

	public BloomMip[] getMipTextures() {
		return mipTextures;
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

	public record BloomMip(Vector2i size, int texture) {}
}
