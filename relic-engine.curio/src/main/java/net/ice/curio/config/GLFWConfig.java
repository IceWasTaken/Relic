package net.ice.curio.config;

import net.ice.heirloom.config.ConfigBase;

@Deprecated
public class GLFWConfig extends ConfigBase {

    private static final String fileName = "glfwConfig.properties";

    private static GLFWConfig instance;

    private static boolean glfwUseX11 = false;

    public GLFWConfig() {
        super(fileName);
    }

    public static GLFWConfig getInstance() {
        return instance == null ? instance = new GLFWConfig() : instance;
    }

    @Override
    public void loadConfig() {
        glfwUseX11 = getBoolean("glfw_use_x11");
    }

    public static boolean shouldGlfwUseX11() {
        return glfwUseX11;
    }

    public static void setGlfwUseX11(boolean value) {
        glfwUseX11 = value;
    }



}
