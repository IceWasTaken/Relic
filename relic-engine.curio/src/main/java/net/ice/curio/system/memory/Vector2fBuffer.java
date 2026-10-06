package net.ice.curio.system.memory;

import org.joml.Vector2f;

public class Vector2fBuffer extends Buffer<Vector2f> {

	public Vector2fBuffer() {
		super(new Size().addFloat(2));
	}

	@Override
	protected Vector2f readNew() {
		return new Vector2f(buffer.getFloat(), buffer.getFloat());
	}

	@Override
	protected Vector2f readInto(Vector2f existing) {
		return existing.set(buffer.getFloat(), buffer.getFloat());
	}

}
