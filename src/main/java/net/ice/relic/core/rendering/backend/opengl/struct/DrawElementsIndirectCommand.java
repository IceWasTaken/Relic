package net.ice.relic.core.rendering.backend.opengl.struct;

import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;

public class DrawElementsIndirectCommand extends Struct {

	public DrawElementsIndirectCommand() {
		super(StructType.RAW);
	}

	@Override
	public Class<?> getRecord() {
		return DrawElementsIndirectCommandStruct.class;
	}

	record DrawElementsIndirectCommandStruct(
			int indexCount,
			int instanceCount,
			int firstIndex,
			int baseVertex,
			int baseInstance
	) {}
}




