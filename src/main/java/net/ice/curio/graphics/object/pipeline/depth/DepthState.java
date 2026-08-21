package net.ice.curio.graphics.object.pipeline.depth;

public record DepthState(
		boolean enableDepthTest,
		boolean enableDepthWrite,
		CompareFunction compareFunction,
		boolean enableDepthBoundTest,
		boolean enableStencilTest
) {}
