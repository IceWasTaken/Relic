package net.ice.relic.engine.opengl.shader;

import net.ice.relic.engine.util.ShaderUtil;
import org.lwjgl.opengl.GL20;
import org.tinylog.Logger;

import java.util.List;

import static net.ice.relic.engine.util.ShaderUtil.validateLink;
import static org.lwjgl.opengl.GL20.*;

public class ShaderBuilder {
    private final List<ShaderModule> modules;
    private final String vertexBase;
    private final String fragmentBase;

    public ShaderBuilder(String vertexBase, String fragmentBase, List<ShaderModule> modules) {
        this.vertexBase = vertexBase;
        this.fragmentBase = fragmentBase;
        this.modules = modules;
    }

    public int build() {
        int program = glCreateProgram();

        String vertexCode = injectModules(vertexBase);
        String fragmentCode = injectModules(fragmentBase);

        Logger.debug(fragmentCode);

        int vert = compileShader(vertexCode, GL_VERTEX_SHADER);
        int frag = compileShader(fragmentCode, GL_FRAGMENT_SHADER);

        glAttachShader(program, vert);
        glAttachShader(program, frag);

        glLinkProgram(program);
        validateLink(program);

        glDeleteShader(vert);
        glDeleteShader(frag);

        return program;
    }

    private int compileShader(String code, int type) {
        int shader = glCreateShader(type);
        glShaderSource(shader, code);
        Logger.debug(code);

        glCompileShader(shader);
        ShaderUtil.validateShader(shader, (type == GL_VERTEX_SHADER ? "Vertex" : "Fragment"));
        return shader;
    }

    private String injectModules(String base) {
        StringBuilder injected = new StringBuilder();
        injected.append("#version 330 core\n");
        for (ShaderModule module : modules) {
            injected.append("// MODULE: ").append(module.getName()).append("\n");
            injected.append(module.getCode()).append("\n");
        }
        injected.append(base);
        return injected.toString();
    }
}