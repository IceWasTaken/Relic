package net.ice.relic.core.physics;

import org.joml.Vector3f;

import java.util.Random;

public class PhysicsUtil {

	private static final Random RANDOM = new Random();

	public static void randomVelocity(float speed, Vector3f destination) {
		float theta = (float) (RANDOM.nextFloat() * 2.0 * Math.PI);

		float z = RANDOM.nextFloat() * 2.0f - 1.0f;

		float r = (float) Math.sqrt(1.0f - z * z);
		float x = (float) (r * Math.cos(theta));
		float y = (float) (r * Math.sin(theta));

		destination.set(x, y, z).mul(speed);
	}

	public static Vector3f randomVelocity(float speed) {
		Vector3f vector = new Vector3f();
		randomVelocity(speed, vector);
		return vector;
	}

}
