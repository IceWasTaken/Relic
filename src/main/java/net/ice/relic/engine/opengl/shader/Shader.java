package net.ice.relic.engine.opengl.shader;

import static net.ice.relic.engine.util.IOUtil.readShaderFile;
import static net.ice.relic.engine.util.ShaderUtil.validateShader;
import static org.lwjgl.opengl.GL43.*;

public class Shader {

    private final int shaderID;
    private int type;
    private String fileName;

    public Shader(String file, int type) {
        this.shaderID = glCreateShader(type);
        this.type = type;
        this.fileName = file;

        if(shaderID == 0) {
            throw new RuntimeException("Error creating shader.");
        }

        glShaderSource(shaderID, readShaderFile(file));
        glCompileShader(shaderID);

        validateShader(shaderID, file);
    }

    public int getShaderID() {
        return shaderID;
    }

    public int getType() {
        return type;
    }

    public String getFileName() {
        return fileName;
    }
}

