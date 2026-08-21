package net.ice.curio.graphics.memory;

import net.ice.heirloom.Lifecycle;

public abstract class Fence implements Lifecycle {

	public abstract void waitSync();
	public abstract void sync();

	public Fence() {

	}
}
