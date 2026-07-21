package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.object.Viewport;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShader;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;
import net.ice.relic.core.rendering.backend.opengl.Uniforms;
import net.ice.relic.core.rendering.backend.opengl.mesh.QuadMesh;
import net.ice.relic.core.rendering.shader.ShaderType;
import net.ice.relic.core.scene.Scene;

import java.util.List;

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
		this.shaderProgram = new GLShaderProgram().attach(List.of(
				new GLShader(ShaderType.VERTEX).load("post.vert", ShaderType.VERTEX),
				new GLShader(ShaderType.FRAGMENT).load("post.frag", ShaderType.FRAGMENT)
		));

		this.uniforms = new Uniforms(shaderProgram);

		uniforms.createUniform("inputSampler");

		this.quadMesh = new QuadMesh();
	}

	@Override
	public void render() {
		shaderProgram.bind();
		Scene scene = renderer.getApplication().getCurrentScene();

		viewport.bind();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		renderer.getSwapBuffer().bindTextures();
		uniforms.setUniform("inputSampler", 0);

		quadMesh.getMeshVAO().bind();
		glDrawElements(GL_TRIANGLES, quadMesh.getVertexCount(), GL_UNSIGNED_INT, 0);

		shaderProgram.unbind();
	}

	public boolean shouldRender() {
		return enabled;
	}

	public void toggleRendering() {
		this.enabled = !enabled;
	}

	public void resize(int width, int height) {

	}
}
