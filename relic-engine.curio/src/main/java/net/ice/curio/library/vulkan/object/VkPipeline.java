package net.ice.curio.library.vulkan.object;

import net.ice.curio.graphics.object.pipeline.Pipeline;
import net.ice.curio.graphics.object.pipeline.PrimitiveType;
import net.ice.curio.graphics.object.pipeline.depth.DepthState;
import net.ice.curio.graphics.object.pipeline.raster.RasterizationState;
import net.ice.curio.graphics.object.pipeline.shader.ShaderType;
import net.ice.curio.library.vulkan.VulkanContext;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.tinylog.Logger;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static net.ice.curio.library.vulkan.utils.VulkanUtils.checkVulkan;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK13.VK_STRUCTURE_TYPE_PIPELINE_RENDERING_CREATE_INFO;

public class VkPipeline extends Pipeline {

	private final long vkPipeline;
	private final long vkPipelineLayout;

	public VkPipeline(
			VulkanContext context,
			PrimitiveType primitiveType,
			RasterizationState rasterizationState,
			DepthState depthState,
			VkShader[] shaders,
			PushConstantRange[]  pushConstantRanges,
			VkPipelineVertexInputStateCreateInfo vkPipelineVertexInputStateCreateInfo,
			int depthFormat,
			int colorFormat
	) {
		super(context, primitiveType, rasterizationState, depthState);

		Logger.debug("[VkPipeline]: Creating new pipeline");

		try (MemoryStack stack = MemoryStack.stackPush()) {
			LongBuffer handle = stack.mallocLong(1);
			ByteBuffer main = stack.UTF8("main");

			VkShader[] vkShaders = shaders;
			int shaderCount = vkShaders.length;

			VkPipelineShaderStageCreateInfo.Buffer stages = VkPipelineShaderStageCreateInfo.calloc(shaderCount, stack);

			for (int i = 0; i < shaderCount; i++) {
				VkShader vkShader = vkShaders[i];
				stages.get(i)
						.sType$Default()
						.stage(getStage(vkShader.getType()))
						.module(vkShader.getHandle())
						.pName(main);
			}

			VkPipelineInputAssemblyStateCreateInfo assemblyStateCreateInfo = VkPipelineInputAssemblyStateCreateInfo.calloc(stack)
					.sType(VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO)
					.topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST);

			VkPipelineViewportStateCreateInfo viewportState = VkPipelineViewportStateCreateInfo.calloc(stack)
					.sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO)
					.viewportCount(1)
					.scissorCount(1);

			VkPipelineRasterizationStateCreateInfo rasterizationStateCreateInfo = VkPipelineRasterizationStateCreateInfo.calloc(stack)
					.sType(VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO)
					.polygonMode(rasterizationState.polygonMode().ordinal())
					.cullMode(rasterizationState.cullMode().ordinal())
					.frontFace(rasterizationState.frontFace().ordinal())
					.lineWidth(rasterizationState.lineWidth());

