package net.ice.relic.engine.opengl;

import net.ice.relic.engine.common.AABB;
import org.joml.Vector3f;
import org.joml.Quaternionf;

public class RigidBody {
    public float mass;
    public Vector3f position;
    public Vector3f velocity;
    public Vector3f force;
    public Quaternionf rotation;
    public float size;

    private boolean isStatic;

    public RigidBody(float mass, Vector3f position) {
        this.mass = mass;
        this.position = new Vector3f(position);
        this.velocity = new Vector3f();
        this.force = new Vector3f();
        this.rotation = new Quaternionf();
        this.size = 1.0f;
        this.isStatic = false;
    }

    public void applyForce(Vector3f force) {
        this.force.add(force);
    }

    public void update(float deltaTime) {
        if (isStatic) return;

        Vector3f acceleration = new Vector3f(force).div(mass);
        velocity.add(acceleration.mul(deltaTime));
        position.add(new Vector3f(velocity).mul(deltaTime));
        force.zero();
    }

    public AABB getAABB() {
        return AABB.fromCenterAndHalfExtents(position, size / 2f);
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }
}
