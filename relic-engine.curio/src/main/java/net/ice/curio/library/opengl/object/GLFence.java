package net.ice.curio.library.opengl.object;

import net.ice.curio.graphics.memory.Fence;

import static org.lwjgl.opengl.GL32.*;

public final class GLFence extends Fence {

	private long handle = 0;

	public GLFence() {

	}

	@Override
	public void waitSync() {
		if(handle != 0) {
			glClientWaitSync(handle, GL_SYNC_FLUSH_COMMANDS_BIT, GL_TIMEOUT_IGNORED);
			glDeleteSync(handle);
		}
	}

	@Override
	public void sync() {
		this.handle = glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
	}
}
