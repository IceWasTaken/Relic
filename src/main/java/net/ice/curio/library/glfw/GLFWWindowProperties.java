package net.ice.curio.library.glfw;

import net.ice.curio.config.GLFWConfig;
import net.ice.curio.library.glfw.enums.GLFWPlatform;

import static net.ice.curio.system.SystemInfo.getOSType;

public class GLFWWindowProperties {

    private String title = "Untitled";
    private GLFWPlatform glfwPlatform;

    public GLFWWindowProperties() {
        this.glfwPlatform = getPlatform();
    }

    private GLFWPlatform getPlatform() {
        switch(getOSType()) {

            case WINDOWS -> {
                return GLFWPlatform.WIN32;
            }

            case MAC -> {
                return GLFWPlatform.COCOA;
            }

            case LINUX -> {
                if(!GLFWConfig.shouldGlfwUseX11()) {
                    return GLFWPlatform.WAYLAND;
                } else {
                    return GLFWPlatform.X11;
                }
            }

            case null, default -> {
                return GLFWPlatform.ANY;
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public GLFWPlatform getGlfwPlatform() {
        return glfwPlatform;
    }

}
