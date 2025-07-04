package net.ice.relic.engine.config;

import net.ice.relic.annotations.Rewrite;
import org.tinylog.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

@Rewrite
public abstract class ConfigBase {

    protected Map<String, ConfigEntry<?>> entries = new LinkedHashMap<>();

    protected ConfigFile configFile;
    protected ConfigFile defaultConfigFile;

    protected ConfigBase(Path configFilePath, Path defaultConfigFilePath) {
        this.configFile = new ConfigFile(configFilePath.toString(), false);
        this.defaultConfigFile = new ConfigFile(defaultConfigFilePath.toString(), true);

    }

    protected abstract ConfigBase loadConfig();

    protected ConfigEntry<Integer> getInt(String key) {
        ConfigEntry<Integer> entry;
        if(entries.containsKey(key) && entries.get(key).getValue() instanceof Integer) {
            entry = new ConfigEntry<>(key, (Integer) entries.get(key).getValue());
        } else {
             entry = new ConfigEntry<>(key, parseIntSmart(configFile.getProperty(key, defaultConfigFile.getProperty(key))));
        }
        entries.putIfAbsent(key, entry);
        return entry;
    }

    protected ConfigEntry<Boolean> getBoolean(String key) {
        ConfigEntry<Boolean> entry = new ConfigEntry<>(key, Boolean.parseBoolean(configFile.getProperty(key)));
        if(entry.getValue() == null) {
            entry = new ConfigEntry<>(key, Boolean.parseBoolean(defaultConfigFile.getProperty(key)));
        }
        entries.putIfAbsent(key, entry);
        return entry;
    }

    protected ConfigEntry<Float> getFloat(String key) {
        ConfigEntry<Float> entry = new ConfigEntry<>(key, Float.parseFloat(configFile.getProperty(key, defaultConfigFile.getProperty(key))));
        entries.putIfAbsent(key, entry);
        return entry;
    }

    protected ConfigEntry<String> getString(String key) {
        ConfigEntry<String> entry = new ConfigEntry<>(key, configFile.getProperty(key) == null ? defaultConfigFile.getProperty(key) : configFile.getProperty(key));
        entries.putIfAbsent(key, entry);
        return entry;
    }

    protected <T extends Enum<T>> ConfigEntry<T> getEnum(String key, Class<T> enumClass) {
        String valueStr = configFile.getProperty(key, defaultConfigFile.getProperty(key));
        T value = T.valueOf(enumClass, valueStr);
        ConfigEntry<T> entry = new ConfigEntry<>(key, value);
        entries.putIfAbsent(key, entry);
        return entry;
    }

    protected String formatProperty(String key, String value) {
        return String.format("%s = %s%n", key, value);
    }

    private int parseIntSmart(String string) {
        if(string == null) {
            return -1;
        } else {
            try {
                return Integer.parseInt(string);
            } catch(NumberFormatException exception) {
                return -1;
            }
        }
    }

    public void writeConfigToFileAndSave(Path configPath) {
        try {
            if (Files.notExists(configPath.getParent())) {
                Files.createDirectory(Path.of("config"));
            }

            File configFile = new File(this.configFile.getPath());

            if ((!configFile.exists() || configFile.delete()) && configFile.createNewFile()) {
                exportPrefs(configFile);
                Logger.info("Configuration saved to '{}'.", configFile.getAbsolutePath());
            } else {
                Logger.warn("Failed to overwrite existing config file at '{}'.", configFile.getAbsolutePath());
            }
        } catch (IOException exception) {
            Logger.error("Could not save window config file.", exception);
        }
    }

    protected void exportPrefs(File file) throws IOException {
        try (OutputStreamWriter writer = new FileWriter(file)) {
            for (Map.Entry<String, ConfigEntry<?>> entry : entries.entrySet()) {
                writer.write(formatProperty(entry.getKey(), entry.getValue().getValue().toString()));
            }
        }
    }
}
