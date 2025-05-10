package net.ice.relic.engine.common;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class AABB {

    private Vector3f min;
    private Vector3f max;

    public AABB(float x1, float y1, float z1, float x2, float y2, float z2) {
        this(new Vector3f(x1, y1, z1), new Vector3f(x2, y2, z2));
    }

    public AABB(Vector3f pos1, Vector3f pos2) {
        this.min = new Vector3f(
                Math.min(pos1.x, pos2.x),
                Math.min(pos1.y, pos2.y),
                Math.min(pos1.z, pos2.z));
        this.max = new Vector3f(
                Math.max(pos1.x, pos2.x),
                Math.max(pos1.y, pos2.y),
                Math.max(pos1.z, pos2.z));
    }

    public boolean intersects(AABB other) {
        return this.min.x <= other.max.x && this.max.x >= other.min.x &&
                this.min.y <= other.max.y && this.max.y >= other.min.y &&
                this.min.z <= other.max.z && this.max.z >= other.min.z;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AABB other)) return false;
        return min.equals(other.min) && max.equals(other.max);
    }

    public Vector3f getMax() {
        return max;
    }

    public Vector3f getMin() {
        return min;
    }

    public void setMax(Vector3f max) {
        this.max = max;
    }

    public void setMin(Vector3f min) {
        this.min = min;
    }
}
