package net.ice.relic.engine.opengl;

import org.joml.Vector3f;

public class EmissiveLight {
    public Vector3f position;
    public Vector3f color;
    public float strength;

    public EmissiveLight(Vector3f position, Vector3f color, float strength) {
        this.position = position;
        this.color = color;
        this.strength = strength;
    }
}
