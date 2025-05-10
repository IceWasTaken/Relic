package net.ice.relic.engine.opengl;

import net.ice.relic.engine.common.AABB;
import org.joml.*;

public class RigidBody {
    public float mass;
    public Vector3f position;
    public Vector3f velocity = new Vector3f();
    public Vector3f forces = new Vector3f();
    public Quaternionf rotation = new Quaternionf();

    public float size = 6.0f; // match scale in render

    public RigidBody(float mass, Vector3f position) {
        this.mass = mass;
        this.position = new Vector3f(position);
    }

    public void applyForce(Vector3f force) {
        this.forces.add(force);
    }

    public void update(float deltaTime) {
        Vector3f acceleration = new Vector3f(forces).div(mass);
        velocity.add(acceleration.mul(deltaTime));
        position.add(new Vector3f(velocity).mul(deltaTime));
        forces.zero(); // reset forces after applying
    }

    public AABB getAABB() {
        Vector3f half = new Vector3f(size / 2f);
        return new AABB(new Vector3f(position).sub(half), new Vector3f(position).add(half));
    }
}