package net.ice.relic.engine.opengl.shader;

import net.ice.relic.engine.util.ShaderUtil;
import org.lwjgl.opengl.GL20;
import org.tinylog.Logger;

import java.util.List;

import static net.ice.relic.engine.util.ShaderUtil.validateLink;
import static org.lwjgl.opengl.GL20.*;

@Deprecated
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

        String vertexCode = injectVertexModules(vertexBase);
        String fragmentCode = injectFragmentModules(fragmentBase);

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

        glCompileShader(shader);
        ShaderUtil.validateShader(shader, (type == GL_VERTEX_SHADER ? "Vertex" : "Fragment"));
        return shader;
    }

    private String injectVertexModules(String base) {
        StringBuilder injected = new StringBuilder("#version 330 core\n");
        for (ShaderModule module : modules) {
            String code = module.getVertexCode();
            if (!code.isEmpty()) {
                injected.append("// MODULE: ").append(module.getName()).append(" (Vertex)\n");
                injected.append(code).append("\n");
            }
        }
        injected.append(base);
        return injected.toString();
    }

    private String injectFragmentModules(String base) {
        StringBuilder injected = new StringBuilder("#version 330 core\n");
//        for (ShaderModule module : modules) {
//            String code = module.getFragmentCode();
//            if (!code.isEmpty()) {
//                injected.append("// MODULE: ").append(module.getName()).append(" (Fragment)\n");
//                injected.append(code).append("\n");
//            }
//        }
        injected.append(base);
        return injected.toString();
    }
}