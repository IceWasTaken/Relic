package net.ice.curio.graphics.object.pipeline.depth;

import static net.ice.curio.graphics.object.pipeline.depth.CompareFunction.GREATER;

public record DepthState(
		boolean enableDepthTest,
		boolean enableDepthWrite,
		CompareFunction compareFunction,
		boolean enableDepthBoundTest,
		boolean enableStencilTest
) {

	public static DepthState DEFAULT = new DepthState(
			true,
			true,
			GREATER,
			false,
			false
	);

}
