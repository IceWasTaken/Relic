package net.ice.curio.graphics.object.pipeline;

import net.ice.curio.graphics.object.pipeline.depth.DepthState;
import net.ice.curio.graphics.object.pipeline.raster.CullMode;
import net.ice.curio.graphics.object.pipeline.raster.FrontFace;
import net.ice.curio.graphics.object.pipeline.raster.PolygonMode;
import net.ice.curio.graphics.object.pipeline.raster.RasterizationState;
import org.tinylog.Logger;

public abstract class Pipeline {

    public static final PrimitiveType DEFAULT_PRIMITIVE_TYPE = PrimitiveType.TRIANGLE;
    public static final RasterizationState DEFAULT_RASTERIZATION_STATE = new RasterizationState(
            PolygonMode.FILL,
            FrontFace.CLOCKWISE,
            CullMode.NONE,
            true,
            1.0f
    );

    protected final PrimitiveType primitiveType;
    protected final RasterizationState rasterizationState;
    protected final DepthState depthState;

    public abstract void bindPipeline();

    protected Pipeline(
            PrimitiveType primitiveType,
            RasterizationState rasterizationState,
            DepthState depthState
    ) {
        this.primitiveType = primitiveType;
        this.rasterizationState = rasterizationState;
        this.depthState = depthState;
    }
}


