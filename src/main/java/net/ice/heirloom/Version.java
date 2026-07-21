package net.ice.heirloom;

public record Version(int major, int minor, int patch) implements Comparable<Version> {

    public Version(int major, int minor) {
        this(major, minor, 0);
    }

    public Version(int major) {
        this(major, 0, 0);
    }

    public static Version fromInt(int version) {
        int patch = version & 0xFFF;
        int minor = (version >> 12) & 0xFF8;
        int major = (version >> 22) & 0x7F;
        return new Version(major, minor, patch);
    }

    @Override
    public int compareTo(Version other) {
        int result = Integer.compare(major, other.major);
        if (result != 0) {
            return result;
        }

        result = Integer.compare(minor, other.minor);
        if (result != 0) {
            return result;
        }

        return Integer.compare(patch, other.patch);
    }


    public boolean isNewer(Version other) {
        return compareTo(other) > 0;
    }

    public boolean isOlder(Version other) {
        return compareTo(other) < 0;
    }

    public String toString() {
        return major + "." + minor + "." + patch;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Version(int major1, int minor1, int patch1)) {
            return major1 == major && minor1 == minor && patch1 == patch;
        }
        return false;
    }




}
