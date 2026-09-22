package net.ice.relic.core.rendering.backend.vulkan;

import org.lwjgl.vulkan.*;
import static org.lwjgl.vulkan.VK13.*;

@Deprecated
public class VertexBufferStruct {
	public static final int TEXT_COORD_COMPONENTS = 2;
	private static final int NUMBER_OF_ATTRIBUTES = 2;
	private static final int POSITION_COMPONENTS = 3;

	private final VkPipelineVertexInputStateCreateInfo vi;
	private final VkVertexInputAttributeDescription.Buffer viAttrs;
	private final VkVertexInputBindingDescription.Buffer viBindings;

	public VertexBufferStruct() {
		viAttrs = VkVertexInputAttributeDescription.calloc(NUMBER_OF_ATTRIBUTES);
		viBindings = VkVertexInputBindingDescription.calloc(1);
		vi = VkPipelineVertexInputStateCreateInfo.calloc();

		int i = 0;
		int offset = 0;
		//pos
		viAttrs.get(i)
				.binding(0)
				.location(i)
				.format(VK_FORMAT_R32G32B32_SFLOAT)
				.offset(offset);

		//texture coord
		i++;
		offset += POSITION_COMPONENTS * 4;
		viAttrs.get(i)
				.binding(0)
				.location(i)
				.format(VK_FORMAT_R32G32_SFLOAT)
				.offset(offset);

		int stride = offset + TEXT_COORD_COMPONENTS * 4;
		viBindings.get(0)
				.binding(0)
				.stride(stride)
				.inputRate(VK_VERTEX_INPUT_RATE_VERTEX);

		vi
				.sType$Default()
				.pVertexBindingDescriptions(viBindings)
				.pVertexAttributeDescriptions(viAttrs);
	}

	public void cleanup() {
		viBindings.free();
		viAttrs.free();
		vi.free();
	}

	public VkPipelineVertexInputStateCreateInfo getVi() {
		return vi;
	}
}