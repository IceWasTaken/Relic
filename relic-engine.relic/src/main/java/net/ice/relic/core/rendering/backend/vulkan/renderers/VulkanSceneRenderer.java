package net.ice.relic.core.rendering.backend.vulkan.renderers;

import net.ice.curio.graphics.enums.image.ImageFormat;
import net.ice.curio.graphics.object.pipeline.PrimitiveType;
import net.ice.curio.graphics.object.pipeline.depth.CompareFunction;
import net.ice.curio.graphics.object.pipeline.depth.DepthState;
import net.ice.curio.graphics.object.pipeline.raster.CullMode;
import net.ice.curio.graphics.object.pipeline.raster.FrontFace;
import net.ice.curio.graphics.object.pipeline.raster.PolygonMode;
import net.ice.curio.graphics.object.pipeline.raster.RasterizationState;
import net.ice.curio.graphics.object.pipeline.shader.ShaderType;
import net.ice.curio.library.shaderc.ShaderCompiler;
import net.ice.curio.library.vulkan.VulkanContext;
import net.ice.curio.library.vulkan.object.*;
import net.ice.curio.library.vulkan.utils.VulkanUtils;
import net.ice.heirloom.Lifecycle;
import net.ice.relic.core.ecs.component.components.TransformComponent;
import net.ice.relic.core.ecs.component.components.rendering.ModelComponent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.model.Model;
import net.ice.relic.core.model.mesh.MeshData;
import net.ice.relic.core.rendering.backend.vulkan.VertexBufferStruct;
import net.ice.relic.core.scene.Scene;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static net.ice.curio.graphics.enums.image.ImageUsage.DEPTH_STENCIL_ATTACHMENT;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_glsl_fragment_shader;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_glsl_vertex_shader;
import static org.lwjgl.vulkan.KHRSynchronization2.VK_IMAGE_LAYOUT_ATTACHMENT_OPTIMAL_KHR;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK13.*;

public class VulkanSceneRenderer implements Lifecycle {

	private static final String VERTEX_SHADER_FILE = "resources/relic/data/rendering/vulkan/scene.vert";
	private static final String FRAGMENT_SHADER_FILE = "resources/relic/data/rendering/vulkan/scene.frag";

	private final VkClearValue clearColorValue;
	private final VkClearValue clearDepthValue;
	private final ByteBuffer pushConstants;


	private final VkPipeline pipeline;

	private VkRenderingAttachmentInfo.Buffer[] attachmentInfoColor;
	private VkRenderingInfo[] renderInfo;
	private Attachment[] depthAttachments;

	private final VulkanContext ctx;

	public VulkanSceneRenderer(VulkanContext ctx) {
		this.ctx = ctx;

		this.clearColorValue = VkClearValue.calloc().color(VulkanUtils::clearColor);
		this.clearDepthValue = VkClearValue.calloc().color(VulkanUtils::clearDepth);

		this.depthAttachments = createDepthAttachment(ctx);

		this.attachmentInfoColor = createColorAttachmentInfo();
		this.renderInfo = createRenderInfo();
		this.pushConstants = MemoryUtil.memAlloc(64 * 4 * 200);

		VkShader[] shaders = createShaders(ctx);
		this.pipeline = createPipeline(ctx, shaders);
		Arrays.asList(shaders).forEach(s -> s.cleanup(ctx));
	}

	private Attachment[] createDepthAttachment(VulkanContext ctx) {
		SwapChain swapChain = ctx.getSwapChain();
		int imageCount = swapChain.getImageCount();
		VkExtent2D swapChainExtent = swapChain.getSwapChainExtent();
		Attachment[] attachments = new Attachment[imageCount];
		for (int i = 0; i < imageCount; i++) {
			attachments[i] = new Attachment(ctx, swapChainExtent.width(), swapChainExtent.height(), ImageFormat.DEPTH_16_UNSIGNED_NORMALIZED, EnumSet.of(DEPTH_STENCIL_ATTACHMENT));
		}

		return attachments;
	}

	private VkPipeline createPipeline(VulkanContext ctx, VkShader[] shaders) {
		VertexBufferStruct vertexBuffer = new VertexBufferStruct();
		VkPipeline vkPipeline = new VkPipeline(
				ctx,
				PrimitiveType.TRIANGLE,
				new RasterizationState(
						PolygonMode.FILL,
						FrontFace.COUNTER_CLOCKWISE,
						CullMode.NONE,
						false,
						1.0f
				),
				new DepthState(
						true,
						true,
						CompareFunction.LESS_OR_EQUAL,
						true,
						false
				),
				shaders,
				new VkPipeline.PushConstantRange[] {
						new VkPipeline.PushConstantRange(VK_SHADER_STAGE_VERTEX_BIT, 0, 64 * 2)
				},
				vertexBuffer.getVi(),
				VK_FORMAT_D16_UNORM,
				ctx.getSurface().getSurfaceFormat().imageFormat()
		);

		vertexBuffer.cleanup();
		return vkPipeline;
	}

