package net.ice.relic.core.rendering.backend.opengl;

import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.ecs.component.components.rendering.model.StaticModelComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.mesh.Mesh;
import net.ice.relic.core.model.mesh.MeshData;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.opengl.buffer.*;
import org.tinylog.Logger;

import java.util.*;

public class BufferManager implements Lifecycle {

	private final InstanceBuffer instanceBuffer;
	private final StaticCommandBuffer staticCommandBuffer;
	private final AnimatedCommandBuffer animatedCommandBuffer;
	private final MaterialMapBuffer materialMapBuffer;
	private final MeshBuffer meshBuffer;

	private static final Deque<Entity> entityLoadingQueue = new ArrayDeque<>();

	private final Set<Model> loadedModels = new LinkedHashSet<>();
	private final Map<Model, LoadedModelInfo> loadedModelInfos = new HashMap<>();

	private final GLRenderer glRenderer;

	public BufferManager(GLRenderer glRenderer) {
		this.glRenderer = glRenderer;
		this.instanceBuffer = new InstanceBuffer(this);
		this.staticCommandBuffer = new StaticCommandBuffer();
		this.animatedCommandBuffer = new AnimatedCommandBuffer();
		this.materialMapBuffer = new MaterialMapBuffer();
		this.meshBuffer = new MeshBuffer();
	}

	@Override
	public void init() {
		instanceBuffer.init();
		staticCommandBuffer.init();
		animatedCommandBuffer.init();
		materialMapBuffer.init();
		meshBuffer.init();
	}

	public void sync() {
		instanceBuffer.sync();
		staticCommandBuffer.sync();
		animatedCommandBuffer.sync();
		materialMapBuffer.sync();
		meshBuffer.sync();
	}

	@Override
	public void update() {
		if(!entityLoadingQueue.isEmpty()) {
			Entity entity = entityLoadingQueue.pollFirst();

			Logger.debug("[BufferManager]: Loading entity {}", entity.getName());

			Model model;
			if((model = entity.getComponent(StaticModelComponent.class).getModel()) != null) {
				meshBuffer.resizeIfNeeded(model);
				if (!loadedModels.contains(model)) {
					loadedModelInfos.put(model, loadModel(model));
				}
				loadedModelInfos.get(model).newInstance();
			}
		}

		staticCommandBuffer.update();
		instanceBuffer.update();
		materialMapBuffer.update(glRenderer.getApplication().getMaterialCache());
	}

	private LoadedModelInfo loadModel(Model model) {
		List<MeshData> meshes = model.getMeshData();
		List<Mesh> bufferedMeshes = new ArrayList<>();

		for (MeshData mesh : meshes) {
			bufferedMeshes.add(meshBuffer.loadMesh(mesh));
		}

		loadedModels.add(model);

		return new LoadedModelInfo(staticCommandBuffer, bufferedMeshes);
	}

	public void loadEntity(Entity entity) {
		entityLoadingQueue.add(entity);
	}

	public StaticCommandBuffer getStaticCommandBuffer() {
		return staticCommandBuffer;
	}

	public MeshBuffer getMeshBuffer() {
		return meshBuffer;
	}

	public Set<Model> getLoadedModels() {
		return loadedModels;
	}

	public static class LoadedModelInfo {
		private final List<StaticCommandBuffer.DrawCommand> drawCommands;

		public LoadedModelInfo(StaticCommandBuffer commandBuffer, List<Mesh> meshes) {
			this.drawCommands = new ArrayList<>();

			for (Mesh meshInfo : meshes) {
				drawCommands.add(new StaticCommandBuffer.DrawCommand(commandBuffer, meshInfo));
			}
		}

		public void newInstance() {
			for(StaticCommandBuffer.DrawCommand drawCommand : drawCommands) {
				drawCommand.newInstance();
			}
		}
	}
}
