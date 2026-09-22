package net.ice.heirloom;

/**
 * Represents a version made of three fields.
 * <ul>
 *     <li>Major</li>
 *     <li>Minor</li>
 *     <li>Patch</li>
 * </ul>
 */
public record Version(int major, int minor, int patch) implements Comparable<Version> {

    /**
     * Instantiates a new Version object with only major and minor fields.
     *
     * @param major The major version.
     * @param minor The minor version.
     */
    public Version(int major, int minor) {
        this(major, minor, 0);
    }

    /**
     * Instantiates a new Version object with only a major field.
     *
     * @param major The major version.
     */
    public Version(int major) {
        this(major, 0, 0);
    }

    /**
     * Converts an integer to a new Version object.
     *
     * @param version the integer to be converted.
     * @return The version object with fields as they exist in the integer.
     */
    public static Version fromInt(int version) {
        int patch = version & 0xFFF;
        int minor = (version >> 12) & 0x3FF;
        int major = (version >> 22) & 0x7F;
        return new Version(major, minor, patch);
    }

    //holy shit that high school html class wasn't useless after all
    /**
     * Converts the version fields into a 32-bit integer.
     * <ul>
     *     <li>Packs the major version as 7 bits into positions 28-22.</li>
     *     <li>Packs the minor version as 10 bits into positions 21-12.</li>
     *     <li>Packs the patch version as 12 bits into positions 11-0.</li>
     * </ul>
     *
     * @return The packed version integer.
     */
    public int toInt() {
        int major = this.major << 22;
        int minor = this.minor << 12;
        int patch = this.patch;

        return major | minor | patch;
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


    /**
     * Checks if the other version is newer than this version object.
     *
     * @param other Version to be checked against the current object
     * @return True if the parameter version is newer than this current object
     */
    public boolean isNewer(Version other) {
        return compareTo(other) > 0;
    }

    /**
     * Checks if the other version is older than this version object
     *
     * @param other Version to be checked against the current object
     * @return True if the parameter version is older than this current object
     */
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
