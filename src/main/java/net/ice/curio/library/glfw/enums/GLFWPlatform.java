package net.ice.curio.library.glfw.enums;

import static org.lwjgl.glfw.GLFW.*;

public enum GLFWPlatform {

    ANY(GLFW_ANY_PLATFORM),
    WIN32(GLFW_PLATFORM_WIN32),
    COCOA(GLFW_PLATFORM_COCOA),
    WAYLAND(GLFW_PLATFORM_WAYLAND),
    X11(GLFW_PLATFORM_X11),
    NULL(GLFW_PLATFORM_NULL);

    private final int glfwEnum;

    GLFWPlatform(int glfwEnum) {
        this.glfwEnum = glfwEnum;
    }

    public int getGLFWEnum() {
        return glfwEnum;
    }
}
