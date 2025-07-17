package net.ice.relic.common;

import static org.lwjgl.vulkan.VK10.VK_MAKE_VERSION;

public class Version {

    private final int major;
    private final int minor;
    private final int patch;

    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public String toString() {
        return major + "." + minor + "." + patch;
    }

    public int makeVulkanVersion() {
        return VK_MAKE_VERSION(major, minor, patch);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Version version) {
            return version.major == major && version.minor == minor && version.patch == patch;
        }
        return false;
    }
}
