package net.ice.relic.engine.opengl;

import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.ARBBindlessTexture.glUniformHandleui64ARB;
import static org.lwjgl.opengl.GL20.*;

public class UniformBufferObject {

    private ShaderProgram shaderProgram;
    private Map<String, Integer> uniformMap;

    public UniformBufferObject(ShaderProgram shaderProgram) {
        this.shaderProgram = shaderProgram;
        this.uniformMap = new HashMap<>();
    }

    public void createUniform(String uniformName) {
        int uniformLocation = glGetUniformLocation(shaderProgram.getProgramID(), uniformName);

        if(uniformLocation < 0) {
            throw new RuntimeException("Could not find uniform: " + uniformName + " in shader.");
        }

        uniformMap.put(uniformName, uniformLocation);
    }

    public void createUniformUnsafe(String uniformName) {
        int uniformLocation = glGetUniformLocation(shaderProgram.getProgramID(), uniformName);
        uniformMap.put(uniformName, uniformLocation);
    }


    public void createUniform(String uniformName, int index) {
        int uniformLocation = glGetUniformLocation(shaderProgram.getProgramID(), formatUniform(uniformName, index));

        if(uniformLocation < 0) {
            throw new RuntimeException("Could not find uniform in shader.");
        }

        uniformMap.put(uniformName, uniformLocation);
    }

    private int getUniformLocation(String uniformName) {
        Integer location = uniformMap.get(uniformName);
        if (location == null) {
            throw new RuntimeException("Could not find: " + uniformName + " in map.");
        }
        return location;
    }

    public void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(getUniformLocation(uniformName), false, value.get(stack.mallocFloat(16)));
        }
    }

    public void setUniform(String uniformName, float value) {
        glUniform1f(getUniformLocation(uniformName), value);
    }

    public void setUniform(String uniformName, int value) {
        glUniform1i(getUniformLocation(uniformName), value);
    }

    public void setUniform(String uniformName, Vector3f value) {
        glUniform3f(getUniformLocation(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, Vector2i value) {
        glUniform2i(getUniformLocation(uniformName), value.x, value.y);
    }

    public void setUniform(String uniformName, Vector4f value) {
        glUniform4f(getUniformLocation(uniformName), value.x, value.y, value.z, value.w);
    }

    public void setUniform(String uniformName, Vector2f value) {
        glUniform2f(getUniformLocation(uniformName), value.x, value.y);
    }

    public void setUniform(String uniformName, long value) {
        glUniformHandleui64ARB(getUniformLocation(uniformName), value);
    }



    public String formatUniform(String uniformName, int index) {
        return uniformName + "[" + index + "]";
    }
}
