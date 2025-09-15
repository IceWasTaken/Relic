package net.ice.relic.core.modding;

import net.ice.relic.core.Version;

public class ModData {

    private String name;
    private String author;
    private String description;

    private Version modVersion;
    private Version engineVersion;
    private Version applicationVersion;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Version getModVersion() {
        return modVersion;
    }
    public void setModVersion(Version version) {
        this.modVersion = version;
    }
    public void setModVersion(int major, int minor, int patch) { this.modVersion = new Version(major, minor, patch); }

    public Version getEngineVersion() {
        return engineVersion;
    }
    public void setEngineVersion(Version engineVersion) {
        this.engineVersion = engineVersion;
    }
    public void setEngineVersion(int major, int minor, int patch) { this.engineVersion = new Version(major, minor, patch); }

    public Version getApplicationVersion() {
        return applicationVersion;
    }
    public void setApplicationVersion(Version applicationVersion) {
        this.applicationVersion = applicationVersion;
    }
    public void setApplicationVersion(int major, int minor, int patch) { this.applicationVersion = new Version(major, minor, patch); }
}
