package net.ice.relic.core.rendering.backend.opengl;

import net.ice.curio.config.RendererConfig;
import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;
import net.ice.curio.library.opengl.object.buffer.ShaderStorageBufferObject;
import net.ice.curio.library.opengl.wrapper.enums.Usage;
import net.ice.relic.core.rendering.backend.opengl.depricated.model.Model;
import net.ice.relic.core.scene.SceneObject;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class GlobalBuffers {

	private ShaderStorageBufferObject instanceBuffer;

	public GlobalBuffers() {

	}

	public void createInstanceBuffer() {
		Struct instanceStruct = new Struct(StructType.STD430) {
			@Override
			public Class<?> getRecord() {
				return Instances.class;
			}
		};

		this.instanceBuffer = new ShaderStorageBufferObject(instanceStruct, RendererConfig.getMaxDrawElements());

		ByteBuffer dummy = MemoryUtil.memAlloc(RendererConfig.getMaxDrawElements() * 80);
		instanceBuffer.bufferData(dummy, Usage.STREAM_DRAW);
		MemoryUtil.memFree(dummy);

		instanceBuffer.bindBase(9);
	}

	public void updateInstanceBuffer(GLRenderer renderer) {
		int index = 0;
		for (Model model : renderer.getApplication().getCurrentScene().getModels().values()) {
			if (model.isAnimated()) continue;
			for (GLRenderer.MeshDrawData meshDrawData : model.getMeshDrawData()) {
				for (SceneObject object : model.getSceneObjects()) {
					instanceBuffer.setMat4x4(0, index, object.getTransform().getTransformMatrix());
					instanceBuffer.setInt(1, index, meshDrawData.materialIdx());
					index++;

				}
			}
		}


	}

	public static record Instances(Matrix4f modelMatrix, int materialIndex) {}

}
