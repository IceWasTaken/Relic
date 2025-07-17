package net.ice.relic.engine.opengl;

import java.util.List;

import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {

    private final int programID;

    public ShaderProgram(List<Shader> shaders) {
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
        int status = glGetProgrami(program, GL_LINK_STATUS);
        if (status == GL_FALSE) {
            String log = glGetProgramInfoLog(program);
            throw new RuntimeException("Shader program linking failed:\n" + log);
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
