package net.ice.relic.core.rendering.backend.opengl.buffer;

import net.ice.curio.graphics.memory.Fence;
import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.curio.library.opengl.object.GLFence;
import net.ice.relic.core.ecs.component.components.TransformComponent;
import net.ice.relic.core.ecs.component.components.rendering.model.StaticModelComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL30.GL_MAP_WRITE_BIT;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL44.GL_MAP_COHERENT_BIT;
import static org.lwjgl.opengl.GL44.GL_MAP_PERSISTENT_BIT;

public class InstanceBuffer {

	private GLBuffer instanceBuffer;
	private Struct instanceBufferStruct;
	private Fence fence;

	public void createInstanceBuffer() {
		this.instanceBufferStruct = new Struct(StructType.STD430) {
			@Override
			public Class<?> getRecord() {
				return Instances.class;
			}
		};

		this.instanceBuffer = new GLBuffer(
				instanceBufferStruct.getStride() * 200L,
				GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT
		);

		this.fence = new GLFence();

		instanceBuffer.bindBase(GL_SHADER_STORAGE_BUFFER, 9);
	}

	public void updateInstanceBuffer(GLRenderer renderer) {
		int index = 0;

		fence.waitSync();

		for (Model model : StaticModelComponent.getAllModels()) {
			if (model.isAnimated()) continue;
			for (GLRenderer.MeshDrawData meshDrawData : model.getMeshDrawData()) {
				for (Entity object : model.getEntities()) {
					int base = instanceBufferStruct.getStride() * index;
					instanceBuffer.putMatrix4f(instanceBufferStruct.getOffset(0) + base, object.getComponent(TransformComponent.class).getTransformationMatrix());
					instanceBuffer.putInt(instanceBufferStruct.getOffset(1) + base, meshDrawData.materialIdx());
					index++;
				}
			}
		}
	}

	public void sync() {
		fence.sync();
	}

	public static record Instances(Matrix4f modelMatrix, int materialIndex) {}
}
