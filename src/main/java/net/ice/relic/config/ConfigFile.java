package net.ice.relic.config;

import org.tinylog.Logger;

import java.io.*;
import java.net.URL;
import java.util.*;

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
        }

        return file;
    }

    public static ConfigFile loadFromJar(String path) {
        ConfigFile file = new ConfigFile(path);
        try(InputStream stream = ConfigFile.class.getClassLoader().getResourceAsStream(path)) {
            URL url = ConfigFile.class.getClassLoader().getResource(path);

            if(stream == null) {
                throw new RuntimeException("Config file not in JAR.");
            }

            file.load(stream);

        } catch (IOException exception) {
            throw new RuntimeException("Error occurred while loading config.", exception);
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