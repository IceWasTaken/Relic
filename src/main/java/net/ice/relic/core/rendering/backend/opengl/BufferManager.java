package net.ice.relic.core.rendering.backend.opengl;

import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.ecs.component.components.rendering.model.StaticModelComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.Mesh;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.opengl.buffer.*;

import java.util.*;

public class BufferManager implements Lifecycle {

	private int baseInstance = 0;

	private final InstanceBuffer instanceBuffer;
	private final StaticCommandBuffer staticCommandBuffer;
	private final AnimatedCommandBuffer animatedCommandBuffer;
	private final MaterialMapBuffer materialMapBuffer;
	private final VertexIndexArrayBuffer vertexIndexArrayBuffer;

	public static final Deque<Entity> entityLoadingQueue = new ArrayDeque<>();

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
				loadModel(entity, model);
			}

		}

		instanceBuffer.update();
		materialMapBuffer.update(glRenderer.getApplication().getMaterialCache());
	}

	public void loadEntity(Entity entity) {
		entityLoadingQueue.add(entity);
	}

	private void loadModel(Entity entity, Model model) {
		int i = 0;
		if(!entityLoadingQueue.isEmpty()) {
			for(Mesh mesh : model.getMeshes()) {
				VertexBufferInstanceInfo meshInfo = vertexIndexArrayBuffer.loadMesh(mesh);

				InstanceInfo instanceInfo = new InstanceInfo(
						entity,
						mesh,
						meshInfo,
						baseInstance,
						i
				);

				staticCommandBuffer.newCommand(
						instanceInfo,
						model
				);

				instanceBuffer.newInstance(
						new InstanceBuffer.Instance(
								entity,
								mesh
						)
				);
				baseInstance += model.getInstanceCount();
				i++;
			}
		}
	}

	public StaticCommandBuffer getStaticCommandBuffer() {
		return staticCommandBuffer;
	}

	public VertexIndexArrayBuffer getVertexIndexArrayBuffer() {
		return vertexIndexArrayBuffer;
	}

	public record InstanceInfo(
		Entity associatedEntity,
		Mesh mesh,
		VertexBufferInstanceInfo vertexBufferInstanceInfo,
		int baseInstance,
		int drawCommandIndex
	){}

	public record VertexBufferInstanceInfo(
			int indexCount,
			int firstIndexIndex, //lmao
			int vertexOffset
	) {}
}
