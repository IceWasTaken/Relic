package net.ice.relic.engine.opengl;

import org.joml.Vector3f;

public class AABB {

    private Vector3f min;
    private Vector3f max;

    public AABB(Vector3f min, Vector3f max) {
        this.min = new Vector3f(min);
        this.max = new Vector3f(max);
    }

    public void intersects(AABB other) {

    }
}
