package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.context.GraphicsContext;
import net.ice.curio.graphics.object.pipeline.framebuffer.GLFramebuffer;
import net.ice.curio.library.opengl.OpenGLContext;
import net.ice.curio.library.opengl.object.Uniforms;
import net.ice.relic.core.ProjectionMatrix;
import net.ice.relic.core.particle.ParticleSystem;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.ARBComputeShader.glDispatchCompute;
import static org.lwjgl.opengl.ARBDirectStateAccess.glBindTextureUnit;

public class GLParticleCompute {

	private GLShaderProgram shaderProgram;
	private Uniforms uniforms;

	private final OpenGLContext context;

	public GLParticleCompute(GraphicsContext context) {
		this.context = (OpenGLContext) context;
	}

	public void init() {
		this.shaderProgram = new GLShaderProgram("particle_compute");
		this.uniforms = new Uniforms(shaderProgram.getProgramID());

		uniforms.createUniform("viewProjectionMatrix");
		uniforms.createUniform("particleCount");
		uniforms.createUniform("deltaTime");
		uniforms.createUniform("nearPlane");
		uniforms.createUniform("farPlane");

		uniforms.createUniform("sceneDepth");
		uniforms.createUniform("sceneNormal");
	}

	public void render(GLRenderer renderer) {
		Matrix4f projMatrix = renderer.getCurrentScene().getMatrix().getProjMatrix();
		Matrix4f viewMatrix = renderer.getCurrentScene().getCamera().getViewMatrix();

		shaderProgram.bind();

		int size = (ParticleSystem.getInstance().getParticleCount() + 64 - 1) / 64;

		uniforms.setUniform("viewProjectionMatrix", projMatrix.mul(viewMatrix, new Matrix4f()));
		uniforms.setUniform("particleCount", ParticleSystem.getInstance().getParticleCount());
		uniforms.setUniform("deltaTime", renderer.getApplication().getClock().getDeltaTime());
		uniforms.setUniform("nearPlane", ProjectionMatrix.Z_NEAR);
		uniforms.setUniform("farPlane", ProjectionMatrix.Z_FAR);

		GLFramebuffer fb = renderer.sceneRenderer.getPipeline().getFramebuffer().get();

		glBindTextureUnit(0, fb.getDepthTexture());
		glBindTextureUnit(1, fb.getTextures()[2]);

		uniforms.setUniform("sceneDepth", 0);
		uniforms.setUniform("sceneNormal", 1);

		glDispatchCompute(size, 1, 1);
	}

	public record Particle(
			Vector4f color,
			Vector3f position,
			Vector3f velocity,
			Vector2f scale,
			float life,
			float collisionSize
	){}



}
