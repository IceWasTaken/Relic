package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.library.opengl.object.VertexArrayObject;
import net.ice.curio.library.opengl.object.buffer.IndexBufferObject;
import net.ice.curio.library.opengl.object.buffer.VertexBufferObject;
import net.ice.curio.library.opengl.wrapper.enums.Usage;
import net.ice.heirloom.Lifecycle;
import net.ice.heirloom.color.RGBColor;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShader;
import net.ice.relic.core.rendering.backend.opengl.depricated.GLShaderProgram;
import net.ice.relic.core.rendering.backend.opengl.depricated.buffer.UniformBufferObject;
import net.ice.relic.core.rendering.shader.ShaderType;
import net.ice.relic.core.scene.Scene;
import net.ice.relic.core.scene.light.Light;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class GLDebugRenderer implements Lifecycle {

	private GLShaderProgram shaderProgram;
	private UniformBufferObject uniforms;

	private VertexBufferObject vbo;
	private VertexArrayObject vao;
	private IndexBufferObject indexBufferObject;

	private static RGBColor notVisibleColor = new RGBColor(78, 59, 255);
	private static RGBColor visibleColor = notVisibleColor.lighter();

	private final GLRenderer glRenderer;

	private final float[] vertices = new float[] {
			// Front
			-1,-1, 1,
			1,-1, 1,
			1, 1, 1,
			-1, 1, 1,

			// Back
			-1,-1,-1,
			1,-1,-1,
			1, 1,-1,
			-1, 1,-1
	};

	private final int[] indices = new int[] {
			// Front square
			0,1, 1,2, 2,3, 3,0,

			// Back square
			4,5, 5,6, 6,7, 7,4,

			// Connecting edges
			0,4, 1,5, 2,6, 3,7
	};

	public GLDebugRenderer(GLRenderer glRenderer) {
		this.glRenderer = glRenderer;
	}

	@Override
	public void init() {
		this.vao = new VertexArrayObject();
		this.vbo = new VertexBufferObject();
		this.indexBufferObject = new IndexBufferObject();

		vao.bind();

		FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(vertices.length);
		floatBuffer.put(vertices);
		floatBuffer.flip();

		vbo.bind();
		vbo.bufferData(floatBuffer, Usage.STATIC_DRAW);

		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

		IntBuffer indicesBuffer = MemoryUtil.memCallocInt(indices.length);
		indicesBuffer.put(indices);
		indicesBuffer.flip();
		indexBufferObject.bind();
		indexBufferObject.bufferData(indicesBuffer, Usage.STATIC_DRAW);

		vao.unbind();

		this.shaderProgram = new GLShaderProgram().attach(List.of(
				new GLShader(ShaderType.VERTEX).load("debug.vert", ShaderType.VERTEX, false),
				new GLShader(ShaderType.FRAGMENT).load("debug.frag", ShaderType.FRAGMENT, false)
		));

		this.uniforms = new UniformBufferObject(shaderProgram);

		uniforms.createUniform("color");
		uniforms.createUniform("centerPos");
		uniforms.createUniform("viewMatrix");
		uniforms.createUniform("projectionMatrix");
	}

	@Override
	public void render() {
		try(GLShaderProgram program = new GLShaderProgram(shaderProgram)) {
			Scene scene = glRenderer.getApplication().getCurrentScene();

			uniforms.setUniform("viewMatrix", scene.getCamera().getViewMatrix());
			uniforms.setUniform("projectionMatrix", scene.getMatrix().getProjMatrix());

			vao.bind();

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


			vao.unbind();
		}
	}


	private void drawCube(Vector3f position) {
		uniforms.setUniform("centerPos", new Matrix4f().identity().translate(position));
		glDrawElements(GL_LINES, 24, GL_UNSIGNED_INT, 0);
	}
}
