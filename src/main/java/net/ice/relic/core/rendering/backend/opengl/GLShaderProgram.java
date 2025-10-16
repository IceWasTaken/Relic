package net.ice.relic.core.rendering.backend.opengl;

import net.ice.relic.core.rendering.shader.IShader;
import net.ice.relic.core.rendering.shader.IShaderProgram;

import java.util.List;

import static net.ice.relic.core.rendering.backend.opengl.GLUtil.assertNoError;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL20.*;

public class GLShaderProgram implements IShaderProgram {

    private final int programID;

    public GLShaderProgram() {
        this.programID = glCreateProgram();

        if(programID == 0) {
            throw new RuntimeException("Error while creating new shader shaderProgram. \nMost recent OpenGL error: " + glGetError());
        }
    }

    @Override
    public GLShaderProgram attach(List<IShader> shaders) {
        for (IShader shader : shaders) {
            if(shader.getHandle() == 0) {
                throw new RuntimeException("Shader Invalid.");
            }

            glAttachShader(programID, (int) shader.getHandle());
        }

        glLinkProgram(programID);
        glValidateProgram(programID);
        validateLink(programID);

        shaders.forEach(shader -> glDetachShader(programID, (int) shader.getHandle()));
        shaders.forEach(shader -> glDeleteShader((int) shader.getHandle()));

        return this;
    }

    private void validateLink(int program) {
        if(glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            int infoLogLength = glGetProgrami(program, GL_INFO_LOG_LENGTH);
            String infoLog = glGetProgramInfoLog(program, infoLogLength);
            System.err.println("Error linking shader program: " + infoLog);
        }
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


}
