package net.ice.relic.core.rendering.backend.opengl.buffer;

import net.ice.curio.graphics.memory.Fence;
import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.curio.library.opengl.object.GLFence;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.ecs.component.components.rendering.model.StaticModelComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.Mesh;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.opengl.BufferManager;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.GL_MAP_WRITE_BIT;
import static org.lwjgl.opengl.GL40.GL_DRAW_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL44.GL_MAP_COHERENT_BIT;
import static org.lwjgl.opengl.GL44.GL_MAP_PERSISTENT_BIT;

public class StaticCommandBuffer implements Lifecycle {

	private List<DrawCommand> commands = new ArrayList<>();

	private GLBuffer staticCommandBuffer;
	private Struct staticCommandBufferStruct;
	private Fence fence;

	private int staticDrawCount = 0;

	@Override
	public void init() {
		if(staticCommandBuffer != null) {
			staticCommandBuffer.cleanup();
		}

		this.fence = new GLFence();
		this.staticCommandBufferStruct = new Struct(StructType.RAW) {
			@Override
			public Class<?> getRecord() {
				return DrawCommand.class;
			}
		};

		//10,000 commands to start
		this.staticCommandBuffer = new GLBuffer((long) 10000 * staticCommandBufferStruct.getStride(), GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);
	}

	public void newCommand(BufferManager.InstanceInfo meshInfo, Model model) {
		int count = meshInfo.vertexBufferInstanceInfo().indexCount();
		int instanceCount = model.getInstanceCount();
		int firstIndex = meshInfo.vertexBufferInstanceInfo().firstIndexIndex();
		int baseVertex = meshInfo.vertexBufferInstanceInfo().vertexOffset();

		putCommand(new DrawCommand(
				count,
				instanceCount,
				firstIndex,
				baseVertex,
				meshInfo.baseInstance()
		));
		staticDrawCount++;
	}

	private void putCommand(DrawCommand command) {
		commands.add(command);
		staticCommandBuffer.putInt(command.indexCount);
		staticCommandBuffer.putInt(command.instanceCount);
		staticCommandBuffer.putInt(command.firstIndex);
		staticCommandBuffer.putInt(command.baseVertex);
		staticCommandBuffer.putInt(command.baseInstance);
	}


	public void updateStaticCommandBuffer(GLRenderer renderer) {
		fence.waitSync();
	}

	public void bind() {
		staticCommandBuffer.bind(GL_DRAW_INDIRECT_BUFFER);
	}

	public void sync() {
		fence.sync();
	}

	public int getStaticDrawCount() {
		return staticDrawCount;
	}

	public record DrawCommand(
			int indexCount, //amount of indices to draw
			int instanceCount, //number of instances to draw
			int firstIndex, //first index in element buffer
			int baseVertex, //value added to each index before reading to array
			int baseInstance //base instance id
	) {}
}
