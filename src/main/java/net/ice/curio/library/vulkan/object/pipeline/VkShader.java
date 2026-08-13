package net.ice.curio.library.vulkan.object.pipeline;

import net.ice.curio.graphics.object.pipeline.shader.Shader;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

public class VkShader extends Shader<Long> {

	public VkShader() {
		super();
	}

	@Override
	protected Long createShader() {
		try(MemoryStack stack = MemoryStack.stackPush()) {
//			ByteBuffer shaderCode = stack.malloc()
		}
		return 0l;
	}

	@Override
	protected String getShadersDirPrefix() {
		return "vulkan";
	}
}
