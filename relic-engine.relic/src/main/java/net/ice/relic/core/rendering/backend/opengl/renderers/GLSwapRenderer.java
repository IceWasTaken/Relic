package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.context.GraphicsContext;
import net.ice.curio.library.opengl.object.pipeline.GLPipeline;
import net.ice.relic.core.rendering.backend.opengl.mesh.QuadMesh;

import static org.lwjgl.opengl.ARBDirectStateAccess.glBindTextureUnit;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL30C.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30C.glBindFramebuffer;

//swaps a FBO to the main framebuffer
//can't just blit because of spec differences
public class GLSwapRenderer {

	private QuadMesh quadMesh;
	private GLPipeline pipeline;

	private final GraphicsContext graphicsContext;

	public GLSwapRenderer(GraphicsContext graphicsContext) {
		this.graphicsContext = graphicsContext;
	}

	public void init() {
		this.quadMesh = new QuadMesh();
		this.pipeline = new GLPipeline(
				graphicsContext,
				"swap",
				null,
				true
		);

		pipeline.getUniforms().createUniform("swap");
	}

	public void render(GLRenderer renderer) {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glEnable(GL_BLEND);
		glBlendEquation(GL_FUNC_ADD);
		glBlendFunc(GL_ONE, GL_ONE);

		pipeline.bindPipeline();

		glBindTextureUnit(0, renderer.lightRenderer.getPipeline().getFramebuffer().get().getTextures()[0]);

		pipeline.getUniforms().setUniform("swap", 0);

		quadMesh.getMeshVAO().bind();
		glDrawElements(GL_TRIANGLES, quadMesh.getVertexCount(), GL_UNSIGNED_INT, 0);
	}

	public void resize(int width, int height) {
		pipeline.resize(width, height);
	}
}
