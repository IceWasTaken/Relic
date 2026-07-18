package net.ice.curio.library.opengl.object.glsl;

import java.util.List;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDeleteShader;

public class GLShaderProgram {

	private final int handle;

	private final Uniforms uniforms;

	public GLShaderProgram(List<GLShader> shaders) {
		this.handle = glCreateProgram();
		this.uniforms = new Uniforms(this);

		if(handle == 0) {
			throw new RuntimeException("Error while creating new shader program.");
		}

		for (GLShader shader : shaders) {
			if(shader.getHandle() == 0) {
				throw new RuntimeException("Shader Invalid.");
			}

			glAttachShader(handle, shader.getHandle());
		}

		glLinkProgram(handle);
		validateLink(handle);
		glValidateProgram(handle);


		shaders.forEach(shader -> glDetachShader(handle, (int) shader.getHandle()));
		shaders.forEach(shader -> glDeleteShader((int) shader.getHandle()));
	}

	int getHandle() {
		return handle;
	}

	public Uniforms getUniforms() {
		return uniforms;
	}

	public void bind() {
		if(handle != 0) {
			glUseProgram(handle);
		} else {
			throw new RuntimeException();
		}
	}

	public void unbind() {
		glUseProgram(0);
	}

	public void cleanup() {
		unbind();
		glDeleteProgram(handle);
	}

	private void validateLink(int program) {
		if(glGetProgrami(program, GL_VALIDATE_STATUS) == GL_FALSE) {
			String log = glGetProgramInfoLog(program);
			throw new RuntimeException("Program unable to run: \n" + log);
		}
	}

}
