package net.ice.relic.engine.util;

import org.tinylog.Logger;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class ShaderUtil {

    public static void validateShader(int shader, String fileName) {
        Logger.debug("Attempting to compile shader: " + fileName);
        if(glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Error while compiling shader: " + fileName + "\n" + glGetShaderInfoLog(shader));
        }
        Logger.debug("Successfully compiled shader: " + fileName);
    }


    public static void validateLink(int program) {
        if(glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            System.out.println(glGetError());
            throw new RuntimeException("Error while linking program.\n" + glGetProgramInfoLog(program));
        }
        Logger.debug("Successfully linked program: ");
    }
}
