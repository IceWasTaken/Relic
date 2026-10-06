package net.ice.curio.graphics.memory;

public abstract class Fence {

	public abstract void waitSync();
	public abstract void sync();

	public Fence() {

	}
}
