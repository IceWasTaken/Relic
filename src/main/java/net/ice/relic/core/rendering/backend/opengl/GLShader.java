package net.ice.relic.core.rendering.backend.opengl;

import net.ice.relic.core.rendering.shader.ShaderType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.lwjgl.opengl.GL43.*;

public class GLShader {

    private final int shaderID;
    private final ShaderType type;

    private GLShader(ShaderType type) {
        this.type = type;

        this.shaderID = glCreateShader(type.getGlType());
    }

    private static GLShader createShader(ShaderType type, String source) {
        GLShader shader = new GLShader(type);
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

    public static GLShader loadShader(String fileName, ShaderType type, boolean postShader) {
        StringBuilder shaderSource = new StringBuilder();

        try(InputStream stream = GLShader.class.getResourceAsStream("/relic/data/rendering/gl/shaders/" + fileName)) {
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

    private void validateShader(int shaderId) {
        int status = glGetShaderi(shaderId, GL_COMPILE_STATUS);
        if (status == GL_FALSE) {
            String log = glGetShaderInfoLog(shaderId);
            throw new RuntimeException("Shader compilation failed:\n" + log);
        }
    }

    public int getShaderID() {
        return shaderID;
    }

    public ShaderType getType() {
        return type;
    }


}