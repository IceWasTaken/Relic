package net.ice.relic.engine.opengl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.lwjgl.opengl.GL43.*;

public class Shader {

    private final int shaderID;
    private final ShaderType type;

    private Shader(ShaderType type) {
        this.type = type;

        this.shaderID = glCreateShader(type.getGlType());
    }

    private static Shader createShader(ShaderType type, String source) {
        Shader shader = new Shader(type);
        shader.setSource(source);
        shader.compile();
        return shader;
    }

    private void setSource(String source) {
        glShaderSource(shaderID, source);
    }

    private void compile() {
        glCompileShader(shaderID);
        validateShader(shaderID);
    }

    public static Shader loadShader(String fileName, ShaderType type) {
        StringBuilder shaderSource = new StringBuilder();

        try(InputStream stream = Shader.class.getResourceAsStream("/net/ice/relic/shaders/" + fileName)) {
            if(stream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        shaderSource.append(line).append("\n");
                    }
                }
            }
        } catch (IOException exception) {
            throw new RuntimeException("Unable to load shader (or shader not found): " + fileName, exception);
        }

        return createShader(type, shaderSource.toString());
    }

    private void validateShader(int shader) {
        if(glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Error while compiling " + (type == ShaderType.VERTEX ? "vertex" : "fragment") + " shader." + "\n\n" + glGetShaderInfoLog(shader));
        }
    }

    public int getShaderID() {
        return shaderID;
    }

    public ShaderType getType() {
        return type;
    }

    public enum ShaderType {

        VERTEX(GL_VERTEX_SHADER),
        FRAGMENT(GL_FRAGMENT_SHADER),
        GEOMETRY(GL_GEOMETRY_SHADER),
        COMPUTE(GL_COMPUTE_SHADER),;

        private final int glType;

        ShaderType(int glType) {
            this.glType = glType;
        }

        public int getGlType() {
            return glType;
        }
    }
}

