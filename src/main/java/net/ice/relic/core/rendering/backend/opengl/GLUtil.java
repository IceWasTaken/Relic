package net.ice.relic.core.rendering.backend.opengl;

import org.tinylog.Logger;

import static org.lwjgl.opengl.GL11.*;

public class GLUtil {

    public static void assertNoError() {
        int error = glGetError();
        if(error != GL_NO_ERROR) {
            String errorCode = switch(error) {
                case GL_INVALID_ENUM -> "GL_INVALID_ENUM";
                case GL_INVALID_VALUE -> "GL_INVALID_VALUE";
                case GL_INVALID_OPERATION -> "GL_INVALID_OPERATION";
                default -> "Error Code Not Mapped";
            };
            Logger.error("OpenGL Error: " + errorCode + " [" + error + "]");
        }
    }

}
