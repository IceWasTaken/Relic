package net.ice.curio.system.memory;

import org.joml.Vector3f;

public class Vector3fBuffer extends Buffer<Vector3f> implements AutoCloseable {

	public Vector3fBuffer() {
		super(new Size().addFloat(3));
	}

	@Override
	protected Vector3f readNew() {
		return new Vector3f(buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
	}

	@Override
	protected Vector3f readInto(Vector3f existing) {
		return existing.set(buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
	}
}
