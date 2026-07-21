package net.ice.relic.core.rendering.shader.struct;

import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SceneInfoStruct extends Struct {

	public SceneInfoStruct(StructType structType) {
		super(structType);
	}

	@Override
	public Class<?> getRecord() {
		return SceneInfo.class;
	}

	public record SceneInfo(Matrix4f viewMatrix, Vector3f ambientLightColor, float ambientLightIntensity, Vector3f cameraPos, int lightCount) {}
}
