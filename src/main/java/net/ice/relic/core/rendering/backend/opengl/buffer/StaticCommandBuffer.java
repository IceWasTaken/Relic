package net.ice.relic.core.rendering.backend.opengl.buffer;

import net.ice.curio.graphics.memory.Fence;
import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.curio.library.opengl.object.GLFence;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.model.mesh.Mesh;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.GL_MAP_WRITE_BIT;
import static org.lwjgl.opengl.GL40.GL_DRAW_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL44.GL_MAP_COHERENT_BIT;
import static org.lwjgl.opengl.GL44.GL_MAP_PERSISTENT_BIT;

public class StaticCommandBuffer implements Lifecycle {

	private final List<DrawCommand> commands = new ArrayList<>();

	private final Struct staticCommandBufferStruct;

	private GLBuffer staticCommandBuffer;
	private Fence fence;

	public StaticCommandBuffer() {
		this.staticCommandBufferStruct = new Struct(StructType.RAW) {
			@Override
			public Class<?> getRecord() {
				return DrawCommandStruct.class;
			}
		};
	}

	@Override
	public void init() {
		if(staticCommandBuffer != null) {
			staticCommandBuffer.cleanup();
		}

		this.fence = new GLFence();

		//10,000 commands to start
		this.staticCommandBuffer = new GLBuffer(
				(long) 10000 * staticCommandBufferStruct.getStride(),
				GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT
		);
	}

	public void update() {
		fence.waitSync();

		int baseInstance = 0;
		staticCommandBuffer.position(0);
		for(DrawCommand drawCommand : commands) {
			staticCommandBuffer.putInt(drawCommand.getMesh().getCount());
			staticCommandBuffer.putInt(drawCommand.instanceCount);
			staticCommandBuffer.putInt(drawCommand.getMesh().getIndexStartPos());
			staticCommandBuffer.putInt(drawCommand.getMesh().getVertexStartPos() / 3);
			staticCommandBuffer.putInt(baseInstance);

			baseInstance += drawCommand.instanceCount;
		}
	}

	public void bind() {
		staticCommandBuffer.bind(GL_DRAW_INDIRECT_BUFFER);
	}

	public void sync() {
		fence.sync();
	}

	public int getStaticDrawCount() {
		return commands.size();
	}

	public static class DrawCommand {
		private final Mesh mesh;
		private int instanceCount = 0;

		public DrawCommand(StaticCommandBuffer commandBuffer, Mesh mesh) {
			this.mesh = mesh;

			commandBuffer.commands.add(this);
		}

		public void newInstance() {
			instanceCount++;
		}

		public void destroyInstance() {
			instanceCount--;
		}

		public Mesh getMesh() {
			return mesh;
		}
	}

	record DrawCommandStruct(
			int indexCount, //amount of indices to draw
			int instanceCount, //number of instances to draw
			int firstIndex, //first index in element buffer
			int baseVertex, //value added to each index before reading to array
			int baseInstance //base instance id
	) {}
}
