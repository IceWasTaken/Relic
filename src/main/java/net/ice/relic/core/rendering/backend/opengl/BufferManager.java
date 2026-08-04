package net.ice.relic.core.rendering.backend.opengl;

import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.ecs.component.components.rendering.model.StaticModelComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.Mesh;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.model.render.MeshRenderInfo;
import net.ice.relic.core.model.render.ModelRenderInfo;
import net.ice.relic.core.rendering.backend.opengl.buffer.*;

import java.util.*;

public class BufferManager implements Lifecycle {

	private int baseInstance = 0;

	private int vertexPos = 0;
	private int indexPos = 0;

	private final InstanceBuffer instanceBuffer;
	private final StaticCommandBuffer staticCommandBuffer;
	private final AnimatedCommandBuffer animatedCommandBuffer;
	private final MaterialMapBuffer materialMapBuffer;
	private final VertexIndexArrayBuffer vertexIndexArrayBuffer;

	public static final Deque<Entity> entityLoadingQueue = new ArrayDeque<>();

	private final List<ModelRenderInfo> loadedModels = new ArrayList<>();

	private final GLRenderer glRenderer;

	public BufferManager(GLRenderer glRenderer) {
		this.glRenderer = glRenderer;
		this.instanceBuffer = new InstanceBuffer();
		this.staticCommandBuffer = new StaticCommandBuffer();
		this.animatedCommandBuffer = new AnimatedCommandBuffer();
		this.materialMapBuffer = new MaterialMapBuffer();
		this.vertexIndexArrayBuffer = new VertexIndexArrayBuffer();
	}

	@Override
	public void init() {
		instanceBuffer.init();
		staticCommandBuffer.init();
		animatedCommandBuffer.init();
		materialMapBuffer.init();
		vertexIndexArrayBuffer.init();
	}

	public void sync() {
		instanceBuffer.sync();
		staticCommandBuffer.sync();
		animatedCommandBuffer.sync();
		materialMapBuffer.sync();
		vertexIndexArrayBuffer.sync();
	}

	@Override
	public void update() {
		if(!entityLoadingQueue.isEmpty()) {
			//load only one new model per frame
			Entity entity = entityLoadingQueue.pollFirst();
			Model model;
			if((model = entity.getComponent(StaticModelComponent.class).getModel()) != null) {
				vertexIndexArrayBuffer.resizeIfNeeded(model);
				if(!model.renderInfoCheck(loadedModels)) {
					loadModel(entity, model);
				}

			}

		}

		instanceBuffer.update();
		materialMapBuffer.update(glRenderer.getApplication().getMaterialCache());
	}

	public void loadEntity(Entity entity) {
		entityLoadingQueue.add(entity);
	}

	private void loadModel(Entity entity, Model model) {
		ModelRenderInfo renderInfo = new ModelRenderInfo(model);
		InstanceInfo instanceInfo = new InstanceInfo(
				entity,
				renderInfo
		);

		int index = 0;
		for (StaticCommandBuffer.DrawCommand drawCommand : instanceInfo.modelRenderInfo.generateDrawCommands()) {
			staticCommandBuffer.newCommand(
					new StaticCommandBuffer.DrawCommand(
							drawCommand.indexCount(),
							drawCommand.instanceCount(),
							drawCommand.firstIndex(),
							drawCommand.baseVertex(),
							baseInstance
					)
			);
			baseInstance += drawCommand.instanceCount();

			instanceBuffer.newInstance(
					new InstanceBuffer.Instance(
							entity,
							instanceInfo.modelRenderInfo.getMeshes().get(index).getAssociatedMesh()
					)
			);
			index++;
		}
		loadedModels.add(renderInfo);
	}

	private ModelRenderInfo generateModelRenderInfo(Model model) {
		ModelRenderInfo modelRenderInfo = new ModelRenderInfo(model);
		for(Mesh mesh : model.getMeshes()) {
			modelRenderInfo.addMesh(new MeshRenderInfo(
					mesh,
					vertexPos,
					indexPos,
					mesh.getIndices().length
			));
			vertexPos += mesh.getVertexPositions().length / 3;
			indexPos += mesh.getIndices().length;
		}
		return modelRenderInfo;
	}

	public StaticCommandBuffer getStaticCommandBuffer() {
		return staticCommandBuffer;
	}

	public VertexIndexArrayBuffer getVertexIndexArrayBuffer() {
		return vertexIndexArrayBuffer;
	}

	public record InstanceInfo(
		Entity associatedEntity,
		ModelRenderInfo modelRenderInfo
	){}
}
