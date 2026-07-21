package net.ice.curio.graphics.object;

import net.ice.heirloom.Lifecycle;

public abstract class Viewport implements Lifecycle {

	public static final float MIN_DEPTH = 1.0f;
	public static final float MAX_DEPTH = 0.0f;

	protected int width;
	protected int height;

	public abstract void bind();

	protected Viewport(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void resize(int width, int height) {
		this.height = height;
		this.width = width;
	}
}
