package net.ice.curio.graphics.object.pipeline.raster;

public record RasterizationState(
		PolygonMode polygonMode,
		FrontFace frontFace,
		CullMode cullMode,
		boolean clampDepth,
		float lineWidth
) {}
