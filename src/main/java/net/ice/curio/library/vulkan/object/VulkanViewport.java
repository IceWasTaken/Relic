package net.ice.curio.library.vulkan.object;

import net.ice.curio.graphics.object.Viewport;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkViewport;

public class VulkanViewport extends Viewport {

	private VkViewport.Buffer vkViewport;

	public VulkanViewport(int width, int height) {
		super(width, height);
		allocateViewport(width, height);
	}

	@Override
	public void bind() {
		//vkCmdSetViewport();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		cleanup();
		allocateViewport(width, height);
	}

	@Override
	public void cleanup() {
		if(vkViewport != null) {
			MemoryUtil.memFree(vkViewport);
			vkViewport = null;
		}
	}

	private void allocateViewport(int width, int height) {
		this.vkViewport = VkViewport.calloc(1)
				.x(0.0f)
				.y(height)
				.width(width)
				.height(-height)
				.minDepth(MIN_DEPTH)
				.maxDepth(MAX_DEPTH);
	}

	public VkViewport.Buffer getVkViewport() {
		return vkViewport;
	}
}
