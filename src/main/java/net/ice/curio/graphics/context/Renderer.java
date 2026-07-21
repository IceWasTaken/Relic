package net.ice.curio.graphics.context;

public abstract class Renderer {

	protected final GraphicsContext graphicsContext;

	public abstract void resize(int width, int height);

	protected Renderer(GraphicsContext graphicsContext) {
		this.graphicsContext = graphicsContext;
	}

}
