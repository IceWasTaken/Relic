package net.ice.curio.library.opengl.object.glsl;

import net.ice.curio.graphics.enums.ShaderStage;
import net.ice.curio.graphics.shader.Shader;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_CONTROL_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_EVALUATION_SHADER;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER;

public final class GLShader extends Shader {

	private final int handle;

	public GLShader(ShaderStage stage, String fileName) {
		super(stage);

		this.handle = glCreateShader(getShaderType(stage));
		glShaderSource(handle, loadFile(fileName));
		glCompileShader(handle);
		validateShader(handle);
	}

	int getHandle() {
		return handle;
	}

	@Override
	public String getFilePrefix() {
		return "/resources/relic/data/rendering/gl/shaders/";
	}

	private void validateShader(int shaderId) {
		if(glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
			String log = glGetShaderInfoLog(shaderId);
			throw new RuntimeException("Shader compilation failed:\n" + log);
		}
	}

	private int getShaderType(ShaderStage stage) {
		return switch(stage) {
			case VERTEX -> GL_VERTEX_SHADER;
			case TESSELLATION_CONTROL -> GL_TESS_CONTROL_SHADER;
			case TESSELLATION_EVALUATION -> GL_TESS_EVALUATION_SHADER;
			case GEOMETRY -> GL_GEOMETRY_SHADER;
			case FRAGMENT -> GL_FRAGMENT_SHADER;
			case COMPUTE -> GL_COMPUTE_SHADER;
		};
	}
}
