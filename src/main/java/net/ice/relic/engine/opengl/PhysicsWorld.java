package net.ice.relic.engine.opengl;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3f;

public class PhysicsWorld {
    private final List<RigidBody> bodies = new ArrayList<>();
    private final Vector3f gravity = new Vector3f(0, -9.81f, 0);

    public void addBody(RigidBody body) {
        bodies.add(body);
    }

    public void step(float deltaTime) {
        for (RigidBody body : bodies) {
            if (!body.isStatic()) {
                body.applyForce(new Vector3f(gravity).mul(body.mass));
                body.update(deltaTime);
            }
        }

        for (int i = 0; i < bodies.size(); i++) {
            for (int j = i + 1; j < bodies.size(); j++) {
                RigidBody a = bodies.get(i);
                RigidBody b = bodies.get(j);
                if (a.getAABB().intersects(b.getAABB())) {
                    resolveCollision(a, b);
                }
            }
        }
    }

    private void resolveCollision(RigidBody a, RigidBody b) {
        if (a.velocity.y < 0) a.velocity.y *= -0.5f;
        if (b.velocity.y < 0) b.velocity.y *= -0.5f;
    }

    public List<RigidBody> getBodies() {
        return bodies;
    }
}
