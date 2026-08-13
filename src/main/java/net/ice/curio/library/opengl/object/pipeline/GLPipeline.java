package net.ice.curio.library.opengl.object.pipeline;

import net.ice.curio.graphics.object.pipeline.Pipeline;
import net.ice.curio.graphics.object.pipeline.PrimitiveType;
import net.ice.curio.graphics.object.pipeline.raster.RasterizationState;

public class GLPipeline extends Pipeline {

	protected GLPipeline(
			PrimitiveType primitiveType,
			RasterizationState rasterizationState
	) {
		super(
				primitiveType,
				rasterizationState
		);


	}



}
