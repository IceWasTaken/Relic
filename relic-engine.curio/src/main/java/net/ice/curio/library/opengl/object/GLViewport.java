package net.ice.curio.library.opengl.object;

import net.ice.curio.graphics.object.Viewport;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_LOWER_LEFT;
import static org.lwjgl.opengl.GL41.glClearDepthf;
import static org.lwjgl.opengl.GL45.*;

public class GLViewport extends Viewport {

	private final float zNear;
	private final float zFar;
	private final int depth;
	private final int x;
	private final int y;

	public GLViewport(
			int x, int y,
			int width, int height,
			boolean reverseZ
	) {
		super(width, height);

		this.x = x;
		this.y = y;

		if(reverseZ) {
			this.zNear = MIN_DEPTH;
			this.zFar = MAX_DEPTH;
			this.depth = GL_ZERO_TO_ONE;
		} else {
			this.zNear = MAX_DEPTH;
			this.zFar = MIN_DEPTH;
			this.depth = GL_NEGATIVE_ONE_TO_ONE;
		}
	}

	@Override
	public void bind() {
		glClipControl(GL_LOWER_LEFT, depth);
		glDepthRange(zNear, zFar);
		glClearDepthf(zFar);
		glViewport(x, y, width, height);
	}
}
