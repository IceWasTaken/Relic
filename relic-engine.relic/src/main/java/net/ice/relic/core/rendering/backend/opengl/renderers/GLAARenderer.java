package net.ice.relic.core.rendering.backend.opengl.renderers;

import net.ice.curio.library.opengl.object.pipeline.GLPipeline;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;

public class GLAARenderer {

	private GLPipeline pipeline;
	private final GLRenderer renderer;

	public GLAARenderer(GLRenderer renderer) {
		this.renderer = renderer;
	}


	public void init() {



	}
}
