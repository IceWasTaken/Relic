package net.ice.relic.engine.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigFile extends Properties {

    private SchrodingersFile fileStatus;
    private String path;
    private boolean jarResource;

    public ConfigFile(String path, boolean jarResource) {
        this.path = path;
        this.jarResource = jarResource;

        try (InputStream stream = jarResource ? ConfigFile.class.getResourceAsStream("/" + path) : new FileInputStream(path)) {
            if (stream != null && stream.available() > 0) {
                this.load(stream);
                fileStatus = SchrodingersFile.ALIVE;
            } else {
                fileStatus = SchrodingersFile.DEAD;
            }


        } catch (IOException | NullPointerException e) {
            fileStatus = SchrodingersFile.DEAD;
        }
    }

    public String getPath() {
        return jarResource ? null : path;
    }

    public boolean isAlive() {
        return fileStatus == SchrodingersFile.ALIVE;
    }

    public boolean isJarResource() {
        return jarResource;
    }

    public SchrodingersFile getStatus() {
        return fileStatus;
    }

    public enum SchrodingersFile {
        ALIVE,
        DEAD
    }
}