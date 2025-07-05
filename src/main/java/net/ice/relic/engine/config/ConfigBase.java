package net.ice.relic.engine.config;

import java.nio.file.Path;

public abstract class ConfigBase {

    protected ConfigFile configFile;
    protected ConfigFile defaultConfigFile;

    protected ConfigBase(Path configFilePath, Path defaultConfigFilePath) {
        this.configFile = ConfigFile.loadFromFile(configFilePath.toString());
        this.defaultConfigFile = ConfigFile.loadFromJar(defaultConfigFilePath.toString());
    }

    protected Integer getInt(String key) {
        return Integer.parseInt(configFile.getProperty(key, defaultConfigFile.getProperty(key)));
    }

    protected Boolean getBoolean(String key) {
        return Boolean.parseBoolean(configFile.getProperty(key, defaultConfigFile.getProperty(key)));
    }

    protected Float getFloat(String key) {
        return Float.parseFloat(configFile.getProperty(key, defaultConfigFile.getProperty(key)));
    }

    protected String getString(String key) {
        return configFile.getProperty(key, defaultConfigFile.getProperty(key));
    }

    //TODO: rewrite getEnum.
//    protected <T extends Enum<T>> T> getEnum(String key, Class<T> enumClass) {
//        String valueStr = configFile.getProperty(key, defaultConfigFile.getProperty(key));
//        T value = T.valueOf(enumClass, valueStr);
//        T> entry = new >(key, value);
//        entries.putIfAbsent(key, entry);
//        return entry;
//    }

    protected abstract ConfigBase loadConfig();
}
