package net.ice.relic.core.rendering.shader;

import net.ice.relic.core.resource.Resource;
import org.lwjgl.util.shaderc.Shaderc;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ShaderCompiler {

    public static byte[] compileShader(Resource shaderCode, int shaderType) {
        ByteBuffer data = shaderCode.load();

        long compiler = 0;
        long options = 0;
        byte[] compiledShader;

        try {
            compiler = Shaderc.shaderc_compiler_initialize();
            options = Shaderc.shaderc_compile_options_initialize();
//            if (EngCfg.getInstance().isDebugShaders()) {
//                Shaderc.shaderc_compile_options_set_generate_debug_info(options);
//                Shaderc.shaderc_compile_options_set_optimization_level(options, 0);
//                Shaderc.shaderc_compile_options_set_source_language(options, Shaderc.shaderc_source_language_glsl);
//            }
            long result = Shaderc.shaderc_compile_into_spv(
                    compiler,
                    StandardCharsets.UTF_8.decode(data),
                    shaderType,
                    "shader.glsl",
                    "main",
                    options
            );

            if (Shaderc.shaderc_result_get_compilation_status(result) != Shaderc.shaderc_compilation_status_success) {
                throw new RuntimeException("Shader compilation failed: " + Shaderc.shaderc_result_get_error_message(result));
            }

            ByteBuffer buffer = Shaderc.shaderc_result_get_bytes(result);
            compiledShader = new byte[buffer.remaining()];
            buffer.get(compiledShader);
        } finally {
            Shaderc.shaderc_compile_options_release(options);
            Shaderc.shaderc_compiler_release(compiler);
        }

        return compiledShader;
    }

    public static byte[] compileShader(ByteBuffer data, int shaderType) {
        long compiler = 0;
        long options = 0;
        byte[] compiledShader;

        try {
            compiler = Shaderc.shaderc_compiler_initialize();
            options = Shaderc.shaderc_compile_options_initialize();
//            if (EngCfg.getInstance().isDebugShaders()) {
//                Shaderc.shaderc_compile_options_set_generate_debug_info(options);
//                Shaderc.shaderc_compile_options_set_optimization_level(options, 0);
//                Shaderc.shaderc_compile_options_set_source_language(options, Shaderc.shaderc_source_language_glsl);
//            }
            long result = Shaderc.shaderc_compile_into_spv(
                    compiler,
                    StandardCharsets.UTF_8.decode(data),
                    shaderType,
                    "shader.glsl",
                    "main",
                    options
            );

            if (Shaderc.shaderc_result_get_compilation_status(result) != Shaderc.shaderc_compilation_status_success) {
                throw new RuntimeException("Shader compilation failed: " + Shaderc.shaderc_result_get_error_message(result));
            }

            ByteBuffer buffer = Shaderc.shaderc_result_get_bytes(result);
            compiledShader = new byte[buffer.remaining()];
            buffer.get(compiledShader);
        } finally {
            Shaderc.shaderc_compile_options_release(options);
            Shaderc.shaderc_compiler_release(compiler);
        }

        return compiledShader;
    }

    public static byte[] compileShader(String shaderCode, int shaderType) {
        long compiler = 0;
        long options = 0;
        byte[] compiledShader;

        try {
            compiler = Shaderc.shaderc_compiler_initialize();
            options = Shaderc.shaderc_compile_options_initialize();
//            if (EngCfg.getInstance().isDebugShaders()) {
//                Shaderc.shaderc_compile_options_set_generate_debug_info(options);
//                Shaderc.shaderc_compile_options_set_optimization_level(options, 0);
//                Shaderc.shaderc_compile_options_set_source_language(options, Shaderc.shaderc_source_language_glsl);
//            }
            long result = Shaderc.shaderc_compile_into_spv(
                    compiler,
                    shaderCode,
                    shaderType,
                    "shader.glsl",
                    "main",
                    options
            );

            if (Shaderc.shaderc_result_get_compilation_status(result) != Shaderc.shaderc_compilation_status_success) {
                throw new RuntimeException("Shader compilation failed: " + Shaderc.shaderc_result_get_error_message(result));
            }

            ByteBuffer buffer = Shaderc.shaderc_result_get_bytes(result);
            compiledShader = new byte[buffer.remaining()];
            buffer.get(compiledShader);
        } finally {
            Shaderc.shaderc_compile_options_release(options);
            Shaderc.shaderc_compiler_release(compiler);
        }

        return compiledShader;
    }

    public static void compileShaderIfChanged(Resource glsShaderFile, Resource spvLocation, int shaderType) {
        byte[] compiledShader;
        try {
            File glslFile = glsShaderFile.getAsFile();
            File spvFile = new File(spvLocation.getPath());
            if (!spvFile.exists() || glslFile.lastModified() > spvFile.lastModified()) {

                Logger.info("Compiling [{}] to [{}]", glslFile.getPath(), spvFile.getPath());
                String shaderCode = new String(Files.readAllBytes(glslFile.toPath()));

                compiledShader = compileShader(shaderCode, shaderType);
                Files.write(spvFile.toPath(), compiledShader);
            } else {
                Logger.debug("Shader [{}] already compiled. Loading compiled version: [{}]", glslFile.getPath(), spvFile.getPath());
            }
        } catch (IOException excp) {
            throw new RuntimeException(excp);
        }
    }

}
