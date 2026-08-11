package net.ice.relic.core.rendering.backend.opengl;

import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.ecs.component.components.rendering.ModelComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.mesh.Mesh;
import net.ice.relic.core.model.mesh.MeshData;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.opengl.buffer.*;
import org.tinylog.Logger;

import java.util.*;

public class BufferManager implements Lifecycle {

	private final InstanceBuffer instanceBuffer;
	private final GLCommandBuffer staticCommandBuffer;
	private final GLCommandBuffer animatedCommandBuffer;
	private final MaterialMapBuffer materialMapBuffer;
	private final MeshBuffer meshBuffer;

	private static final Deque<Entity> entityLoadingQueue = new ArrayDeque<>();

	private final Set<Model> loadedModels = new LinkedHashSet<>();
	private final Map<Model, LoadedModelInfo> loadedModelInfos = new HashMap<>();

	private final GLRenderer glRenderer;

	public BufferManager(GLRenderer glRenderer) {
		this.glRenderer = glRenderer;
		this.instanceBuffer = new InstanceBuffer(this);
		this.staticCommandBuffer = new GLCommandBuffer();
		this.animatedCommandBuffer = new GLCommandBuffer();
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
			if((model = entity.getComponent(ModelComponent.class).getModel()) != null) {
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
			bufferedMeshes.add(meshBuffer.loadMesh(mesh, model.isAnimated()));
		}

		loadedModels.add(model);

		return new LoadedModelInfo(staticCommandBuffer, bufferedMeshes);
	}

	public void loadEntity(Entity entity) {
		entityLoadingQueue.add(entity);
	}

	public GLCommandBuffer getStaticCommandBuffer() {
		return staticCommandBuffer;
	}

	public MeshBuffer getMeshBuffer() {
		return meshBuffer;
	}

	public Set<Model> getLoadedModels() {
		return loadedModels;
	}

	public static class LoadedModelInfo {
		private final List<GLCommandBuffer.DrawCommand> drawCommands;

		public LoadedModelInfo(GLCommandBuffer commandBuffer, List<Mesh> meshes) {
			this.drawCommands = new ArrayList<>();

			for (Mesh meshInfo : meshes) {
				drawCommands.add(new GLCommandBuffer.DrawCommand(commandBuffer, meshInfo));
			}
		}

		public void newInstance() {
			for(GLCommandBuffer.DrawCommand drawCommand : drawCommands) {
				drawCommand.newInstance();
			}
		}
	}
}
