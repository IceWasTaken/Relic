package net.ice.curio.graphics.object.pipeline.raster;

public record RasterizationState(
		PolygonMode polygonMode,
		FrontFace frontFace,
		CullMode cullMode,
		boolean clampDepth,
		float lineWidth
) {

	public static RasterizationState DEFAULT = new RasterizationState(
			PolygonMode.FILL,
			FrontFace.COUNTER_CLOCKWISE,
			CullMode.BACK,
			true,
			1.0f
	);
}
