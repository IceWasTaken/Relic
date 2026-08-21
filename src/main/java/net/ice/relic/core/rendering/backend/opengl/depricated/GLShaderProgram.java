package net.ice.relic.core.rendering.backend.opengl.depricated;

import net.ice.relic.core.rendering.backend.opengl.Uniforms;
import net.ice.curio.graphics.object.pipeline.shader.ShaderType;
import org.tinylog.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL20.*;

public class GLShaderProgram {

    private final int programID;
    private final Uniforms uniforms;

    public GLShaderProgram(String programDirPath) {
        this.programID = glCreateProgram();
        this.uniforms = new Uniforms(programID);

        if(programID == 0) {
            throw new RuntimeException("Error while creating new shader shaderProgram. \nMost recent OpenGL error: " + glGetError());
        }

        Path dir = Paths.get("resources/relic/data/rendering/gl/shaders/" + programDirPath + "/");
        List<GLShader> shaders = new ArrayList<>();
        try(Stream<Path> stream = Files.list(dir)) {
            stream.forEach((path -> {
                ShaderType type = getShaderTypeFromFileExtension(path.toFile().getName());
                shaders.add(new GLShader(type).load(
                        programDirPath + "/" + path.toFile().getName(),
                        type
                ));
            }));
        } catch (Exception ioException) {
            Logger.error("[GLShaderProgram]: Error loading shader program: {}", ioException);
        }

        attach(shaders);
    }

    public GLShaderProgram attach(List<GLShader> shaders) {
        for (GLShader shader : shaders) {
            if(shader.getHandle() == 0) {
                throw new RuntimeException("Shader Invalid.");
            }

            glAttachShader(programID, shader.getHandle());
        }

        glLinkProgram(programID);
        validateLink(programID);
        glValidateProgram(programID);


        shaders.forEach(shader -> glDetachShader(programID, shader.getHandle()));
        shaders.forEach(shader -> glDeleteShader(shader.getHandle()));

        return this;
    }

    private void validateLink(int program) {
        if(glGetProgrami(program, GL_VALIDATE_STATUS) == GL_FALSE) {
            String log = glGetProgramInfoLog(programID);
            throw new RuntimeException("Program unable to run: \n" + log);
        }
    }

    private ShaderType getShaderTypeFromFileExtension(String fileName) {
        if(fileName.endsWith("vert")) {
            return ShaderType.VERTEX;
        } else if(fileName.endsWith("geom")) {
            return ShaderType.GEOMETRY;
        } else if(fileName.endsWith("frag")) {
            return ShaderType.FRAGMENT;
        } else if(fileName.endsWith("comp")) {
            return ShaderType.COMPUTE;
        }

        throw new RuntimeException("gfdghfdshsdfg");
    }

    private String fileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if(lastIndex == -1) {
            return "";
        }
        return fileName.substring(lastIndex + 1);
    }

    public void bind() {
        if(programID != 0) {
            glUseProgram(programID);
        } else {
            throw new RuntimeException();
        }
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        glDeleteProgram(programID);
    }

    public int getProgramID() {
        return programID;
    }

    public Uniforms getUniforms() {
        return uniforms;
    }
}
