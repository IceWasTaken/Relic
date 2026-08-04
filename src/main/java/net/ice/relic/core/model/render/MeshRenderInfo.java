package net.ice.relic.core.model.render;

import net.ice.relic.core.model.Mesh;

public class MeshRenderInfo {

	private final Mesh associatedMesh;
	private final int firstVertexPos;
	private final int firstIndexPos;
	private final int indexCount;

	public MeshRenderInfo(
			Mesh mesh,
			int firstVertexPos,
			int firstIndexPos,
			int indexCount
	) {
		this.associatedMesh = mesh;
		this.firstVertexPos = firstVertexPos;
		this.firstIndexPos = firstIndexPos;
		this.indexCount = indexCount;
	}

	public int getIndexCount() {
		return indexCount;
	}

	public int getFirstIndexPos() {
		return firstIndexPos;
	}

	public int getFirstVertexPos() {
		return firstVertexPos;
	}

	public Mesh getAssociatedMesh() {
		return associatedMesh;
	}
}
