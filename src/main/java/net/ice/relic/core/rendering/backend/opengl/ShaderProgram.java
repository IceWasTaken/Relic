package net.ice.relic.core.rendering.backend.opengl;

import java.util.List;

import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {

    private final int programID;

    public ShaderProgram(List<GLShader> shaders) {
        this.programID = glCreateProgram();

        if(programID == 0) {
            throw new RuntimeException("Error while creating new shader shaderProgram. \nMost recent OpenGL error: " + glGetError());
        }

        shaders.forEach(shader -> glAttachShader(programID, shader.getShaderID()));

        glLinkProgram(programID);
        glValidateProgram(programID);
        validateLink(programID);

        shaders.forEach(shader -> glDetachShader(programID, shader.getShaderID()));
        shaders.forEach(shader -> glDeleteShader(shader.getShaderID()));
    }

    private void validateLink(int program) {
        if(glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            int infoLogLength = glGetProgrami(program, GL_INFO_LOG_LENGTH);
            String infoLog = glGetProgramInfoLog(program, infoLogLength);
            System.err.println("Error linking shader program: " + infoLog);
        }
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

    public int getProgramID() {
        return programID;
    }
}
