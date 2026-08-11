package net.ice.relic.core.rendering.backend.opengl.buffer;

import net.ice.curio.graphics.memory.Fence;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.curio.library.opengl.object.GLFence;
import net.ice.curio.library.opengl.object.VertexArrayObject;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.model.mesh.Mesh;
import net.ice.relic.core.model.mesh.MeshData;
import net.ice.relic.core.model.Model;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL30.GL_MAP_WRITE_BIT;
import static org.lwjgl.opengl.GL44.GL_MAP_COHERENT_BIT;
import static org.lwjgl.opengl.GL44.GL_MAP_PERSISTENT_BIT;

public class MeshBuffer implements Lifecycle {

	private int vertexPos = 0;
	private int indexPos = 0;

	private VertexArrayObject staticArrayObject;
	private GLBuffer staticVertexBuffer;
	private GLBuffer staticIndexBuffer;

	private VertexArrayObject animatedArrayObject;
	private GLBuffer animatedVertexBuffer;
	private GLBuffer animatedIndexBuffer;

	private Fence fence;

	public MeshBuffer() {}

	@Override
	public void init() {
		this.staticArrayObject = new VertexArrayObject();
		//1,000,000 vertices to start (56mb)
		this.staticVertexBuffer = new GLBuffer(1_000_000 * 56, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);
		this.staticIndexBuffer = new GLBuffer(1_000_000 * 56, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);

		setupAttributes();

		this.fence = new GLFence();
	}

	public void bind() {
		staticArrayObject.bind();
	}

	//loads a mesh into both buffers
	public Mesh loadMesh(MeshData mesh) {
		fence.waitSync();

		int count = mesh.getIndices().length;
		int currVertexPos = vertexPos;
		int currIndexPos = indexPos;

		mesh.populateBufferWithMesh(staticVertexBuffer);
		staticIndexBuffer.putInt(mesh.getIndices());

		vertexPos += mesh.getVertexPositions().length;
		indexPos += mesh.getIndices().length;

		return new Mesh(mesh, currVertexPos, currIndexPos, count);
	}


	public void sync() {
		fence.sync();
	}

	public void resizeIfNeeded(Model model) {
		long remaining = staticVertexBuffer.getSize() - staticVertexBuffer.getUsed();
		if(model.getModelInfo().modelSize() > remaining) {
			staticVertexBuffer.resize();
			staticIndexBuffer.resize();
		}
	}


	private void setupAttributes() {
		staticArrayObject.vertexBuffer(0, staticVertexBuffer, 0, 56);

		staticArrayObject.attributeFormat(0, 3, GL_FLOAT, false, 0);
		staticArrayObject.attributeFormat(1, 3, GL_FLOAT, false, 12);
		staticArrayObject.attributeFormat(2, 3, GL_FLOAT, false, 24);
		staticArrayObject.attributeFormat(3, 3, GL_FLOAT, false, 36);
		staticArrayObject.attributeFormat(4, 2, GL_FLOAT, false, 48);

		staticArrayObject.attributeBinding(0, 0);
		staticArrayObject.attributeBinding(1, 0);
		staticArrayObject.attributeBinding(2, 0);
		staticArrayObject.attributeBinding(3, 0);
		staticArrayObject.attributeBinding(4, 0);

		staticArrayObject.enableAttribute(0);
		staticArrayObject.enableAttribute(1);
		staticArrayObject.enableAttribute(2);
		staticArrayObject.enableAttribute(3);
		staticArrayObject.enableAttribute(4);

		staticArrayObject.elementBuffer(staticIndexBuffer);
	}
}
