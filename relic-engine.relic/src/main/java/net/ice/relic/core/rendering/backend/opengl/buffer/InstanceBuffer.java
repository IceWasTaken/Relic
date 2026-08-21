package net.ice.relic.core.rendering.backend.opengl.buffer;

import net.ice.curio.graphics.memory.Fence;
import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.curio.library.opengl.object.GLFence;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.ecs.component.components.TransformComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.mesh.MeshData;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.opengl.BufferManager;
import org.joml.Matrix4f;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.GL_MAP_WRITE_BIT;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL44.GL_MAP_COHERENT_BIT;
import static org.lwjgl.opengl.GL44.GL_MAP_PERSISTENT_BIT;

public class InstanceBuffer implements Lifecycle {

	private GLBuffer instanceBuffer;
	private Struct instanceBufferStruct;
	private Fence fence;

	private final BufferManager bufferManager;

	public InstanceBuffer(BufferManager bufferManager) {
		this.bufferManager = bufferManager;

	}

	@Override
	public void init() {
		this.instanceBufferStruct = new Struct(StructType.STD430) {
			@Override
			public Class<?> getRecord() {
				return InstanceStruct.class;
			}
		};

		this.instanceBuffer = new GLBuffer(
				instanceBufferStruct.getStride() * 5000L,
				GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT
		).bind(GL_SHADER_STORAGE_BUFFER, 7);

		this.fence = new GLFence();

		Logger.debug("[InstanceBuffer]: Created and bound InstanceBuffer to index 7");
	}

	@Override
	public void update() {
		int index = 0;

		fence.waitSync();

		for(Model model : bufferManager.getLoadedModels()) {
			for(MeshData mesh : model.getMeshData()) {
				for(Entity entity : model.getEntities()) {
					int base = instanceBufferStruct.getStride() * index;
					instanceBuffer.putMatrix4f(instanceBufferStruct.getOffset(0) + base, entity.getComponent(TransformComponent.class).getTransformationMatrix());
					instanceBuffer.putInt(instanceBufferStruct.getOffset(1) + base, mesh.getMaterialIndex());
					index++;
				}
			}
		}
	}


	public void sync() {
		fence.sync();
	}

	public record InstanceStruct(Matrix4f transformIndex, int materialIndex) {}
}
