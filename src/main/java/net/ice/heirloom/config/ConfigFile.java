package net.ice.heirloom.config;

import org.tinylog.Logger;
import java.io.*;
import java.util.Properties;

public class ConfigFile extends Properties {

    private final String path;

    private ConfigFile(String path) {
        this.path = path;
    }

    public static ConfigFile loadFromFile(String path) {
        ConfigFile file = new ConfigFile(path);

        try(InputStream stream = new FileInputStream(path)) {
            file.load(stream);
        } catch (IOException exception) {
            Logger.warn("Unable to load config file: {}", path);
            return null;
        }

        return file;
    }

    public void writeToFile() {
        try(OutputStream stream = new FileOutputStream(path)) {

        } catch (IOException exception) {
            throw new RuntimeException("Error while writing to file.");
        }
    }

    public String getPath() {
        return path;
    }
}