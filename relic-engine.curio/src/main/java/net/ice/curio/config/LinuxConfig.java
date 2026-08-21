package net.ice.curio.config;

import net.ice.heirloom.config.ConfigBase;

public class LinuxConfig extends ConfigBase {

    private static final String fileName = "linuxConfig.properties";

    private static boolean seenConfigurator = false;
    private static boolean glfwUseX11 = true;

    public LinuxConfig() {
        super(fileName);
    }

    @Override
    public void loadConfig() {
        seenConfigurator = getBoolean("seen_configurator");
        glfwUseX11 = getBoolean("glfw_use_x11");
    }

    public static boolean hasSeenConfigurator() {
        return seenConfigurator;
    }

    public static void setSeenConfigurator(boolean value) {seenConfigurator = value;
    }

    public static boolean shouldGlfwUseX11() {
        return glfwUseX11;
    }

    public static void setGlfwUseX11(boolean value) {
        glfwUseX11 = value;
    }
}
