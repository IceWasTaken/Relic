package net.ice.relic.core.rendering.backend.vulkan.rendering;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.interfaces.Cleanable;
import net.ice.relic.core.model.MeshData;
import net.ice.relic.core.rendering.backend.Renderer;
import net.ice.relic.core.rendering.backend.opengl.model.Model;
import net.ice.relic.core.rendering.backend.vulkan.Queue;
import net.ice.relic.core.rendering.backend.vulkan.SwapChain;
import net.ice.relic.core.rendering.backend.vulkan.VulkanManager;
import net.ice.relic.core.rendering.backend.vulkan.buffer.TransferBuffer;
import net.ice.relic.core.rendering.backend.vulkan.buffer.VkBuffer;
import net.ice.relic.core.rendering.backend.vulkan.command.CommandBuffer;
import net.ice.relic.core.rendering.backend.vulkan.command.CommandPool;
import net.ice.relic.core.rendering.backend.vulkan.model.VulkanMesh;
import net.ice.relic.core.rendering.backend.vulkan.model.VulkanModel;
import net.ice.relic.core.rendering.backend.vulkan.sync.Fence;
import net.ice.relic.core.rendering.backend.vulkan.sync.Semaphore;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkCommandBufferSubmitInfo;
import org.lwjgl.vulkan.VkSemaphoreSubmitInfo;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.vulkan.VK13.*;

public class VulkanRenderer extends Renderer implements Cleanable {

    private final int MAX_FRAMES_IN_SWAPCHAIN = 2;

    private final CommandBuffer[] commandBuffers;
    private final CommandPool[] commandPools;
    private final Fence[] fences;
    private  Semaphore[] renderCompleteSemphs;
    private final Semaphore[] presCompleteSemphs;

    private Queue.GraphicsQueue graphicsQueue;
    private Queue.PresentQueue presentQueue;

    private List<VulkanModel> vulkanModels;
    private int currentFrame;

    private final VulkanSceneRenderer sceneRenderer;

    private final RelicApplication application;
    private final VulkanManager vulkanManager;

    public VulkanRenderer(RelicApplication application) {
        this.application = application;
        this.currentFrame = 0;

        if(application.getBackendManager() instanceof VulkanManager vulkanManager) {
            this.vulkanManager = vulkanManager;
            this.commandBuffers = new CommandBuffer[MAX_FRAMES_IN_SWAPCHAIN];
            this.commandPools = new CommandPool[MAX_FRAMES_IN_SWAPCHAIN];
            this.fences = new Fence[MAX_FRAMES_IN_SWAPCHAIN];
            this.presCompleteSemphs = new Semaphore[MAX_FRAMES_IN_SWAPCHAIN];
            this.sceneRenderer = new VulkanSceneRenderer();
        } else {
            throw new RuntimeException("Engine: Attempting to create vulkan renderer in a non-vulkan backend.");
        }
    }

    private void waitForFence(int currentFrame) {
        Fence fence = fences[currentFrame];
        fence.fenceWait(vulkanManager.getDevice());
    }

    private void beginRecording(CommandPool commandPool, CommandBuffer commandBuffer) {
        commandPool.reset(vulkanManager.getDevice());
        commandBuffer.record();
    }

    private void stopRecording(CommandBuffer commandBuffer) {
        commandBuffer.stopRecording();
    }

