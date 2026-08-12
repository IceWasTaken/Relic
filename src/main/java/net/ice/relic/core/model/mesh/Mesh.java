package net.ice.relic.core.model.mesh;

public class Mesh {

	private final MeshData meshData;

	private int vertexStartPos;
	private int indexStartPos;
	private int count;

	public Mesh(
			MeshData meshData,
			int vertexStartPos,
			int indexStartPos,
			int count
	) {
		this.meshData = meshData;
		this.vertexStartPos = vertexStartPos;
		this.indexStartPos = indexStartPos;
		this.count = count;
	}

	public int getIndexStartPos() {
		return indexStartPos;
	}

	public int getVertexStartPos() {
		return vertexStartPos;
	}

	public int getCount() {
		return count;
	}

}
