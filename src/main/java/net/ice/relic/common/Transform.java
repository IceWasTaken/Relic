package net.ice.relic.common;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {

    public Vector3f position;
    public Quaternionf rotation;
    public Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);

    public Transform() {
        position = new Vector3f();
        rotation = new Quaternionf();
    }

    public Transform(Vector3f position, Quaternionf rotation) {
        this.position = position;
        this.rotation = rotation;
    }




}
