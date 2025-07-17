package net.ice.relic.common;

import org.tinylog.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EngineConfig {

    private static EngineConfig instance;

    private boolean shouldUseVulkan;
    private boolean debugMode;

    private EngineConfig() {
        Properties properties = new Properties();

        try(InputStream stream = EngineConfig.class.getResourceAsStream("/" + "engine_config.properties")) {
            properties.load(stream);
            shouldUseVulkan = Boolean.parseBoolean(properties.getOrDefault("vulkan", false).toString());
            debugMode = Boolean.parseBoolean(properties.getOrDefault("debug", false).toString());

        } catch (IOException exception) {
            Logger.error("Could not read [{}] properties file", "engine_config.properties", exception);
        }
    }

    public boolean shouldUseVulkan() {
        return shouldUseVulkan;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public static EngineConfig getInstance() {
        if(instance == null) {
            instance = new EngineConfig();
        }
        return instance;
    }
}