			VkPipelineMultisampleStateCreateInfo multisampleState = VkPipelineMultisampleStateCreateInfo.calloc(stack)
					.sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO)
					.rasterizationSamples(VK_SAMPLE_COUNT_1_BIT);

			VkPipelineDepthStencilStateCreateInfo depthStencilStateCreateInfo = null;
			if (depthFormat != VK_FORMAT_UNDEFINED) {
				depthStencilStateCreateInfo = VkPipelineDepthStencilStateCreateInfo.calloc(stack)
						.sType(VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO)
						.depthTestEnable(true)
						.depthWriteEnable(true)
						.depthCompareOp(VK_COMPARE_OP_LESS_OR_EQUAL)
						.depthBoundsTestEnable(true)
						.stencilTestEnable(false);
			}

			VkPipelineDynamicStateCreateInfo dynamicState = VkPipelineDynamicStateCreateInfo.calloc(stack)
					.sType(VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO)
					.pDynamicStates(stack.ints(
							VK_DYNAMIC_STATE_VIEWPORT,
							VK_DYNAMIC_STATE_SCISSOR
					));

			VkPushConstantRange.Buffer pushConstantRangesBuffer = null;
			int pcRangeCount = pushConstantRanges.length;
			if(pcRangeCount > 0){
				pushConstantRangesBuffer = VkPushConstantRange.calloc(pcRangeCount, stack);
				for (int i = 0; i < pcRangeCount; i++) {
					PushConstantRange pushConstantRange = pushConstantRanges[i];
					pushConstantRangesBuffer.get(i)
							.stageFlags(pushConstantRange.stage)
							.offset(pushConstantRange.offset)
							.size(pushConstantRange.size);
				}
			}

			VkPipelineColorBlendAttachmentState.Buffer blendAttachmentState = VkPipelineColorBlendAttachmentState.calloc(1, stack)
					.colorWriteMask(VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT | VK_COLOR_COMPONENT_B_BIT  | VK_COLOR_COMPONENT_A_BIT)
					.blendEnable(false);

			VkPipelineColorBlendStateCreateInfo blendStateCreateInfo = VkPipelineColorBlendStateCreateInfo.calloc(stack)
					.sType(VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO)
					.pAttachments(blendAttachmentState);

			IntBuffer colorFormats = stack.mallocInt(1);
			colorFormats.put(0, colorFormat);
			VkPipelineRenderingCreateInfo renderingCreateInfo = VkPipelineRenderingCreateInfo.calloc(stack)
					.sType(VK_STRUCTURE_TYPE_PIPELINE_RENDERING_CREATE_INFO)
					.colorAttachmentCount(1)
					.pColorAttachmentFormats(colorFormats);

			if(depthStencilStateCreateInfo != null){
				renderingCreateInfo.depthAttachmentFormat(depthFormat);
			}

			VkPipelineLayoutCreateInfo layoutCreateInfo = VkPipelineLayoutCreateInfo.calloc(stack)
					.sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
					.pPushConstantRanges(pushConstantRangesBuffer);

			checkVulkan(
					vkCreatePipelineLayout(context.getDevice().getVkDevice(), layoutCreateInfo, null, handle),
					"[VkPipeline]: Failed to create pipeline layout"
			);

			this.vkPipelineLayout = handle.get(0);

			VkGraphicsPipelineCreateInfo.Buffer createInfo = VkGraphicsPipelineCreateInfo.calloc(1, stack)
					.sType(VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO)
					.renderPass(VK_NULL_HANDLE)
					.pStages(stages)
					.pVertexInputState(vkPipelineVertexInputStateCreateInfo)
					.pInputAssemblyState(assemblyStateCreateInfo)
					.pViewportState(viewportState)
					.pRasterizationState(rasterizationStateCreateInfo)
					.pColorBlendState(blendStateCreateInfo)
					.pMultisampleState(multisampleState)
					.pDynamicState(dynamicState)
					.layout(vkPipelineLayout)
					.pNext(renderingCreateInfo);

			if(depthStencilStateCreateInfo != null) {
				createInfo.pDepthStencilState(depthStencilStateCreateInfo);
			}

			checkVulkan(
					vkCreateGraphicsPipelines(context.getDevice().getVkDevice(), context.getPipelineCache().getVkPipelineCache(), createInfo, null, handle),
					"[VkPipeline]: Failed to create pipeline"
			);

			this.vkPipeline = handle.get(0);
		}
	}

	@Override
	public void bindPipeline() {

	}

	public void cleanup(VulkanContext context) {
		Logger.debug("[VkPipeline]: Destroying pipeline]");
		vkDestroyPipelineLayout(context.getDevice().getVkDevice(), vkPipelineLayout, null);
		vkDestroyPipeline(context.getDevice().getVkDevice(), vkPipeline, null);
	}

	private int getStage(ShaderType shaderType) {
		return switch (shaderType) {
			case VERTEX -> VK_SHADER_STAGE_VERTEX_BIT;
			case FRAGMENT -> VK_SHADER_STAGE_FRAGMENT_BIT;
			case GEOMETRY -> VK_SHADER_STAGE_GEOMETRY_BIT;
			case COMPUTE -> VK_SHADER_STAGE_COMPUTE_BIT;
		};
	}

	long getVkPipeline() {
		return vkPipeline;
	}

	long getVkPipelineLayout() {
		return vkPipelineLayout;
	}

	public record PushConstantRange(int stage, int offset, int size){}

}
