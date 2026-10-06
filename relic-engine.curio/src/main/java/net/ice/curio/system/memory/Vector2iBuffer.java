package net.ice.curio.system.memory;

import org.joml.Vector2i;

public class Vector2iBuffer extends Buffer<Vector2i> {

	public Vector2iBuffer() {
		super(new Size().addInt(2));
	}

	@Override
	protected Vector2i readNew() {
		return new Vector2i(buffer.getInt(), buffer.getInt());
	}

	@Override
	protected Vector2i readInto(Vector2i existing) {
		return existing.set(buffer.getInt(), buffer.getInt());
	}
}
