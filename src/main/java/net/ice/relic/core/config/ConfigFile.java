package net.ice.relic.core.config;

import net.ice.relic.core.resource.Resource;
import org.tinylog.Logger;

import java.io.*;
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

    public static ConfigFile loadFromJar(Resource path) {
        ConfigFile file = new ConfigFile(path.getAsPath());
        try(InputStream stream = ConfigFile.class.getClassLoader().getResourceAsStream( path.getAsPath())) {

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