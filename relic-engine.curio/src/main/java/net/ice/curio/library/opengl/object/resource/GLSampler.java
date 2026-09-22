package net.ice.curio.library.opengl.object.resource;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL33.glSamplerParameterf;
import static org.lwjgl.opengl.GL33.glSamplerParameteri;
import static org.lwjgl.opengl.GL45.glCreateSamplers;
import static org.lwjgl.opengl.GL46.GL_TEXTURE_MAX_ANISOTROPY;

public class GLSampler {

	private final int handle;

	public GLSampler(SamplerFormat samplerFormat) {
		this.handle = glCreateSamplers();

		glSamplerParameteri(handle, GL_TEXTURE_MIN_FILTER, samplerFormat.minFilter);
		glSamplerParameteri(handle, GL_TEXTURE_MAG_FILTER, samplerFormat.magFilter);
		glSamplerParameteri(handle, GL_TEXTURE_WRAP_S, samplerFormat.wrapS);
		glSamplerParameteri(handle, GL_TEXTURE_WRAP_T, samplerFormat.wrapT);
		glSamplerParameteri(handle, GL_TEXTURE_WRAP_R, samplerFormat.wrapR);
		glSamplerParameterf(handle, GL_TEXTURE_MAX_ANISOTROPY, samplerFormat.anisotropicFactor);
	}

	int getHandle() {
		return handle;
	}

	public record SamplerFormat(
			int wrapS,
			int wrapT,
			int wrapR,
			int minFilter,
			int magFilter,
			float anisotropicFactor
	) {
		public static SamplerFormat UI = new SamplerFormat(GL_REPEAT, GL_REPEAT, GL_REPEAT, GL_NEAREST, GL_NEAREST, 16.0f);
		public static SamplerFormat UI_CLAMPED = new SamplerFormat(GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, GL_NEAREST, GL_NEAREST, 16.0f);
		public static SamplerFormat BILINEAR_FILTERING = new SamplerFormat(GL_REPEAT, GL_REPEAT, GL_REPEAT, GL_LINEAR, GL_LINEAR, 16.0f);
		public static SamplerFormat TRILINEAR_FILTERING = new SamplerFormat(GL_REPEAT, GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR, 16.0f);
	}


}
