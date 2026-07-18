package net.ice.curio.library.opengl.object;

import net.ice.curio.graphics.object.Viewport;

import static org.lwjgl.opengl.GL11.glDepthRange;
import static org.lwjgl.opengl.GL11.glViewport;

public class GLViewport extends Viewport {

	public GLViewport(int width, int height) {
		super(width, height);
	}

	@Override
	public void bind() {
		glViewport(0, 0, width, height);
		glDepthRange(MIN_DEPTH, MAX_DEPTH);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}
}
