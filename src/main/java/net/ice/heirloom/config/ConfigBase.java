package net.ice.heirloom.config;

public abstract class ConfigBase {

    protected ConfigFile configFile;

    protected ConfigBase(String configFilePath) {
        this.configFile = ConfigFile.loadFromFile("config/" + configFilePath);

        if(!(configFile == null)) {
            loadConfig();
        }
    }

    protected Integer getInt(String key) {
        return Integer.parseInt(configFile.getProperty(key));
    }

    protected Boolean getBoolean(String key) {
        return Boolean.parseBoolean(configFile.getProperty(key));
    }

    protected Float getFloat(String key) {
        return Float.parseFloat(configFile.getProperty(key));
    }

    protected String getString(String key) {
        return configFile.getProperty(key);
    }

    protected <T extends Enum<T>> T getEnum(String key, Class<T> enumClass) {
        String valueStr = configFile.getProperty(key);
        return T.valueOf(enumClass, valueStr);
    }

    public abstract void loadConfig();



}
