package net.ice.relic.core.rendering.backend.opengl.buffer;

import net.ice.curio.graphics.memory.Fence;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.curio.library.opengl.object.GLFence;
import net.ice.curio.library.opengl.object.VertexArrayObject;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.model.Mesh;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.rendering.backend.opengl.BufferManager;
import org.tinylog.Logger;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL30.GL_MAP_WRITE_BIT;
import static org.lwjgl.opengl.GL44.GL_MAP_COHERENT_BIT;
import static org.lwjgl.opengl.GL44.GL_MAP_PERSISTENT_BIT;

public class VertexIndexArrayBuffer implements Lifecycle {

	private VertexArrayObject staticArrayObject;
	private GLBuffer vertexBuffer;
	private GLBuffer indexBuffer;

	private Fence fence;

	private int indexPos = 0;
	private int vertexPos = 0;
	private int offset = 0;

	public VertexIndexArrayBuffer() {}

	@Override
	public void init() {
		this.staticArrayObject = new VertexArrayObject();
		//1,000,000 vertices to start (56mb)
		this.vertexBuffer = new GLBuffer(1_000_000 * 56, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);
		this.indexBuffer = new GLBuffer(1_000_000 * 56, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);

		setupVAOAttributes();

		this.fence = new GLFence();
	}

	public void bind() {
		staticArrayObject.bind();
	}

	//loads a mesh into both buffers
	public BufferManager.VertexBufferInstanceInfo loadMesh(Mesh mesh) {
		fence.waitSync();

		int meshVertexCount = mesh.getVertexPositions().length / 3;

		vertexPos += meshVertexCount;
		indexPos += mesh.getIndices().length;
		offset = vertexPos;

		BufferManager.VertexBufferInstanceInfo meshInfo = new BufferManager.VertexBufferInstanceInfo(
				mesh.getIndices().length,
				indexPos,
				offset
		);

		mesh.populateBufferWithMesh(vertexBuffer);
		indexBuffer.putInt(mesh.getIndices());

		return meshInfo;
	}


	public void sync() {
		fence.sync();
	}

	public void resizeIfNeeded(Model model) {
		long remaining = vertexBuffer.getSize() - vertexBuffer.getUsed();
		if(model.getModelInfo().modelSize() > remaining) {
			vertexBuffer.resize();
			indexBuffer.resize();
		}
	}


	private void setupVAOAttributes() {
		staticArrayObject.vertexBuffer(0, vertexBuffer, 0, 56);

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

		staticArrayObject.elementBuffer(indexBuffer);
	}


}
