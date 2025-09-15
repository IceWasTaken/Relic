package net.ice.relic.common.math.vector;

public class Vector4f {

    public float x;
    public float y;
    public float z;
    public float w;

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f(float val) {
        this.x = val;
        this.y = val;
        this.z = val;
        this.w = val;
    }

    //for compat and transitional period
    public Vector4f fromJOML(org.joml.Vector4f other) {
        return new Vector4f(other.x, other.y, other.z, other.w);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getW() {
        return w;
    }
}
