package net.ice.curio.library.opengl.object.resource;

import static net.ice.curio.library.opengl.object.resource.GLSamplers.SamplerFormat.*;
import static org.lwjgl.opengl.ARBTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL33.glSamplerParameterf;
import static org.lwjgl.opengl.GL33.glSamplerParameteri;
import static org.lwjgl.opengl.ARBDirectStateAccess.*;

public class GLSamplers {

	public final GLSampler SAMPLER_UI;
	public final GLSampler SAMPLER_UI_CLAMPED;
	public final GLSampler SAMPLER_BILINEAR_FILTERING;
	public final GLSampler SAMPLER_TRILINEAR_FILTERING;

	public GLSamplers() {
		SAMPLER_UI = new GLSampler(UI);
		SAMPLER_UI_CLAMPED = new GLSampler(UI_CLAMPED);
		SAMPLER_BILINEAR_FILTERING = new GLSampler(BILINEAR_FILTERING);
		SAMPLER_TRILINEAR_FILTERING = new GLSampler(TRILINEAR_FILTERING);
	}

	public static class GLSampler {
		private final int handle;

		private GLSampler(SamplerFormat samplerFormat) {
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
	}

	enum SamplerFormat {
		UI(GL_REPEAT, GL_REPEAT, GL_REPEAT, GL_NEAREST, GL_NEAREST, 16.0f),
		UI_CLAMPED(GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, GL_NEAREST, GL_NEAREST, 16.0f),
		BILINEAR_FILTERING(GL_REPEAT, GL_REPEAT, GL_REPEAT, GL_LINEAR, GL_LINEAR, 16.0f),
		TRILINEAR_FILTERING(GL_REPEAT, GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR, 16.0f);

		private final int wrapS;
		private final int wrapT;
		private final int wrapR;
		private final int minFilter;
		private final int magFilter;
		private final float anisotropicFactor;

		SamplerFormat(int wrapS, int wrapT, int wrapR, int minFilter, int magFilter, float anisotropicFactor) {
			this.wrapS = wrapS;
			this.wrapT = wrapT;
			this.wrapR = wrapR;
			this.minFilter = minFilter;
			this.magFilter = magFilter;
			this.anisotropicFactor = anisotropicFactor;
		}
	}


}
