package net.ice.relic.core.particle;

import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.heirloom.color.RGBColor;
import net.ice.relic.core.ecs.component.components.TransformComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.physics.PhysicsUtil;
import org.joml.Random;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.ARBBufferStorage.GL_MAP_COHERENT_BIT;
import static org.lwjgl.opengl.ARBBufferStorage.GL_MAP_PERSISTENT_BIT;
import static org.lwjgl.opengl.ARBShaderStorageBufferObject.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL30C.GL_MAP_WRITE_BIT;

public class ParticleSystem {

	private int particleCount;

	private static ParticleSystem INSTANCE;

	private GLBuffer particleBuffer;
	private Struct particleStruct;

	public ParticleSystem() {
		this.particleStruct = new Struct(StructType.STD430) {
			@Override
			public Class<?> getRecord() {
				return Particle.class;
			}
		};
		this.particleBuffer = new GLBuffer(particleStruct.getStride() * 10000L, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);
		particleBuffer.bind(GL_SHADER_STORAGE_BUFFER, 9);
	}

	public int lastUsedParticle = 0;
	public int firstUnusedParticle() {
		for (int i = lastUsedParticle; i < particleCount; ++i) {
			if(readLife(i) <= 0.0f) {
				lastUsedParticle = i;
				return i;
			}
		}

		for(int i = 0; i < lastUsedParticle; ++i) {
			if(readLife(i) <= 0.0f) {
				lastUsedParticle = i;
				return i;
			}
		}

		lastUsedParticle = 0;
		return 0;
	}
	
	public void respawn(Entity source, Vector3f offset) {
		Random random = new Random();
		int rColor = random.nextInt(255);
		int gColor = random.nextInt(255);
		int bColor = random.nextInt(255);

		int idx = firstUnusedParticle();
		int base = idx * particleStruct.getStride();
		particleBuffer.putVec4f(particleStruct.getOffset(0) + base, new RGBColor(rColor, gColor, bColor, 255).div().vec4f());
		particleBuffer.putVec3f(particleStruct.getOffset(1) + base, source.getComponent(TransformComponent.class).getPosition().add(offset, new Vector3f()));
		particleBuffer.putFloat(particleStruct.getOffset(2) + base, 1.0f); //life
		particleBuffer.putVec3f(particleStruct.getOffset(3) + base, PhysicsUtil.randomVelocity(5f));
		particleBuffer.putFloat(particleStruct.getOffset(4) + base, 4); //collision size
		particleBuffer.putVec2f(particleStruct.getOffset(5) + base, new Vector2f(1,1));
		particleBuffer.putInt(particleStruct.getOffset(6) + base, 0);

		particleCount++;
	}


//	public void respawn(Entity source, Vector3f offset) {
//		Random random = new Random();
//		float rColor = 0.5f + (random.nextFloat() % 100) / 100.0f;
//
//		Particle particle = new Particle(
//				source.getComponent(TransformComponent.class).getPosition().add(offset, new Vector3f()),
//				PhysicsUtil.randomVelocity(2f),
//				new RGBColor(rColor + 25, rColor + 5, rColor + 100, 1.0f),
//				1.0f
//		);
//
//		particles.add(particle);
//	}

	private float readLife(int index) {
		int base = index * particleStruct.getStride();

		return particleBuffer.readFloat(particleStruct.getOffset(2) + base);
	}

	public int getParticleCount() {
		return particleCount;
	}

	public void setParticleCount(int particleCount) {
		this.particleCount = particleCount;
	}

	public static ParticleSystem getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new ParticleSystem();
		}
		return INSTANCE;
	}

	private record Particle(
			Vector4f color,
			Vector3f pos,
			Vector3f velocity,
			Vector2f scale,
			float life,
			float collisionSize,
			int dead
	){}
}
