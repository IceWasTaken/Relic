package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.context.GraphicsContext;
import net.ice.curio.graphics.object.resource.Texture;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.curio.library.opengl.object.Uniforms;
import net.ice.curio.library.opengl.object.VertexArrayObject;
import net.ice.curio.library.opengl.object.resource.GLTexture;
import net.ice.curio.library.stb.Bitmap;
import net.ice.heirloom.color.Colors;
import net.ice.heirloom.io.resource.Resource;
import net.ice.relic.core.particle.Particle;
import net.ice.relic.core.particle.ParticleSystem;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.ARBDirectStateAccess.glBindTextureUnit;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL30C.GL_MAP_WRITE_BIT;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class GLParticleRenderer {

	private GLShaderProgram shaderProgram;
	private Uniforms uniforms;
	
	private Texture sprite;

	private VertexArrayObject vertexArray;

	private GLBuffer vertexBuffer;

	private final GraphicsContext context;

	float[] particle_quad = {
			-0.5f, -0.5f, 0.0f,
			0.5f, -0.5f, 0.0f,
			-0.5f,  0.5f, 0.0f,
			0.5f,  0.5f, 0.0f,
	};

	public GLParticleRenderer(GraphicsContext context) {
		this.context = context;
	}

	public void init() {
		this.shaderProgram = new GLShaderProgram("particle");
		this.uniforms = new Uniforms(shaderProgram.getProgramID());

		uniforms.createUniform("viewMatrix");
		uniforms.createUniform("projectionMatrix");
		uniforms.createUniform("cameraRight");
		uniforms.createUniform("cameraUp");
		uniforms.createUniform("billboardPos");
		uniforms.createUniform("billboardSize");
		uniforms.createUniform("color");
		uniforms.createUniform("life");
		uniforms.createUniform("sprite");

		this.vertexArray = new VertexArrayObject();
		this.vertexBuffer = new GLBuffer(particle_quad.length * 4L, GL_MAP_WRITE_BIT);

		vertexArray.bind();

		vertexBuffer.putFloat(0, particle_quad);

		vertexArray.vertexBuffer(0, vertexBuffer, 0, 12);
		vertexArray.attributeFormat(0, 3, GL_FLOAT, false, 0);
		vertexArray.attributeBinding(0, 0);
		vertexArray.enableAttribute(0);

		glBindVertexArray(0);

		this.sprite = context.createTexture(new Bitmap(Resource.getResource("relic", "assets/textures/smoke1.png")));


		this.vertexBuffer.unmap();
	}

	public void render(ParticleSystem particleSystem, float delta, GLRenderer renderer) {
		shaderProgram.bind();

		particleSystem.updateParticles(delta);

		glBlendFunc(GL_SRC_ALPHA, GL_ONE);
		glDepthMask(false);

		for(Particle particle : particleSystem.getParticles()) {
			if(particle.getLife() > 0.0f) {
				Matrix4f viewMatrix = renderer.getApplication().getCurrentScene().getCamera().getViewMatrix();

				uniforms.setUniform("projectionMatrix", renderer.getApplication().getCurrentScene().getMatrix().getProjMatrix());
				uniforms.setUniform("viewMatrix", renderer.getApplication().getCurrentScene().getCamera().getViewMatrix());
				uniforms.setUniform("cameraRight", new Vector3f(viewMatrix.get(0, 0), viewMatrix.get(1, 0), viewMatrix.get(2, 0)));
				uniforms.setUniform("cameraUp", new Vector3f(viewMatrix.get(0, 1), viewMatrix.get(1, 1), viewMatrix.get(2, 1)));
				uniforms.setUniform("billboardPos", particle.getPosition());
				uniforms.setUniform("billboardSize", new Vector2f(1, 1));
				uniforms.setUniform("color", Colors.BLUE.getRGBColor().div().vec4f());
				uniforms.setUniform("life", particle.getLife());
				uniforms.setUniform("sprite", 0);

				int id = ((GLTexture) sprite).getTextureHandle();
				glBindTextureUnit(0, id);

				vertexArray.bind();
				glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
				vertexArray.unbind();
			}
		}

		glDepthMask(true);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	}
}
