package net.ice.relic.core.config;

import net.ice.relic.core.resource.Resource;

public abstract class ConfigBase {

    protected ConfigFile configFile;
    protected ConfigFile defaultConfigFile;

    protected ConfigBase(String configFilePath, Resource defaultConfigFilePath) {
        this.configFile = ConfigFile.loadFromFile(configFilePath);
        this.defaultConfigFile = ConfigFile.loadFromJar(defaultConfigFilePath);
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

    protected <T extends Enum<T>> T getEnum(String key, Class<T> enumClass) {
        String valueStr = configFile.getProperty(key, defaultConfigFile.getProperty(key));
        return T.valueOf(enumClass, valueStr);
    }


    protected abstract ConfigBase loadConfig();
}
