package net.ice.relic.core.particle;

import net.ice.heirloom.color.RGBColor;
import net.ice.relic.core.ecs.component.components.TransformComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.physics.PhysicsUtil;
import org.joml.Random;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

//todo: convert particle handling to a compute shader
public class ParticleSystem {

	private final List<Particle> particles;

	public ParticleSystem() {
		this.particles = new ArrayList<>();
	}

	public void updateParticles(float delta) {
		particles.removeIf(particle -> particle.getLife() < 0.0f);

		for(Particle particle : particles) {
			particle.update(delta);
		}
	}

	public void respawn(Entity source, Vector3f offset) {
		Random random = new Random();
		float rColor = 0.5f + (random.nextFloat() % 100) / 100.0f;

		Particle particle = new Particle(
				source.getComponent(TransformComponent.class).getPosition().add(offset, new Vector3f()),
				PhysicsUtil.randomVelocity(2f),
				new RGBColor(rColor + 25, rColor + 5, rColor + 100, 1.0f),
				1.0f
		);

		particles.add(particle);
	}

	public List<Particle> getParticles() {
		return particles;
	}
}
