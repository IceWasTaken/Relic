package net.ice.curio.library.opengl.object.glsl;

import org.joml.*;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.ARBBindlessTexture.glProgramUniformHandleui64ARB;
import static org.lwjgl.opengl.GL46.*;

public class Uniforms {

	private final int handle;

	public Uniforms(GLShaderProgram program) {
		this.handle = program.getHandle();
	}

	public void uniform(int location, int value) {
		glProgramUniform1i(handle, location, value);
	}

	public void uniform(int location, long value) {
		glProgramUniformHandleui64ARB(handle, location, value);
	}

	public void uniform(int location, float value) {
		glProgramUniform1f(handle, location, value);
	}

	public void uniform(int location, double value) {
		glProgramUniform1d(handle, location, value);
	}

	public void uniform(int location, Vector2i value) {
		glProgramUniform2i(handle, location, value.x, value.y);
	}

	public void uniform(int location, Vector2f value) {
		glProgramUniform2f(handle, location, value.x, value.y);
	}

	public void uniform(int location, Vector3i value) {
		glProgramUniform3i(handle, location, value.x, value.y, value.z);
	}

	public void uniform(int location, Vector3f value) {
		glProgramUniform3f(handle, location, value.x, value.y, value.z);
	}

	public void uniform(int location, Vector4i value) {
		glProgramUniform4i(handle, location, value.x, value.y, value.z, value.w);
	}

	public void uniform(int location, Vector4f value) {
		glProgramUniform4f(handle, location, value.x, value.y, value.z, value.w);
	}

	public void setUniform(int location, Matrix4f value) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			glProgramUniformMatrix4fv(handle, location, false, value.get(stack.mallocFloat(16)));
		}
	}




}