    private void submit(CommandBuffer commandBuffer, int currentFrame, int imageIndex) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            Fence fence = fences[currentFrame];
            fence.reset(vulkanManager.getDevice());
            VkCommandBufferSubmitInfo.Buffer submitInfos = VkCommandBufferSubmitInfo.calloc(1, stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_SUBMIT_INFO)
                    .commandBuffer(commandBuffer.getCommandBuffer());
            VkSemaphoreSubmitInfo.Buffer waitSemaphores = VkSemaphoreSubmitInfo.calloc(1, stack)
                    .sType(VK_STRUCTURE_TYPE_SEMAPHORE_SUBMIT_INFO)
                    .stageMask(VK_PIPELINE_STAGE_2_COLOR_ATTACHMENT_OUTPUT_BIT)
                    .semaphore(presCompleteSemphs[currentFrame].getSemaphoreHandle());
            VkSemaphoreSubmitInfo.Buffer signalSemphs = VkSemaphoreSubmitInfo.calloc(1, stack)
                    .sType$Default()
                    .stageMask(VK_PIPELINE_STAGE_2_BOTTOM_OF_PIPE_BIT)
                    .semaphore(renderCompleteSemphs[imageIndex].getSemaphoreHandle());
            graphicsQueue.submit(submitInfos, waitSemaphores, signalSemphs, fence);
        }
    }

    @Override
    public void init() {
        this.renderCompleteSemphs = new Semaphore[vulkanManager.getSwapChain().getImageCount()];

        this.graphicsQueue = new Queue.GraphicsQueue(vulkanManager.getDevice(), 0);
        this.presentQueue = new Queue.PresentQueue(vulkanManager.getDevice(), 0, vulkanManager);

        for (int i = 0; i < MAX_FRAMES_IN_SWAPCHAIN; i++) {
            commandPools[i] = new CommandPool().init(vulkanManager.getDevice());
            commandBuffers[i] = new CommandBuffer(true, true).init(vulkanManager.getDevice(), commandPools[i]);
            fences[i] = new Fence().init(vulkanManager.getDevice(), true);
            presCompleteSemphs[i] = new Semaphore().init(vulkanManager.getDevice());
        }
        for (int i = 0; i < vulkanManager.getSwapChain().getImageCount(); i++) {
            renderCompleteSemphs[i] = new Semaphore().init(vulkanManager.getDevice());
        }

        sceneRenderer.init(vulkanManager);
    }

    @Override
    public void render() {
        if(vulkanModels == null) {
            List<Model> models = application.getCurrentScene().getModels().values().stream().toList();
            this.vulkanModels = loadModels(vulkanManager, models, commandPools[0], graphicsQueue);
        }

        SwapChain swapChain = vulkanManager.getSwapChain();
        waitForFence(currentFrame);

        CommandPool commandPool = commandPools[currentFrame];
        CommandBuffer commandBuffer = commandBuffers[currentFrame];

        beginRecording(commandPool, commandBuffer);

        int imgIndex = swapChain.getNextImage(vulkanManager.getDevice(), presCompleteSemphs[currentFrame]);
        if(imgIndex < 0) {
            return;
        }

        sceneRenderer.render(vulkanManager, commandBuffer, 0, vulkanModels);

        stopRecording(commandBuffer);

        submit(commandBuffer, currentFrame, imgIndex);

        swapChain.presentImage(presentQueue, renderCompleteSemphs[imgIndex], imgIndex);

        currentFrame = (currentFrame + 1) % MAX_FRAMES_IN_SWAPCHAIN;
    }


    @Override
    public void resize() {

    }

    @Override
    public void setupData() {

    }

    public List<VulkanModel> loadModels(VulkanManager vkCtx, List<Model> models, CommandPool cmdPool, Queue queue) {
        List<VkBuffer> stagingBufferList = new ArrayList<>();
        List<VulkanModel> vulkanModels = new ArrayList<>();

        var cmd = new CommandBuffer(true, true).init(vkCtx.getDevice(), cmdPool);
        cmd.record();

        for (Model modelData : models) {
            VulkanModel vulkanModel = new VulkanModel(modelData.getId());
            //modelsMap.put(vulkanModel.getId(), vulkanModel);
            vulkanModels.add(vulkanModel);

            // Transform meshes loading their data into GPU buffers
            for (MeshData meshData : modelData.getMeshData()) {
                TransferBuffer verticesBuffers = createVerticesBuffers(vkCtx, meshData);
                TransferBuffer indicesBuffers = createIndicesBuffers(vkCtx, meshData);
                stagingBufferList.add(verticesBuffers.srcBuffer());
                stagingBufferList.add(indicesBuffers.srcBuffer());
                verticesBuffers.recordTransferCommand(cmd);
                indicesBuffers.recordTransferCommand(cmd);

                VulkanMesh vulkanMesh = new VulkanMesh(verticesBuffers.dstBuffer(),
                        indicesBuffers.dstBuffer(), meshData.getIndices().length);
                vulkanModel.getVulkanMeshList().add(vulkanMesh);
            }
        }

        cmd.stopRecording();
        cmd.submitAndWait(vkCtx, queue);
        //cmd.cleanup(vkCtx, cmdPool);

        stagingBufferList.forEach(b -> b.cleanup(vkCtx));

        return vulkanModels;
    }

    private TransferBuffer createIndicesBuffers(VulkanManager vkCtx, MeshData meshData) {
        int[] indices = meshData.getIndices();
        int numIndices = indices.length;
        int bufferSize = numIndices * 4;

        var srcBuffer = new VkBuffer(vkCtx, bufferSize,
                VK_BUFFER_USAGE_TRANSFER_SRC_BIT, VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
        var dstBuffer = new VkBuffer(vkCtx, bufferSize,
                VK_BUFFER_USAGE_TRANSFER_DST_BIT | VK_BUFFER_USAGE_INDEX_BUFFER_BIT, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);

        long mappedMemory = srcBuffer.map(vkCtx);
        IntBuffer data = MemoryUtil.memIntBuffer(mappedMemory, (int) srcBuffer.getRequestedSize());
        data.put(indices);
        srcBuffer.unMap(vkCtx);

        return new TransferBuffer(srcBuffer, dstBuffer);
    }

    private TransferBuffer createVerticesBuffers(VulkanManager vkCtx, MeshData meshData) {
        float[] positions = meshData.getVertices();
        float[] textCoords = meshData.getTextureCoords();
        if (textCoords == null || textCoords.length == 0) {
            textCoords = new float[(positions.length / 3) * 2];
        }
        int numElements = positions.length + textCoords.length;
        int bufferSize = numElements * 4;

        var srcBuffer = new VkBuffer(vkCtx, bufferSize,
                VK_BUFFER_USAGE_TRANSFER_SRC_BIT, VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
        var dstBuffer = new VkBuffer(vkCtx, bufferSize,
                VK_BUFFER_USAGE_TRANSFER_DST_BIT | VK_BUFFER_USAGE_VERTEX_BUFFER_BIT, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);

        long mappedMemory = srcBuffer.map(vkCtx);
        FloatBuffer data = MemoryUtil.memFloatBuffer(mappedMemory, (int) srcBuffer.getRequestedSize());

        int rows = positions.length / 3;
        for (int row = 0; row < rows; row++) {
            int startPos = row * 3;
            int startTextCoord = row * 2;
            data.put(positions[startPos]);
            data.put(positions[startPos + 1]);
            data.put(positions[startPos + 2]);
            data.put(textCoords[startTextCoord]);
            data.put(textCoords[startTextCoord + 1]);
        }

        srcBuffer.unMap(vkCtx);

        return new TransferBuffer(srcBuffer, dstBuffer);
    }
}
