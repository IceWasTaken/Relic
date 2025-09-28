package net.ice.relic.core.rendering.backend.vulkan.pipeline;

import net.ice.relic.core.rendering.backend.vulkan.shader.VulkanShader;
import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;

public class PipelineInfo {

    private final int colorFormat;
    private final VulkanShader[] shaderModules;
    private final VkPipelineVertexInputStateCreateInfo vi;

    public PipelineInfo(VulkanShader[] shaderModules, VkPipelineVertexInputStateCreateInfo vi, int colorFormat) {
        this.shaderModules = shaderModules;
        this.vi = vi;
        this.colorFormat = colorFormat;
    }

    public int getColorFormat() {
        return colorFormat;
    }

    public VulkanShader[] getShaderModules() {
        return shaderModules;
    }

    public VkPipelineVertexInputStateCreateInfo getVi() {
        return vi;
    }
}
