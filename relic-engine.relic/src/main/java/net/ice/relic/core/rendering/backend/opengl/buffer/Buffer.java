package net.ice.relic.core.rendering.backend.opengl.buffer;

import net.ice.curio.graphics.memory.Fence;
import net.ice.curio.graphics.memory.Struct;
import net.ice.curio.library.opengl.object.GLBuffer;
import net.ice.curio.library.opengl.object.GLFence;

import static org.lwjgl.opengl.GL30.GL_MAP_WRITE_BIT;
import static org.lwjgl.opengl.GL44.GL_MAP_COHERENT_BIT;
import static org.lwjgl.opengl.GL44.GL_MAP_PERSISTENT_BIT;

public abstract class Buffer {

	protected final Struct struct;

	protected GLBuffer buffer;
	protected Fence fence;

	public abstract void update();

	public Buffer(Struct struct) {
		this.struct = struct;
	}

	public void init() {
		if(buffer != null) {
			buffer.cleanup();
		}

		this.fence = new GLFence();

		this.buffer = new GLBuffer(
				(long) 10000 * struct.getStride(),
				GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT
		);
	}

	public void sync() {
		fence.sync();
	}
}
