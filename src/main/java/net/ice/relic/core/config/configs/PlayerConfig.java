package net.ice.relic.core.config.configs;

import net.ice.relic.core.config.ConfigBase;

public class PlayerConfig extends ConfigBase {

    private static final String defaultFileName = "default_player_config.properties";
    private static final String fileName = "playerConfig.properties";

    private static final String configPath = "config/" + fileName;
    private static final String defaultConfigPath = "net/ice/relic/config/" + defaultFileName;

    private float mouseSensitivity;

    public PlayerConfig() {
        super(configPath, defaultConfigPath);
    }

    @Override
    public PlayerConfig loadConfig() {
        mouseSensitivity = getFloat("mouse_sensitivity");

        return this;
    }

    public float getMouseSensitivity() {
        return mouseSensitivity;
    }

    public void setMouseSensitivity(float mouseSensitivity) {
        this.mouseSensitivity = mouseSensitivity;
    }
}
