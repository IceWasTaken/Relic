package net.ice.relic.core.physics;

import net.ice.relic.core.ecs.component.components.PhysicsComponent;
import net.ice.relic.core.ecs.component.components.TransformComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.scene.Scene;
import org.joml.Vector3f;

public class PhysicsWorld {

	public static final Vector3f GRAVITY = new Vector3f(0,-9.81f,0);

	public static void processPhysics(Scene scene, float deltaTime) {
		for(Entity entity : scene.getEntities()) {
			if(entity.getComponent(PhysicsComponent.class) != null && entity.getComponent(TransformComponent.class) != null) {
				PhysicsComponent physicsComponent = entity.getComponent(PhysicsComponent.class);
				TransformComponent transformComponent = entity.getComponent(TransformComponent.class);

				Vector3f force = new Vector3f(GRAVITY).mul(physicsComponent.getMass(), new Vector3f());

				physicsComponent.getVelocity().add(
						force.div(physicsComponent.getMass()).mul(deltaTime)
				);

				transformComponent.getPosition().add(physicsComponent.getVelocity().mul(deltaTime));
				transformComponent.update();
			}
		}


	}
}
