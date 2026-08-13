package net.ice.curio.graphics.object.pipeline.shader;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Shader<T> {

	//protected final T handle;

	protected abstract T createShader();
	protected abstract String getShadersDirPrefix();

	protected Shader() {

	}

	public static Shader[] readShaderProgram(String path) {
		Path dir = Paths.get("" + "/");
		return null;
	}
}
