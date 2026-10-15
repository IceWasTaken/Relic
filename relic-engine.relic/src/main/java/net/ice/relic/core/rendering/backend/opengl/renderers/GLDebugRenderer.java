package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.context.GraphicsContext;
import net.ice.curio.library.opengl.object.VertexArrayObject;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.heirloom.color.Colors;
import net.ice.heirloom.color.RGBColor;
import net.ice.curio.library.opengl.object.Uniforms;
import net.ice.relic.core.ecs.component.components.TransformComponent;
import net.ice.relic.core.ecs.component.components.rendering.ModelComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.mesh.MeshData;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;
import net.ice.relic.core.scene.Scene;
import net.ice.relic.core.scene.light.Light;
import net.ice.relic.core.scene.primitives.threed.LineCube;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.GL_MAP_WRITE_BIT;

public class GLDebugRenderer {

	private GLShaderProgram shaderProgram;
	private Uniforms uniforms;

	private VertexArrayObject vertexArrayObject;

	private GLBuffer vertexBuffer;
	private GLBuffer indexBuffer;

	private static RGBColor notVisibleColor = new RGBColor(78, 59, 255);
	private static RGBColor visibleColor = notVisibleColor.lighter();

	private final GraphicsContext context;

	private final LineCube lineCube = new LineCube();

	public GLDebugRenderer(GraphicsContext context) {
		this.context = context;
	}

	public void init() {
		this.vertexArrayObject = new VertexArrayObject();
		this.vertexBuffer = new GLBuffer(lineCube.getVertices().length * 4L, GL_MAP_WRITE_BIT);
		this.indexBuffer = new GLBuffer(lineCube.getIndices().length * 4L, GL_MAP_WRITE_BIT);

		vertexArrayObject.bind();

		vertexBuffer.putFloat(0, lineCube.getVertices());

		vertexArrayObject.vertexBuffer(0, vertexBuffer, 0, 12);
		vertexArrayObject.attributeFormat(0, 3, GL_FLOAT, false, 0);
		vertexArrayObject.attributeBinding(0, 0);
		vertexArrayObject.enableAttribute(0);

		indexBuffer.putInt(0, lineCube.getIndices());

		vertexArrayObject.elementBuffer(indexBuffer);

		glBindVertexArray(0);

		this.shaderProgram = new GLShaderProgram("debug");


		this.uniforms = new Uniforms(shaderProgram.getProgramID());

		uniforms.createUniform("color");
		uniforms.createUniform("centerPos");
		uniforms.createUniform("viewMatrix");
		uniforms.createUniform("projectionMatrix");

		vertexBuffer.unmap();
		indexBuffer.unmap();
	}

	public void render(GLRenderer renderer) {
		shaderProgram.bind();
		Scene scene = renderer.getApplication().getCurrentScene();

		uniforms.setUniform("viewMatrix", scene.getCamera().getViewMatrix());
		uniforms.setUniform("projectionMatrix", scene.getMatrix().getProjMatrix());

		vertexArrayObject.bind();

		glDisable(GL_DEPTH_TEST);
		uniforms.setUniform("color", notVisibleColor.div().vec3f());
		for(Light light : scene.getLights()) {
			drawCube(light.getPosition(), new Vector3f(1, 1, 1));
		}

		for(Entity entity : scene.getEntities()) {
			if(entity.getComponent(ModelComponent.class) != null) {
				for(MeshData meshData : entity.getComponent(ModelComponent.class).getModel().getMeshData()) {
					uniforms.setUniform("color", Colors.RED.getRGBColor().div().vec3f());

					TransformComponent tComponent = entity.getComponent(TransformComponent.class);

					Matrix4f matrix4f = new Matrix4f(tComponent.getTransformationMatrix());
					matrix4f.translate(meshData.getAabb().getCenter());
					matrix4f.scale(meshData.getAabb().getSize().mul(0.5f, new Vector3f()));
					drawCube(matrix4f);
				}
			}
		}

//		for(Particle particle : scene.getApplication().getParticleSystem().getParticles()) {
//			uniforms.setUniform("color", particle.getColor().div().vec3f());
//			drawCube(particle.getPosition());
//		}

		glEnable(GL_DEPTH_TEST);
		uniforms.setUniform("color", visibleColor.div().vec3f());
		for(Light light : scene.getLights()) {
			drawCube(light.getPosition(), new Vector3f(1, 1, 1));
		}

		glBindVertexArray(0);
		shaderProgram.unbind();
	}


	private void drawCube(Vector3f position, Vector3f scale) {
		uniforms.setUniform("centerPos", new Matrix4f().identity().translate(position).scale(scale));
		glDrawElements(GL_LINES, lineCube.getIndices().length, GL_UNSIGNED_INT, 0);
	}

	private void drawCube(Matrix4f modelMat) {
		uniforms.setUniform("centerPos", modelMat);
		glDrawElements(GL_LINES, lineCube.getIndices().length, GL_UNSIGNED_INT, 0);
	}
}
