package net.ice.relic.core.rendering.backend.opengl.framebuffers;

import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import org.joml.Vector2i;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

public class ShadowBuffer {

	private final Vector2i size = GLRenderer.SHADOW_MAP_SIZE;

	private final int handle;
	private final int textureArray;
	private final int depthTextureArray;

	public static final int LAYER_COUNT = 3;

	public ShadowBuffer() {
		textureArray = glGenTextures();
		glBindTexture(GL_TEXTURE_2D_ARRAY, textureArray);

		glTexImage3D(
				GL_TEXTURE_2D_ARRAY,
				0,
				GL_RG32F,
				size.x,
				size.y,
				LAYER_COUNT,
				0,
				GL_RG,
				GL_FLOAT,
				(ByteBuffer) null
		);

		glTexParameterf(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		glTexParameterf(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);

		this.depthTextureArray = glGenTextures();
		glBindTexture(GL_TEXTURE_2D_ARRAY, depthTextureArray);

		glTexImage3D(
				GL_TEXTURE_2D_ARRAY,
				0,
				GL_DEPTH_COMPONENT32F,
				size.x,
				size.y,
				LAYER_COUNT,
				0,
				GL_DEPTH_COMPONENT,
				GL_FLOAT,
				(ByteBuffer) null
		);

		glTexParameterf(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		glTexParameterf(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);

		this.handle = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, handle);

		glFramebufferTexture(
				GL_FRAMEBUFFER,
				GL_COLOR_ATTACHMENT0,
				textureArray,
				0
		);

		glFramebufferTexture(
				GL_FRAMEBUFFER,
				GL_DEPTH_ATTACHMENT,
				depthTextureArray,
				0
		);

		glDrawBuffers(GL_COLOR_ATTACHMENT0);

		assertComplete();

		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public void bindFramebuffer() {
		glBindFramebuffer(GL_FRAMEBUFFER, handle);
		glViewport(0, 0, size.x, size.y);
	}

	public static void unbindFramebuffer() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	public void bindTextureArray(int textureUnit) {
		glActiveTexture(textureUnit);
		glBindTexture(GL_TEXTURE_2D_ARRAY, textureArray);
	}

	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	public void assertComplete() {
		int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
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
			default -> throw new RuntimeException("Unknown framebuffer status: " + status);
		}
	}
}
