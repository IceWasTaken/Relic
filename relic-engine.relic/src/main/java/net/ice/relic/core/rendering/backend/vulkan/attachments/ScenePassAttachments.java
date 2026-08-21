package net.ice.relic.core.rendering.backend.vulkan.attachments;

import static org.lwjgl.vulkan.VK10.VK_FORMAT_D32_SFLOAT;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R16G16B16A16_SFLOAT;

public class ScenePassAttachments {

	public static final int COLOR_FORMAT = VK_FORMAT_R16G16B16A16_SFLOAT;
	public static final int DEPTH_FORMAT = VK_FORMAT_D32_SFLOAT;

//	private final List<Attachment> colorAttachments;
//	private final Attachment depthAttachment;
//	private final int width;
//	private final int height;

	public ScenePassAttachments() {

	}

}
