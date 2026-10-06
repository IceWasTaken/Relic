package net.ice.curio.graphics;

import net.ice.curio.Curio;
import net.ice.curio.graphics.context.GraphicsContext;
import org.joml.Vector2i;

public abstract class Renderer {

	private static Renderer instance;

	protected final GraphicsContext graphicsContext;
	protected final Curio curio;

	public abstract void init();
	public abstract void render();
	public abstract void destroy();

	public abstract void resizeWindow(Vector2i size);
	public abstract void resizeFramebuffer(Vector2i size);

	public Renderer(Curio curio) {
		this.curio = curio;
		this.graphicsContext = curio.getGraphicsContext();
	}
}
