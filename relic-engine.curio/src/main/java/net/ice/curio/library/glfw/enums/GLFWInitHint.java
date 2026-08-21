package net.ice.curio.library.glfw.enums;

import static org.lwjgl.glfw.GLFW.*;

public enum GLFWInitHint {

    PLATFORM(GLFW_PLATFORM),
    ANGLE_PLATFORM_TYPE(GLFW_ANGLE_PLATFORM_TYPE),

    JOYSTICK_HAT_BUTTONS(GLFW_JOYSTICK_HAT_BUTTONS),

    COCOA_CHDIR_RESOURCES(GLFW_COCOA_CHDIR_RESOURCES),
    COCOA_MENUBAR(GLFW_COCOA_MENUBAR),

    WAYLAND_LIBDECOR(GLFW_WAYLAND_LIBDECOR),

    X11_XCB_VULKAN_SURFACE(GLFW_X11_XCB_VULKAN_SURFACE);

    private final int glfwEnum;

    GLFWInitHint(int glfwEnum) {
        this.glfwEnum = glfwEnum;
    }

    public int getGlfwEnum() {
        return glfwEnum;
    }
}
