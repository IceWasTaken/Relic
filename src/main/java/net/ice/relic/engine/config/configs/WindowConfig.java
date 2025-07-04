package net.ice.relic.engine.config.configs;

import net.ice.relic.annotations.Rewrite;
import net.ice.relic.engine.config.ConfigBase;
import net.ice.relic.engine.config.ConfigEntry;

import java.nio.file.Path;

@Rewrite
public class WindowConfig extends ConfigBase {

    private static final String defaultFileName = "default_window_config.properties";
    private static final String fileName = "windowConfig.properties";

    private static final Path configPath = Path.of("config/" + fileName);
    private static final Path defaultConfigPath = Path.of("net/ice/relic/config/" + defaultFileName);

    private ConfigEntry<String> TITLE;
    private ConfigEntry<Integer> WIDTH;
    private ConfigEntry<Integer> HEIGHT;
    private ConfigEntry<Boolean> VSYNC;
    private ConfigEntry<Boolean> FULLSCREEN;

    public WindowConfig() {
        super(configPath, defaultConfigPath);
    }

    @Override
    public WindowConfig loadConfig() {
        writeConfigToFileAndSave(configPath);

        TITLE = getString("title");
        WIDTH = getInt("width");
        HEIGHT = getInt("height");
        VSYNC = getBoolean("vsync");
        FULLSCREEN = getBoolean("fullscreen");

        writeConfigToFileAndSave(configPath);

        return this;
    }

    public String getTitle() {
        return TITLE.getValue();
    }

    public int getWidth() {
        return WIDTH.getValue();
    }

    public int getHeight() {
        return HEIGHT.getValue();
    }

    public boolean isVsync() {
        return VSYNC.getValue();
    }

    public boolean isFullscreen() {
        return FULLSCREEN.getValue();
    }

    public void setTitle(String title) {
        this.TITLE.setValue(title);
    }

    public void setWidth(int width) {
        this.WIDTH.setValue(width);
    }

    public void setHeight(int height) {
        this.HEIGHT.setValue(height);
    }

    public void setFullscreen(boolean fullscreen) {
        this.FULLSCREEN.setValue(fullscreen);
    }
}
