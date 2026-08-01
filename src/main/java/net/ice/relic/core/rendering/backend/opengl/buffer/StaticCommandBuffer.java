package net.ice.relic.core.rendering.backend.opengl.buffer;

import net.ice.curio.graphics.memory.Fence;
import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.graphics.memory.StructType;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.curio.library.opengl.object.GLFence;
import net.ice.relic.core.ecs.component.components.rendering.model.StaticModelComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.opengl.GLRenderer;

import java.util.List;

import static org.lwjgl.opengl.GL30.GL_MAP_WRITE_BIT;
import static org.lwjgl.opengl.GL40.GL_DRAW_INDIRECT_BUFFER;
import static org.lwjgl.opengl.GL44.GL_MAP_COHERENT_BIT;
import static org.lwjgl.opengl.GL44.GL_MAP_PERSISTENT_BIT;

public class StaticCommandBuffer {

	private GLBuffer staticCommandBuffer;
	private Struct staticCommandBufferStruct;
	private Fence fence;

	private int staticDrawCount;

	public void createStaticCommandBuffer() {
		if(staticCommandBuffer != null) {
			staticCommandBuffer.destroy();
		}

		List<Model> models = StaticModelComponent.getAllModels();

		this.fence = new GLFence();
		this.staticCommandBufferStruct = new Struct(StructType.RAW) {
			@Override
			public Class<?> getRecord() {
				return DrawElementsIndirectCommandStruct.class;
			}
		};

		int meshCount = 0;
		int firstIndex = 0;
		int baseInstance = 0;

		for (Model model : models) {
			meshCount += model.getMeshDrawData().size();
		}

		this.staticCommandBuffer = new GLBuffer(
				(long) meshCount * staticCommandBufferStruct.getStride(),
				GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT
		);

		this.staticDrawCount = meshCount;

		for (Model model : models) {
			List<Entity> entities = model.getEntities();
			int entityCount = entities.size();

			for(GLRenderer.MeshDrawData meshDrawData : model.getMeshDrawData()) {
				staticCommandBuffer.putInt(meshDrawData.count());
				staticCommandBuffer.putInt(entityCount);
				staticCommandBuffer.putInt(firstIndex);
				staticCommandBuffer.putInt(meshDrawData.offset()); //baseVertex
				staticCommandBuffer.putInt(baseInstance);

				firstIndex += meshDrawData.count();
				baseInstance += entityCount;
			}
		}
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

	public record DrawElementsIndirectCommandStruct(
			int count,
			int instanceCount,
			int firstIndex,
			int baseVertex,
			int baseInstance
	) {}
}
