package net.ice.relic.core.rendering.backend.opengl.depricated.model;

import net.ice.heirloom.Version;

public class RelicModelHeader {

    public final Version version;
    public final int flags;
    public final long meshCount;
    public final long offset;

    public RelicModelHeader(Version version, int flags, long meshCount, long offset) {
        this.version = version;
        this.flags = flags;
        this.meshCount = meshCount;
        this.offset = offset;
    }
}
