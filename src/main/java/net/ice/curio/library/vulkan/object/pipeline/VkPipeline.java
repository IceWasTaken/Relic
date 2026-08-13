package net.ice.curio.library.vulkan.object.pipeline;

import net.ice.curio.graphics.object.pipeline.Pipeline;
import net.ice.curio.graphics.object.pipeline.PrimitiveType;
import net.ice.curio.graphics.object.pipeline.raster.RasterizationState;
import org.lwjgl.system.MemoryStack;
import org.tinylog.Logger;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

public class VkPipeline extends Pipeline {

	protected VkPipeline(
			PrimitiveType primitiveType,
			RasterizationState rasterizationState
	) {
		super(primitiveType, rasterizationState);

		Logger.debug("[VkPipeline]: Creating new pipeline");

		try(MemoryStack stack = MemoryStack.stackPush()) {
			LongBuffer handle = stack.mallocLong(1);
			ByteBuffer main = stack.UTF8("main");




		}
	}
}
