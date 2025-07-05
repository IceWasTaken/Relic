package net.ice.relic.engine.config.configs;

import net.ice.relic.engine.config.ConfigBase;

import java.nio.file.Path;

public class WindowConfig extends ConfigBase {

    private static final String defaultFileName = "default_window_config.properties";
    private static final String fileName = "windowConfig.properties";

    private static final String configPath = "config/" + fileName;
    private static final String defaultConfigPath = "net/ice/relic/config/" + defaultFileName;

    private String title;
    private Integer width;
    private Integer height;
    private Boolean vsync;
    private Boolean fullscreen;

    public WindowConfig() {
        super(configPath, defaultConfigPath);
    }

    @Override
    public WindowConfig loadConfig() {
        title = getString("title");
        width = getInt("width");
        height = getInt("height");
        vsync = getBoolean("vsync");
        fullscreen = getBoolean("fullscreen");

        return this;
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isVsync() {
        return vsync;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setVsync(boolean vsync) {
        this.vsync = vsync;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }
}
