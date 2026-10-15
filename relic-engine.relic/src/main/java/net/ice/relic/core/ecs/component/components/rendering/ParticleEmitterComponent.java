package net.ice.relic.core.ecs.component.components.rendering;

import net.ice.relic.core.ecs.component.Component;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.particle.ParticleSystem;
import org.joml.Vector3f;

public class ParticleEmitterComponent extends Component {

	private int rate = 25;
	private float accum = 0.0f;

	public ParticleEmitterComponent() {

	}

	@Override
	public void update(Entity entity, float deltaTime) {
		accum += deltaTime;

		float interval = 1.0f / rate;

		while(accum >= interval) {
			spawnParticle(entity);
			accum -= interval;
		}
	}

	private void spawnParticle(Entity entity) {
		ParticleSystem particleSystem = ParticleSystem.getInstance();
		particleSystem.respawn(entity, new Vector3f(0, -2, 0));
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}
}
