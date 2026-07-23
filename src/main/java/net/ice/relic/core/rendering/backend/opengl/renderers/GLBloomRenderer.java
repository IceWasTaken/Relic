package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.object.Viewport;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.Uniforms;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;
import net.ice.relic.core.rendering.backend.opengl.framebuffers.SwapBuffer;
import net.ice.relic.core.rendering.backend.opengl.mesh.QuadMesh;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;

public class GLBloomRenderer implements Lifecycle {

	private GLShaderProgram thresholdProgram;
	private GLShaderProgram blurProgram;
	private GLShaderProgram blendProgram;
	private GLShaderProgram swapProgram;

	private SwapBuffer thresholdToBlurSwap;
	private SwapBuffer blurToBlendSwap;

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
		this.thresholdProgram = new GLShaderProgram("bloom/brightpass");
		this.blurProgram = new GLShaderProgram("bloom/blur");
		this.blendProgram = new GLShaderProgram("bloom/blend");
		this.swapProgram = new GLShaderProgram("swap");

		this.thresholdToBlurSwap = new SwapBuffer(
				renderer.getApplication().getWindow().getWidth(),
				renderer.getApplication().getWindow().getHeight()
		);
		this.blurToBlendSwap = new SwapBuffer(
				renderer.getApplication().getWindow().getWidth(),
				renderer.getApplication().getWindow().getHeight()
		);

		thresholdProgram.getUniforms().createUniform("inputSampler");
		thresholdProgram.getUniforms().createUniform("threshold");

		blurProgram.getUniforms().createUniform("inputSampler");
		blurProgram.getUniforms().createUniform("horizontal");

		blendProgram.getUniforms().createUniform("inputSampler");
		blendProgram.getUniforms().createUniform("blurSampler");
		blendProgram.getUniforms().createUniform("exposure");

		swapProgram.getUniforms().createUniform("swap");

		this.quadMesh = new QuadMesh();
	}

	@Override
	public void render() {
		thresholdPass();
		blurPass(0);
		blurPass(1);
		blendPass();
		swapPass();

	}

	private void thresholdPass() {
		Uniforms uniforms = thresholdProgram.getUniforms();

		SwapBuffer sceneSwapBuffer = renderer.getLightBuffer();

		thresholdProgram.bind();

		thresholdToBlurSwap.bind();
		thresholdToBlurSwap.clear();

		sceneSwapBuffer.bindTextures(0);
		uniforms.setUniform("inputSampler", 0);
		uniforms.setUniform("threshold", 1f);

		quadMesh.getMeshVAO().bind();
		glDrawElements(GL_TRIANGLES, quadMesh.getVertexCount(), GL_UNSIGNED_INT, 0);

		thresholdProgram.unbind();
		thresholdToBlurSwap.unbind();
	}

	private void blurPass(int horizontal) {
		blurProgram.bind();

		Uniforms uniforms = blurProgram.getUniforms();

		blurToBlendSwap.bind();
		blurToBlendSwap.clear();

		thresholdToBlurSwap.bindTextures(0);
		uniforms.setUniform("inputSampler", 0);
		uniforms.setUniform("horizontal", horizontal);

		quadMesh.getMeshVAO().bind();
		glDrawElements(GL_TRIANGLES, quadMesh.getVertexCount(), GL_UNSIGNED_INT, 0);

		blurProgram.unbind();
		blurToBlendSwap.unbind();
	}

	private void blendPass() {
		blendProgram.bind();
		SwapBuffer sceneSwapBuffer = renderer.getLightBuffer();
		Uniforms uniforms = blendProgram.getUniforms();

		//just reuse
		thresholdToBlurSwap.bind();
		thresholdToBlurSwap.clear();

		sceneSwapBuffer.bindTextures(0);
		blurToBlendSwap.bindTextures(1);
		uniforms.setUniform("inputSampler", 0);
		uniforms.setUniform("blurSampler", 1);
		uniforms.setUniform("exposure", 1f);

		quadMesh.getMeshVAO().bind();
		glDrawElements(GL_TRIANGLES, quadMesh.getVertexCount(), GL_UNSIGNED_INT, 0);

		blendProgram.unbind();
		thresholdToBlurSwap.unbind();
	}

	private void swapPass() {
		swapProgram.bind();
		Uniforms uniforms = swapProgram.getUniforms();

		renderer.getLightBuffer().bind();
		renderer.getLightBuffer().clear();

		thresholdToBlurSwap.bindTextures(0);
		uniforms.setUniform("swap", 0);

		quadMesh.getMeshVAO().bind();
		glDrawElements(GL_TRIANGLES, quadMesh.getVertexCount(), GL_UNSIGNED_INT, 0);

		swapProgram.unbind();
		renderer.getSwapBuffer().unbind();
	}


	public void resize(int width, int height) {
		thresholdToBlurSwap = new SwapBuffer(width, height);
		blurToBlendSwap = new SwapBuffer(width, height);
		this.viewport.resize(width, height);
	}
}
