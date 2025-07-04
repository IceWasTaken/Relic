package net.ice.relic.engine.config;

public class ConfigEntry<T> {

    private T value;
    private final String key;

    public ConfigEntry(String key, T value) {
        this.value = value;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
