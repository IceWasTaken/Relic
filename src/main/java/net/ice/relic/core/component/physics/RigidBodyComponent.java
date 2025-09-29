package net.ice.relic.core.component.physics;

import net.ice.relic.core.component.Component;
import net.ice.relic.core.interfaces.Updatable;
import net.ice.relic.core.scene.Transform;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class RigidBodyComponent extends Component implements Updatable {

    private float mass = 1f;
    private Vector3f velocity = new Vector3f();
    private boolean gravity = true;
    private List<Vector3f> accelAtPos;

    public RigidBodyComponent() {
        this.accelAtPos = new ArrayList<>();
    }

    public void updateAccelAtPos(List<Vector3f> list) {
        this.accelAtPos = list;
    }

    @Override
    public void update(float deltaTime) {
        if (gravity) {
            // Start with natural gravity (Earth-like)
            Vector3f acceleration = new Vector3f(0, -9.81f, 0);

            // Add contributions from all gravity sources
            for (Vector3f sourceAccel : accelAtPos) {
                acceleration.add(sourceAccel);
            }


            // Integrate velocity: v += a * dt
            velocity.fma(deltaTime, acceleration);
            
            // Clear accelerations for next frame
            accelAtPos.clear();
        }

        // Integrate position: x += v * dt
        Transform transform = attachedObject.getTransform();
        Vector3f position = transform.getPosition();
        position.fma(deltaTime, velocity);
        transform.setPosition(position);
    }

    public void enableGravity() {
        this.gravity = true;
    }
    public void disableGravity() {
        this.gravity = false;
    }
}
