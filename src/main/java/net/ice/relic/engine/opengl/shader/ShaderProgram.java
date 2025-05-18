package net.ice.relic.engine.opengl.shader;

import java.util.List;

import static net.ice.relic.engine.util.ShaderUtil.*;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {

    private final int programID;

    public ShaderProgram(List<Shader> shaders) {
        this.programID = glCreateProgram();

        if(programID == 0) {
            throw new RuntimeException("Error while creating new shader program. \nMost recent OpenGL error: " + glGetError());
        }

        shaders.forEach(s -> validateShader(s.getShaderID(), s.getFileName()));
        shaders.forEach(shader -> glAttachShader(programID, shader.getShaderID()));

        glLinkProgram(programID);
        validateLink(programID);

        shaders.forEach(shader -> glDetachShader(programID, shader.getShaderID()));
        shaders.forEach(shader -> glDeleteShader(shader.getShaderID()));
    }

    public void bind() {
        glUseProgram(programID);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        glDeleteProgram(programID);
    }
}
