package net.ice.relic.core.rendering.backend.vulkan.shader;

import net.ice.relic.core.interfaces.Cleanable;
import net.ice.relic.core.rendering.backend.vulkan.device.Device;
import net.ice.relic.core.rendering.shader.ShaderType;
import net.ice.relic.core.resource.Resource;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import static net.ice.relic.core.rendering.backend.vulkan.VulkanUtil.checkVulkan;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanShader implements Cleanable {

    private final long shaderHandle;

    private final Device device;
    private final ShaderType type;

    public VulkanShader(Device device, Resource resource, ShaderType type) {
        this.type = type;
        this.device = device;

        ByteBuffer fileData = resource.load();
        this.shaderHandle = createShader(device, fileData);

    }

    private long createShader(Device device, ByteBuffer data) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkShaderModuleCreateInfo moduleCreateInfo = VkShaderModuleCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
                    .pCode(data);

            LongBuffer handleBuffer = stack.mallocLong(1);
            checkVulkan(vkCreateShaderModule(device.getDevice(), moduleCreateInfo, null, handleBuffer), "Vulkan: Failed to create shader module.");

            return handleBuffer.get(0);
        }
    }

    @Override
    public void cleanup() {
        vkDestroyShaderModule(device.getDevice(), shaderHandle, null);
    }

    public long getShaderHandle() {
        return shaderHandle;
    }

    public ShaderType getShaderType() {
        return type;
    }
}
