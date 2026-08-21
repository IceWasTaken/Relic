package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.graphics.enums.BufferAccess;
import net.ice.curio.graphics.enums.BufferFlags;
import net.ice.curio.library.opengl.object.VertexArrayObject;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.curio.library.opengl.wrapper.enums.Usage;
import net.ice.heirloom.Lifecycle;
import net.ice.heirloom.color.RGBColor;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.Uniforms;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;
import net.ice.relic.core.scene.Scene;
import net.ice.relic.core.scene.light.Light;
import net.ice.relic.core.scene.primitives.threed.LineCube;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.EnumSet;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.memFree;

public class GLDebugRenderer implements Lifecycle {

	private GLShaderProgram shaderProgram;
	private Uniforms uniforms;

	private VertexArrayObject vertexArrayObject;

	private GLBuffer vertexBuffer;
	private GLBuffer indexBuffer;

	private static RGBColor notVisibleColor = new RGBColor(78, 59, 255);
	private static RGBColor visibleColor = notVisibleColor.lighter();

	private final GLRenderer glRenderer;

	private final LineCube lineCube = new LineCube();

	public GLDebugRenderer(GLRenderer glRenderer) {
		this.glRenderer = glRenderer;
	}

	@Override
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

	@Override
	public void render() {
		shaderProgram.bind();
		Scene scene = glRenderer.getApplication().getCurrentScene();

		uniforms.setUniform("viewMatrix", scene.getCamera().getViewMatrix());
		uniforms.setUniform("projectionMatrix", scene.getMatrix().getProjMatrix());

		vertexArrayObject.bind();

		glDisable(GL_DEPTH_TEST);
		uniforms.setUniform("color", notVisibleColor.div().vec3f());
		for(Light light : scene.getLights()) {
			drawCube(light.getPosition());
		}



		glEnable(GL_DEPTH_TEST);
		uniforms.setUniform("color", visibleColor.div().vec3f());
		for(Light light : scene.getLights()) {
			drawCube(light.getPosition());
		}


		glBindVertexArray(0);
		shaderProgram.unbind();

	}


	private void drawCube(Vector3f position) {
		uniforms.setUniform("centerPos", new Matrix4f().identity().translate(position));
		glDrawElements(GL_LINES, lineCube.getIndices().length, GL_UNSIGNED_INT, 0);
	}
}
