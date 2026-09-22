package net.ice.curio.library.vulkan.object;

import net.ice.curio.graphics.context.GraphicsContext;
import net.ice.curio.graphics.object.pipeline.shader.Shader;
import net.ice.curio.graphics.object.pipeline.shader.ShaderType;
import net.ice.curio.library.vulkan.VulkanContext;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;

import java.io.File;
import java.io.IOException;
import java.nio.LongBuffer;
import java.nio.file.Files;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.VK10.vkCreateShaderModule;
import static org.lwjgl.vulkan.VK10.vkDestroyShaderModule;

public class VkShader extends Shader {

	private final long handle;

	public VkShader(GraphicsContext context, ShaderType type, String spvFile) {
		super(context, type);

		try(MemoryStack stack = MemoryStack.stackPush()) {
			byte[] fileBytes = Files.readAllBytes(new File(spvFile).toPath());

			VkShaderModuleCreateInfo moduleCreateInfo = VkShaderModuleCreateInfo.calloc(stack)
					.sType$Default()
					.pCode(stack.malloc(fileBytes.length).put(fileBytes).flip());

			LongBuffer handle = stack.mallocLong(1);

			checkVulkan(
					vkCreateShaderModule(
							((VulkanContext) context).getDevice().getVkDevice(),
							moduleCreateInfo,
							null,
							handle
					),
					"[VkShader] Failed to create shader module"
			);

			this.handle = handle.get(0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void cleanup(VulkanContext context) {
		vkDestroyShaderModule(context.getDevice().getVkDevice(), handle, null);
	}

	long getHandle() {
		return handle;
	}
}
