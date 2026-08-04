package net.ice.relic.core.rendering.backend.opengl.buffer;

import net.ice.curio.graphics.memory.Fence;
import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.curio.library.opengl.object.GLFence;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.ecs.component.components.TransformComponent;
import net.ice.relic.core.ecs.component.components.rendering.model.StaticModelComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.Mesh;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;
import org.joml.Matrix4f;

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

	private final List<Instance> drawInstances;

	public InstanceBuffer() {
		this.drawInstances = new ArrayList<>();
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
				instanceBufferStruct.getStride() * 500L,
				GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT
		).bind(GL_SHADER_STORAGE_BUFFER, 9);

		this.fence = new GLFence();
	}

	public void newInstance(Instance meshDrawInstance) {
		drawInstances.add(meshDrawInstance);
	}

	public void newInstance(int index, Instance meshDrawInstance) {
		drawInstances.add(index, meshDrawInstance);
	}

	@Override
	public void update() {
		int index = 0;

		fence.waitSync();

		for(Instance instance : drawInstances) {
			int base = instanceBufferStruct.getStride() * index;
			Entity entity = instance.associatedEntity;
			Mesh mesh = instance.associatedMesh;
			instanceBuffer.putMatrix4f(instanceBufferStruct.getOffset(0) + base, entity.getComponent(TransformComponent.class).getTransformationMatrix());
			instanceBuffer.putInt(instanceBufferStruct.getOffset(1) + base, mesh.getMaterialIndex());
			index++;
		}
	}

	public void sync() {
		fence.sync();
	}

	public record Instance(Entity associatedEntity, Mesh associatedMesh) {}
	public record InstanceStruct(Matrix4f transformIndex, int materialIndex) {}
}
