package net.ice.relic.core.rendering.backend.opengl.buffer;

import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;
import net.ice.curio.library.opengl.object.pipeline.GLPipeline;
import net.ice.relic.core.model.mesh.Mesh;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL40.GL_DRAW_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL43.glMultiDrawElementsIndirect;

public class GLCommandBuffer extends Buffer {

	private final List<DrawCommand> commands = new ArrayList<>();

	public GLCommandBuffer() {
		super(new Struct(StructType.RAW) {
					@Override
					public Class<?> getRecord() {
						return DrawCommandStruct.class;
					}
				});
	}

	@Override
	public void update() {
		fence.waitSync();

		int baseInstance = 0;
		buffer.position(0);
		for(DrawCommand drawCommand : commands) {
			buffer.putInt(drawCommand.getMesh().getCount());
			buffer.putInt(drawCommand.instanceCount);
			buffer.putInt(drawCommand.getMesh().getIndexStartPos());
			buffer.putInt(drawCommand.getMesh().getVertexStartPos() / 3);
			buffer.putInt(baseInstance);

			baseInstance += drawCommand.instanceCount;
		}
	}

	public void bind() {
		buffer.bind(GL_DRAW_INDIRECT_BUFFER);
	}

	public void draw(GLPipeline pipeline) {
		glMultiDrawElementsIndirect(pipeline.getMode(), GL_UNSIGNED_INT, 0, commands.size(), 0);
	}

	public static class DrawCommand {
		private final Mesh mesh;
		private int instanceCount = 0;

		public DrawCommand(GLCommandBuffer commandBuffer, Mesh mesh) {
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
