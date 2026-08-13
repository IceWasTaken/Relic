package net.ice.curio.graphics.object.pipeline;

import net.ice.curio.graphics.object.pipeline.raster.CullMode;
import net.ice.curio.graphics.object.pipeline.raster.FrontFace;
import net.ice.curio.graphics.object.pipeline.raster.PolygonMode;
import net.ice.curio.graphics.object.pipeline.raster.RasterizationState;

public abstract class Pipeline {

    public static final PrimitiveType DEFAULT_PRIMITIVE_TYPE = PrimitiveType.TRIANGLE;
    public static final RasterizationState DEFAULT_RASTERIZATION_STATE = new RasterizationState(
            PolygonMode.FILL,
            FrontFace.CLOCKWISE,
            CullMode.NONE,
            true,
            1.0f
    );

    private final PrimitiveType primitiveType;
    private final RasterizationState rasterizationState;

    protected Pipeline(
            PrimitiveType primitiveType,
            RasterizationState rasterizationState
    ) {
        this.primitiveType = primitiveType;
        this.rasterizationState = rasterizationState;
    }
}


