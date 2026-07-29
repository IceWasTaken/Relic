package net.ice.relic.core.rendering.backend.opengl.depricated;

import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.rendering.shader.ShaderType;
import org.tinylog.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.lwjgl.opengl.GL43.*;

public class GLShader implements Lifecycle {

    private final int shaderID;
    private final ShaderType type;

    public GLShader(ShaderType type) {
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

    public GLShader load(String fileName, ShaderType type) {
        StringBuilder shaderSource = new StringBuilder();
        fileName = "resources/relic/data/rendering/gl/shaders/" + fileName;

        Logger.info("[GLShader]: Loading shader: {}", fileName);
        try(InputStream stream = new FileInputStream(fileName)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    shaderSource.append(line).append("\n");
                    }
            }
        } catch (Exception exception) {
            Logger.error("[GLShader]: Error while loading shader: {}", exception);
        }

        return createShader(type, shaderSource.toString());
    }

    private void validateShader(int shaderId) {
        if(glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            String log = glGetShaderInfoLog(shaderId);
            throw new RuntimeException("Shader compilation failed:\n" + log);
        }
    }

    @Override
    public void cleanup() {
        glDeleteShader(shaderID);
    }

    public int getHandle() {
        return shaderID;
    }

    public ShaderType getType() {
        return type;
    }
}