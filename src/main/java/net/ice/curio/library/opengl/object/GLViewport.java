package net.ice.curio.library.opengl.object;

import net.ice.curio.graphics.object.Viewport;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_LOWER_LEFT;
import static org.lwjgl.opengl.GL41.glClearDepthf;
import static org.lwjgl.opengl.GL45.GL_ZERO_TO_ONE;
import static org.lwjgl.opengl.GL45.glClipControl;

public class GLViewport extends Viewport {

	public GLViewport(int width, int height) {
		super(width, height);
	}

	@Override
	public void bind() {
		glClipControl(GL_LOWER_LEFT, GL_ZERO_TO_ONE);
		glDepthRange(MIN_DEPTH, MAX_DEPTH);
		glDepthFunc(GL_GREATER);
		glClearDepthf(MAX_DEPTH);
		glViewport(0, 0, width, height);
	}
}
