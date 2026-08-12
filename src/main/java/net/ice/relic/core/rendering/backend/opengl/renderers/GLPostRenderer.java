package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.object.Viewport;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.Uniforms;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;
import net.ice.relic.core.rendering.backend.opengl.mesh.QuadMesh;
import net.ice.relic.core.scene.Scene;

import static org.lwjgl.opengl.GL11.*;

public class GLPostRenderer implements Lifecycle {

	private GLShaderProgram shaderProgram;
	private Uniforms uniforms;
	private QuadMesh quadMesh;
	private Viewport viewport;

	private boolean enabled = true;

	private final GLRenderer renderer;

	public GLPostRenderer(GLRenderer renderer) {
		this.renderer = renderer;
		this.viewport = renderer.getApplication().getCurio().getGraphicsContext().createViewport(
				renderer.getApplication().getWindow().getWidth(),
				renderer.getApplication().getWindow().getHeight()
		);
	}

	@Override
	public void init() {
		this.shaderProgram = new GLShaderProgram("post");


		this.uniforms = new Uniforms(shaderProgram);

		uniforms.createUniform("inputSampler");

		this.quadMesh = new QuadMesh();
	}

	@Override
	public void render() {
		if(enabled) {
			shaderProgram.bind();
			Scene scene = renderer.getApplication().getCurrentScene();

			viewport.bind();
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			renderer.getLightBuffer().bindTextures(0);
			uniforms.setUniform("inputSampler", 0);

			quadMesh.getMeshVAO().bind();
			glDrawElements(GL_TRIANGLES, quadMesh.getVertexCount(), GL_UNSIGNED_INT, 0);

			shaderProgram.unbind();
		}

	}

	public boolean shouldRender() {
		return enabled;
	}

	public void toggleRendering() {
		this.enabled = !enabled;
	}

	public void resize(int width, int height) {
		this.viewport.resize(width, height);
	}
}
