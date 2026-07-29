package net.ice.relic.core.rendering.backend.opengl;

import net.ice.curio.config.RendererConfig;
import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;
import net.ice.curio.library.opengl.object.buffer.GLBuffer;
import net.ice.curio.library.opengl.wrapper.enums.Usage;
import net.ice.relic.core.ecs.component.components.TransformComponent;
import net.ice.relic.core.ecs.component.components.rendering.model.StaticModelComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.Model;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

public class GlobalBuffers {

	private GLBuffer instanceBuffer;
	private Struct instanceBufferStruct;

	public GlobalBuffers() {

	}

	public void createInstanceBuffer() {
		this.instanceBufferStruct = new Struct(StructType.STD430) {
			@Override
			public Class<?> getRecord() {
				return Instances.class;
			}
		};

		this.instanceBuffer = new GLBuffer();

		ByteBuffer dummy = MemoryUtil.memAlloc(RendererConfig.getMaxDrawElements() * 80);
		instanceBuffer.bufferData(dummy, Usage.STREAM_DRAW);
		MemoryUtil.memFree(dummy);

		instanceBuffer.bindBase(GL_SHADER_STORAGE_BUFFER, 9);
	}

	public void updateInstanceBuffer(GLRenderer renderer) {
		int index = 0;
		for (Model model : StaticModelComponent.getAllModels()) {
			if (model.isAnimated()) continue;
			for (GLRenderer.MeshDrawData meshDrawData : model.getMeshDrawData()) {
				for (Entity object : model.getSceneObjects()) {
					int base = instanceBufferStruct.getStride() * index;
					instanceBuffer.bufferSubData(instanceBufferStruct.getOffset(0) + base, ((TransformComponent) object.getComponent(TransformComponent.class)).getTransformationMatrix());
					instanceBuffer.bufferSubData(instanceBufferStruct.getOffset(1) + base, meshDrawData.materialIdx());
					index++;

				}
			}
		}


	}

	public static record Instances(Matrix4f modelMatrix, int materialIndex) {}

}
