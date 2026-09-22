package net.ice.curio.library.shaderc;

import net.ice.curio.Curio;
import net.ice.curio.graphics.context.GraphicsContext;
import org.lwjgl.util.shaderc.Shaderc;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.function.BiPredicate;

public class ShaderCompiler {

	public static final BiPredicate<File, File> IF_CHANGED = (glsl, spv) -> !spv.exists() || glsl.lastModified() > spv.lastModified();

	private final long compilerHandle;
	private final long options;

	public ShaderCompiler(Curio curio) {
		compilerHandle = Shaderc.shaderc_compiler_initialize();
		options =  Shaderc.shaderc_compile_options_initialize();

		if(curio.getApplicationProperties().debug()) {
			Shaderc.shaderc_compile_options_set_generate_debug_info(options);
			Shaderc.shaderc_compile_options_set_optimization_level(options, 0);
			Shaderc.shaderc_compile_options_set_source_language(options, Shaderc.shaderc_source_language_glsl);
		}
	}

	public ShaderCompiler(GraphicsContext ctx) {
		this(ctx.getCurio());
	}

	public byte[] compile(String code, int type) {
		long result = Shaderc.shaderc_compile_into_spv(
				compilerHandle,
				code,
				type,
				"shader.glsl",
				"main",
				options
		);

		if(Shaderc.shaderc_result_get_compilation_status(result) != Shaderc.shaderc_compilation_status_success) {
			throw new RuntimeException("[ShaderCompiler]: Shader compilation failed: " + Shaderc.shaderc_result_get_error_message(result));
		}

		ByteBuffer buffer = Shaderc.shaderc_result_get_bytes(result);
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		return bytes;
	}

	public ShaderCompiler compileIf(String glslFilePath, int type, BiPredicate<File, File> condition) {
		try {
			File glslFile = new File(glslFilePath);
			File sprvFile = new File(glslFilePath + ".spv");

			if(condition.test(glslFile, sprvFile)) {
				Logger.debug("[ShaderCompiler]: Compiling " + glslFile.getAbsolutePath());

				String shaderCode = new String(Files.readAllBytes(glslFile.toPath()));
				Files.write(sprvFile.toPath(), compile(shaderCode, type));
			} else {
				Logger.debug("[ShaderCompiler]: Using precompiled shader " + glslFile.getAbsolutePath());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return this;
	}

	public void setTargetEnvironment(int env, int version) {
		Shaderc.shaderc_compile_options_set_target_env(options, env, version);
	}

	public void cleanup() {
		Shaderc.shaderc_compile_options_release(options);
		Shaderc.shaderc_compiler_release(compilerHandle);
	}
}
