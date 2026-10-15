package net.ice.relic.core.ecs.component.components;

import net.ice.relic.core.ecs.component.Component;
import net.ice.relic.core.ecs.entity.Entity;
import org.joml.Vector3f;

public class PhysicsComponent extends Component {

	private Vector3f velocity;
	private float mass = 4f;

	public PhysicsComponent() {
		velocity = new Vector3f();
	}

	@Override
	public void update(Entity entity, float deltaTime) {

	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public float getMass() {
		return mass;
	}
}
