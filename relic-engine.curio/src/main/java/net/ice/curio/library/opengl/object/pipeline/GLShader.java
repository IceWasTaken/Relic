package net.ice.curio.library.opengl.object.pipeline;

import net.ice.curio.graphics.context.GraphicsContext;
import net.ice.curio.graphics.object.pipeline.shader.Shader;
import net.ice.curio.graphics.object.pipeline.shader.ShaderType;
import org.tinylog.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER;

public class GLShader extends Shader {

	private final int shaderHandle;

	private GLShader(GraphicsContext context, String path, ShaderType type) {
		super(context, type);

		this.shaderHandle = glCreateShader(getTypeEnum());

		glShaderSource(shaderHandle, loadFile(path));
		glCompileShader(getTypeEnum());
	}

	public static List<GLShader> loadProgram(GraphicsContext context, String programPath) {
		Path dir = Paths.get("resources/relic/data/rendering/gl/shaders/" + programPath + "/");
		List<GLShader> shaderList = new ArrayList<>();
		try(Stream<Path> stream = Files.list(dir)) {
			stream.forEach((path -> {
				ShaderType type = getShaderTypeFromFileExtension(path.toFile().getName());
				shaderList.add(new GLShader(
						context,
						programPath + "/" + path.toFile().getName(),
						type
				));
			}));
		} catch(IOException e) {
			Logger.error("[GLShader]: Failed to read shader program");
		}
		return shaderList;
	}

	private String loadFile(String path) {
		StringBuilder source = new StringBuilder();
		path = "resources/relic/data/rendering/gl/shaders/" + path;

		Logger.info("[GLShader]: Loading shader: {}", path);
		try(InputStream stream = new FileInputStream(path)) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
				String line;
				while ((line = reader.readLine()) != null) {
					source.append(line).append("\n");
				}
			}
		} catch (Exception exception) {
			Logger.error("[GLShader]: Error while loading shader: {}", exception);
		}

		return source.toString();
	}

	private int getTypeEnum() {
		return switch(type) {
			case VERTEX -> GL_VERTEX_SHADER;
			case FRAGMENT -> GL_FRAGMENT_SHADER;
			case GEOMETRY -> GL_GEOMETRY_SHADER;
			case COMPUTE -> GL_COMPUTE_SHADER;
		};
	}

	public int getShaderHandle() {
		return shaderHandle;
	}
}
