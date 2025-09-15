package net.ice.relic.common.generation;

import org.joml.Vector3f;

public class ChunkPosition extends Vector3f {

    public ChunkPosition(float x, float y, float z) {
        super(x, y, z);
    }

    public ChunkPosition() {
        super();
    }

    public static ChunkPosition fromVector3f(Vector3f vector3f) {
        return new ChunkPosition(vector3f.x / 16, vector3f.y / 16, vector3f.z / 16);
    }
}
