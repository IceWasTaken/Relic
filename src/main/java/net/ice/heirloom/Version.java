package net.ice.heirloom;

public record Version(int major, int minor, int patch) {

    public Version(int major, int minor) {
        this(major, minor, 0);
    }

    public Version(int major) {
        this(major, 0, 0);
    }

    public String toString() {
        return major + "." + minor + "." + patch;
    }

    public static Version fromInt(int version) {
        int patch = version & 0xFFF;
        int minor = (version >> 12) & 0xFF8;
        int major = (version >> 22) & 0x7F;
        return new Version(major, minor, patch);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Version version) {
            return version.major == major &&
                    version.minor == minor &&
                    version.patch == patch;
        }
        return false;
    }
}
