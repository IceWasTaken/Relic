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

	private int staticVertexPos = 0;
	private int staticIndexPos = 0;

	private int totalBindingPosesVertices = 0;

	private VertexArrayObject staticArrayObject;
	private GLBuffer staticVertexBuffer;
	private GLBuffer staticIndexBuffer;

	private VertexArrayObject animatedArrayObject;
	private GLBuffer bindingPoseBuffer;
	private GLBuffer bonesMatricesBuffer;
	private GLBuffer bonesIndicesWeightsBuffer;
	private GLBuffer destinationAnimationBuffer;

	private Fence fence;

	public MeshBuffer() {}

	@Override
	public void init() {
		this.staticArrayObject = new VertexArrayObject();
		this.animatedArrayObject = new VertexArrayObject();
		//1,000,000 vertices to start (56mb)
		this.staticVertexBuffer = new GLBuffer(1_000_000 * 56, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);
		this.staticIndexBuffer = new GLBuffer(1_000_000 * 56, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);

		this.bindingPoseBuffer = new GLBuffer(1_000_000 * 56, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);
		this.bonesMatricesBuffer = new GLBuffer(1_000_000 * 56, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);
		this.bonesIndicesWeightsBuffer = new GLBuffer(1_000_000 * 56, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);
		this.destinationAnimationBuffer = new GLBuffer(1_000_000 * 56, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);

		setupAttributes(staticArrayObject, staticVertexBuffer, staticIndexBuffer);

		this.fence = new GLFence();
	}

	public void bind() {
		staticArrayObject.bind();
	}

	//loads a mesh into both buffers
	public Mesh loadMesh(MeshData mesh, boolean animated) {
		if(animated) {
			return loadAnimatedMesh(mesh);
		}
		return loadStaticMesh(mesh);
	}

	private Mesh loadStaticMesh(MeshData mesh) {
		fence.waitSync();

		int count = mesh.getIndices().length;
		int currVertexPos = staticVertexPos;
		int currIndexPos = staticIndexPos;

		mesh.populateBufferWithMesh(staticVertexBuffer);
		staticIndexBuffer.putInt(mesh.getIndices());

		staticVertexPos += mesh.getPositions().length;
		staticIndexPos += mesh.getIndices().length;

		return new Mesh(mesh, currVertexPos, currIndexPos, count);
	}

	private Mesh loadAnimatedMesh(MeshData mesh) {
		fence.waitSync();

		mesh.populateBufferWithMesh(bindingPoseBuffer);

		int count = mesh.getIndices().length;
		int currVertexPos = staticVertexPos;
		int currIndexPos = staticIndexPos;


		return new Mesh(mesh, 0, 0, 0);
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


	private void setupAttributes(VertexArrayObject vertexArrayObject, GLBuffer vertexBuffer, GLBuffer indexBuffer) {
		vertexArrayObject.vertexBuffer(0, vertexBuffer, 0, 56);

		vertexArrayObject.attributeFormat(0, 3, GL_FLOAT, false, 0);
		vertexArrayObject.attributeFormat(1, 3, GL_FLOAT, false, 12);
		vertexArrayObject.attributeFormat(2, 3, GL_FLOAT, false, 24);
		vertexArrayObject.attributeFormat(3, 3, GL_FLOAT, false, 36);
		vertexArrayObject.attributeFormat(4, 2, GL_FLOAT, false, 48);

		vertexArrayObject.attributeBinding(0, 0);
		vertexArrayObject.attributeBinding(1, 0);
		vertexArrayObject.attributeBinding(2, 0);
		vertexArrayObject.attributeBinding(3, 0);
		vertexArrayObject.attributeBinding(4, 0);

		vertexArrayObject.enableAttribute(0);
		vertexArrayObject.enableAttribute(1);
		vertexArrayObject.enableAttribute(2);
		vertexArrayObject.enableAttribute(3);
		vertexArrayObject.enableAttribute(4);

		vertexArrayObject.elementBuffer(indexBuffer);
	}
}
