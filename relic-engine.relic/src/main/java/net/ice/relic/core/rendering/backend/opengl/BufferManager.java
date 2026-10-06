package net.ice.relic.core.rendering.backend.opengl;

import net.ice.relic.core.ecs.component.components.rendering.ModelComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.mesh.Mesh;
import net.ice.relic.core.model.mesh.MeshData;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.opengl.buffer.*;
import org.tinylog.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class BufferManager {

	private final InstanceBuffer instanceBuffer;
	private final GLCommandBuffer staticCommandBuffer;
	private final GLCommandBuffer animatedCommandBuffer;
	private final SceneInfoBuffer sceneInfoBuffer;
	private final MeshBuffer meshBuffer;

	private static final Deque<Entity> entityLoadingQueue = new ArrayDeque<>();

	private final Set<Model> loadedModels = new LinkedHashSet<>();
	private final Map<Model, BufferedModel> loadedModelInfos = new HashMap<>();

	private final GLRenderer glRenderer;

	public BufferManager(GLRenderer glRenderer) {
		this.glRenderer = glRenderer;
		this.instanceBuffer = new InstanceBuffer(this);
		this.staticCommandBuffer = new GLCommandBuffer();
		this.animatedCommandBuffer = new GLCommandBuffer();
		this.sceneInfoBuffer = new SceneInfoBuffer();
		this.meshBuffer = new MeshBuffer();
	}

	public void init() {
		instanceBuffer.init();
		staticCommandBuffer.init();
		animatedCommandBuffer.init();
		sceneInfoBuffer.init();
		meshBuffer.init();
	}

	public void sync() {
		instanceBuffer.sync();
		staticCommandBuffer.sync();
		animatedCommandBuffer.sync();
		//materialMapBuffer.sync();
		sceneInfoBuffer.sync();
		meshBuffer.sync();
	}

	public void update() {
		if(!entityLoadingQueue.isEmpty()) {
			Entity entity = entityLoadingQueue.pollFirst();
			long start = System.nanoTime();

			Model model;
			if((model = entity.getComponent(ModelComponent.class).getModel()) != null) {
				meshBuffer.resizeIfNeeded(model);
				if (!loadedModels.contains(model)) {
					loadedModelInfos.put(model, loadModel(model));
				}
				loadedModelInfos.get(model).newInstance();
			}

			long time = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
			if(time <= 0) {
				Logger.debug("[BufferManager]: Entity '{}' took <{}ms to load", entity.getName(), time);
			} else {
				Logger.debug("[BufferManager]: Entity '{}' took {}ms to load", entity.getName(), time);
			}
		}

		staticCommandBuffer.update();
		sceneInfoBuffer.update(glRenderer.getApplication().getCurrentScene());
		instanceBuffer.update(glRenderer.getApplication().getMaterialCache());
	}

	private BufferedModel loadModel(Model model) {
		List<MeshData> meshes = model.getMeshData();
		List<Mesh> bufferedMeshes = new ArrayList<>();

		for (MeshData mesh : meshes) {
			bufferedMeshes.add(meshBuffer.loadMesh(mesh, model.isAnimated()));
		}

		loadedModels.add(model);

		return new BufferedModel(staticCommandBuffer, bufferedMeshes);
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

	public static class BufferedModel {
		private final List<GLCommandBuffer.DrawCommand> drawCommands;

		public BufferedModel(GLCommandBuffer commandBuffer, List<Mesh> meshes) {
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
