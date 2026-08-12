package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.object.Viewport;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.Uniforms;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;
import net.ice.relic.core.rendering.backend.opengl.framebuffers.BloomBuffer;
import net.ice.relic.core.rendering.backend.opengl.framebuffers.SwapBuffer;
import net.ice.relic.core.rendering.backend.opengl.mesh.QuadMesh;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;

public class GLBloomRenderer implements Lifecycle {

	private GLShaderProgram downsampleProgram;
	private GLShaderProgram upsampleProgram;
	private GLShaderProgram blendProgram;
	private GLShaderProgram swapProgram;

	private BloomBuffer mipChain;
	private SwapBuffer swap;

	private QuadMesh quadMesh;
	private Viewport viewport;

	private final GLRenderer renderer;

	public GLBloomRenderer(GLRenderer renderer) {
		this.renderer = renderer;

		this.viewport = renderer.getApplication().getCurio().getGraphicsContext().createViewport(
				renderer.getApplication().getWindow().getWidth(),
				renderer.getApplication().getWindow().getHeight()
		);
	}

	@Override
	public void init() {
		this.downsampleProgram = new GLShaderProgram("bloom/downsample");
		this.upsampleProgram = new GLShaderProgram("bloom/upsample");
		this.blendProgram = new GLShaderProgram("bloom/blend");
		this.swapProgram = new GLShaderProgram("swap");

		this.mipChain = new BloomBuffer(
				renderer.getApplication().getWindow().getWidth(),
				renderer.getApplication().getWindow().getHeight(),
				6
		);
		this.swap = new SwapBuffer(
				renderer.getApplication().getWindow().getWidth(),
				renderer.getApplication().getWindow().getHeight()
		);

		downsampleProgram.getUniforms().createUniform("srcResolution");
		downsampleProgram.getUniforms().createUniform("mipLevel");
		downsampleProgram.getUniforms().createUniform("srcTexture");

		upsampleProgram.getUniforms().createUniform("srcTexture");
		upsampleProgram.getUniforms().createUniform("filterRadius");

		blendProgram.getUniforms().createUniform("inputSampler");
		blendProgram.getUniforms().createUniform("bloomSampler");
		blendProgram.getUniforms().createUniform("exposure");

		swapProgram.getUniforms().createUniform("swap");

		this.quadMesh = new QuadMesh();
	}

	@Override
	public void render() {
		renderBloomTexture();
		blendPass();
		swapPass();

	}

	private void downsamplePass() {
		Uniforms uniforms = downsampleProgram.getUniforms();
		downsampleProgram.bind();

		uniforms.setUniform("mipLevel", 0);

		renderer.getLightBuffer().bindTextures(0);
		uniforms.setUniform("srcTexture", 0);

		for (int i = 0; i < 6; i++) {
			BloomBuffer.BloomMip mip = mipChain.getMipTextures()[i];

			glViewport(0, 0, mip.size().x, mip.size().y);
			mipChain.changeTexture(i);

			quadMesh.getMeshVAO().bind();
			glDrawElements(GL_TRIANGLES, quadMesh.getVertexCount(), GL_UNSIGNED_INT, 0);

			uniforms.setUniform("srcResolution", mip.size());

			mipChain.bindTextures(0, i);

			if(i == 0) {
				uniforms.setUniform("mipLevel", 1);
			}
		}

		downsampleProgram.unbind();
	}

	private void upsamplePass() {
		Uniforms uniforms = upsampleProgram.getUniforms();

		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE);
		glBlendEquation(GL_FUNC_ADD);

		upsampleProgram.bind();

		uniforms.setUniform("srcTexture", 0);
		uniforms.setUniform("filterRadius", 1.0f);
		quadMesh.getMeshVAO().bind();

		for (int i = 6 - 1; i > 0; i--) {
			BloomBuffer.BloomMip mip = mipChain.getMipTextures()[i];
			BloomBuffer.BloomMip nextMip = mipChain.getMipTextures()[i - 1];

			mipChain.bindTextures(0, i);
			glViewport(0, 0, nextMip.size().x, nextMip.size().y);
			mipChain.changeTexture(i - 1);

			quadMesh.getMeshVAO().bind();
			glDrawElements(GL_TRIANGLES, quadMesh.getVertexCount(), GL_UNSIGNED_INT, 0);
		}

		//glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_BLEND);

		upsampleProgram.unbind();
	}

	private void renderBloomTexture() {
		mipChain.bind();

		downsamplePass();
		upsamplePass();

		viewport.bind();
	}

	private void blendPass() {
		blendProgram.bind();
		SwapBuffer sceneSwapBuffer = renderer.getLightBuffer();
		Uniforms uniforms = blendProgram.getUniforms();

		swap.bind();
		swap.clear();

		sceneSwapBuffer.bindTextures(0);
		mipChain.bindTextures(1, 0);
		uniforms.setUniform("inputSampler", 0);
		uniforms.setUniform("bloomSampler", 1);
		uniforms.setUniform("exposure", 1f);

		quadMesh.getMeshVAO().bind();
		glDrawElements(GL_TRIANGLES, quadMesh.getVertexCount(), GL_UNSIGNED_INT, 0);

		blendProgram.unbind();
		swap.unbind();
	}

	private void swapPass() {
		swapProgram.bind();
		Uniforms uniforms = swapProgram.getUniforms();

		renderer.getLightBuffer().bind();
		renderer.getLightBuffer().clear();

		swap.bindTextures(0);
		uniforms.setUniform("swap", 0);

		quadMesh.getMeshVAO().bind();
		glDrawElements(GL_TRIANGLES, quadMesh.getVertexCount(), GL_UNSIGNED_INT, 0);

		swapProgram.unbind();
		renderer.getSwapBuffer().unbind();
	}


	public void resize(int width, int height) {
		this.mipChain = new BloomBuffer(width, height, 6);
		this.viewport.resize(width, height);
	}
}
