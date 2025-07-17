package net.ice.relic.common;

import org.joml.Vector3f;

public class AABB {
    private final Vector3f min;
    private final Vector3f max;

    public AABB(Vector3f min, Vector3f max) {
        this.min = new Vector3f(min);
        this.max = new Vector3f(max);
    }

    public boolean intersects(AABB other) {
        return max.x > other.min.x && min.x < other.max.x &&
                max.y > other.min.y && min.y < other.max.y &&
                max.z > other.min.z && min.z < other.max.z;
    }

    public Vector3f getMin() {
        return new Vector3f(min);
    }

    public Vector3f getMax() {
        return new Vector3f(max);
    }

    public static AABB fromCenterAndHalfExtents(Vector3f center, float halfSize) {
        Vector3f min = new Vector3f(center).sub(halfSize, halfSize, halfSize);
        Vector3f max = new Vector3f(center).add(halfSize, halfSize, halfSize);
        return new AABB(min, max);
    }
}
