package net.ice.relic.core.physics;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;

public class PhysicsUtil {

	public static final Vector3f UP = new Vector3f(0, 1, 0);
	public static final Vector3f DOWN = new Vector3f(0, -1, 0);
	public static final Vector3f WEST = new Vector3f(0, 0, -1);
	public static final Vector3f EAST = new Vector3f(0, 0, 1);
	public static final Vector3f NORTH = new Vector3f(1, 0, 0);
	public static final Vector3f SOUTH = new Vector3f(-1, 0, 0);

	private static final Random RANDOM = new Random();

	public static void randomInDirectionOf(float speed, float maxAngle, Vector3f dir, Vector3f destination) {
		float u1 = RANDOM.nextFloat();
		float u2 = RANDOM.nextFloat();

		float cosTheta = 1.0f - u1 * (1.0f - (float) Math.cos(maxAngle));
		float sinTheta = (float) Math.sqrt(1.0f - cosTheta * cosTheta);

		float phi = u2 * 2.0f * (float) Math.PI;

		destination.set(
				sinTheta * (float) Math.cos(phi),
				sinTheta * (float) Math.sin(phi),
				cosTheta
		);

		destination.rotate(new Quaternionf().rotationTo(EAST, dir));
		destination.mul(speed);
	}

	public static Vector3f randomInDirectionOf(float speed, float maxAngle, Vector3f dir) {
		Vector3f temp = new Vector3f();
		randomInDirectionOf(speed, maxAngle, dir, temp);
		return temp;
	}

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
