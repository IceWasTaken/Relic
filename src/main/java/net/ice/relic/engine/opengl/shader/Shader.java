package net.ice.relic.engine.opengl.shader;

import static net.ice.relic.engine.util.IOUtil.readShaderFile;
import static net.ice.relic.engine.util.ShaderUtil.validateShader;
import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private final int shader;

    public Shader(int type) {
        this.shader = glCreateShader(type);
    }

    public Shader loadShader(String fileName, ShaderAssembler assembler) {
        String baseSource = readShaderFile(fileName);

        String finalSource = assembler.assemble(baseSource);

        glShaderSource(shader, finalSource);
        glCompileShader(shader);
        validateShader(shader, fileName);

        return this;
    }

    public int getShader() {
        return shader;
    }
}
