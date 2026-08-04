package net.ice.relic.core.model.render;

import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.opengl.buffer.StaticCommandBuffer;

import java.util.ArrayList;
import java.util.List;

public class ModelRenderInfo {

	private int instanceCount;
	private final Model associatedModel;
	private final List<MeshRenderInfo> meshes;

	public ModelRenderInfo(Model model) {
		this.associatedModel = model;
		this.meshes = new ArrayList<>();
	}

	public void newInstance() {
		instanceCount++;
	}

	public void addMesh(MeshRenderInfo renderInfo) {
		meshes.add(renderInfo);
	}

	public List<StaticCommandBuffer.DrawCommand> generateDrawCommands() {
		List<StaticCommandBuffer.DrawCommand> commands = new ArrayList<>();
		for (MeshRenderInfo mesh : meshes) {
			commands.add(new StaticCommandBuffer.DrawCommand(
					mesh.getIndexCount(),
					instanceCount,
					mesh.getFirstIndexPos(),
					mesh.getFirstVertexPos(),
					0
			));
		}

		return commands;
	}

	public List<MeshRenderInfo> getMeshes() {
		return meshes;
	}

	public Model getAssociatedModel() {
		return associatedModel;
	}
}