	private VkShader[] createShaders(VulkanContext ctx) {
		new ShaderCompiler(ctx).compileIf(VERTEX_SHADER_FILE, shaderc_glsl_vertex_shader, ShaderCompiler.IF_CHANGED).cleanup();
		new ShaderCompiler(ctx).compileIf(FRAGMENT_SHADER_FILE, shaderc_glsl_fragment_shader, ShaderCompiler.IF_CHANGED).cleanup();

		return new VkShader[] {
				new VkShader(ctx, ShaderType.VERTEX, VERTEX_SHADER_FILE + ".spv"),
				new VkShader(ctx, ShaderType.FRAGMENT, FRAGMENT_SHADER_FILE + ".spv")
		};
	}

	private VkRenderingAttachmentInfo.Buffer[] createColorAttachmentInfo() {
		SwapChain swapChain = ctx.getSwapChain();
		int imageCount = swapChain.getImageCount();
		VkRenderingAttachmentInfo.Buffer[] result = new  VkRenderingAttachmentInfo.Buffer[imageCount];

		for (int i = 0; i < imageCount; i++) {
			result[i] = swapChain.getImageViews()[i].createAttachmentInfo(
					VK_IMAGE_LAYOUT_ATTACHMENT_OPTIMAL_KHR,
					VK_ATTACHMENT_LOAD_OP_CLEAR,
					VK_ATTACHMENT_STORE_OP_STORE,
					clearColorValue
			);
		}

		return result;
	}

	private VkRenderingInfo[] createRenderInfo(){
		SwapChain swapChain = ctx.getSwapChain();
		int imageCount = swapChain.getImageCount();

		VkRenderingInfo[] renderInfo = new VkRenderingInfo[imageCount];

		try(MemoryStack stack = MemoryStack.stackPush()) {
			VkExtent2D extent = swapChain.getSwapChainExtent();

			VkRect2D renderArea = VkRect2D.calloc(stack).extent(extent);

			for(int i = 0; i < imageCount; i++) {
				VkRenderingInfo renderingInfo = VkRenderingInfo.calloc()
						.sType(VK_STRUCTURE_TYPE_RENDERING_INFO)
						.renderArea(renderArea)
						.layerCount(1)
						.pColorAttachments(attachmentInfoColor[i]);

				renderInfo[i] = renderingInfo;
			}
		}

		return renderInfo;
	}

	private void setPushConstants(VulkanCommandBuffer commandBuffer, Matrix4f projMatrix, Matrix4f transformMatrix) {
		projMatrix.get(pushConstants);
		transformMatrix.get(16 * 4 * 4, pushConstants);
		commandBuffer.cmdPushConstants(pushConstants, pipeline, VK_SHADER_STAGE_VERTEX_BIT, 0);
	}

	public void render(VulkanCommandBuffer commandBuffer, int index, Scene scene) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			SwapChain swapChain = ctx.getSwapChain();

			swapChain.getImageViews()[index].imageBarrier(
					stack,
					commandBuffer,
					VK_IMAGE_LAYOUT_UNDEFINED,
					VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL,
					VK_PIPELINE_STAGE_2_COLOR_ATTACHMENT_OUTPUT_BIT,
					VK_PIPELINE_STAGE_2_COLOR_ATTACHMENT_OUTPUT_BIT,
					VK_ACCESS_2_NONE,
					VK_ACCESS_2_COLOR_ATTACHMENT_WRITE_BIT
			);
			depthAttachments[index].getImageView().imageBarrier(
					stack,
					commandBuffer,
					VK_IMAGE_LAYOUT_UNDEFINED,
					VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_OPTIMAL,
					VK_PIPELINE_STAGE_2_EARLY_FRAGMENT_TESTS_BIT | VK_PIPELINE_STAGE_2_LATE_FRAGMENT_TESTS_BIT,
					VK_PIPELINE_STAGE_2_EARLY_FRAGMENT_TESTS_BIT | VK_PIPELINE_STAGE_2_LATE_FRAGMENT_TESTS_BIT,
					VK_ACCESS_2_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT,
					VK_ACCESS_2_DEPTH_STENCIL_ATTACHMENT_READ_BIT | VK_ACCESS_2_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT
			);

			commandBuffer.cmdBeginRendering(renderInfo[index]);
			commandBuffer.cmdBindPipeline(pipeline);

			VkExtent2D extent = swapChain.getSwapChainExtent();
			int width = extent.width();
			int height = extent.height();
			VkViewport.Buffer viewport = VkViewport.calloc(1, stack)
					.x(0)
					.y(height)
					.height(-height)
					.width(width)
					.minDepth(1.0f)
					.maxDepth(0.0f);

			commandBuffer.cmdSetViewport(viewport);

			VkRect2D.Buffer scissor = VkRect2D.calloc(1, stack)
					.extent(e -> e.width(width).height(height))
					.offset(e -> e.x(0).y(0));

			commandBuffer.cmdSetScissor(scissor);

			LongBuffer offsets = stack.mallocLong(1).put(0, 0L);
			LongBuffer vertexBuffer = stack.mallocLong(1);

			List<Entity> entities = scene.getEntities();
			for(Entity entity : entities) {
				Model model = entity.getComponent(ModelComponent.class).getModel();
				List<MeshData> meshes = model.getMeshData();
				int meshCount = meshes.size();
				setPushConstants(commandBuffer, scene.getMatrix().getProjMatrix(), entity.getComponent(TransformComponent.class).getTransformationMatrix());
				for(MeshData mesh : meshes) {
					//vertexBuffer.put(0, mesh.getPositions());
				}
			}


		}
	}

	@Override
	public void cleanup() {
	}
}
