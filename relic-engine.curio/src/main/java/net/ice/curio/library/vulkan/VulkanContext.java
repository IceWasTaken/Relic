package net.ice.curio.library.vulkan;

import net.ice.curio.Curio;
import net.ice.curio.graphics.context.GraphicsContext;
import net.ice.curio.graphics.context.GraphicsContextLogger;
import net.ice.curio.graphics.object.Viewport;
import net.ice.curio.graphics.object.resource.Texture;
import net.ice.curio.library.stb.Bitmap;
import net.ice.curio.library.vulkan.object.*;
import net.ice.curio.library.vulkan.object.Surface;
import net.ice.curio.window.Window;
import net.ice.curio.window.backend.vulkan.VulkanWindow;

public class VulkanContext extends GraphicsContext {

    public static final int SWAPCHAIN_REQUESTED_IMAGES = 3;

    private final Instance instance;
    private final PhysicalDevice physicalDevice;
    private final Device device;
    private final PipelineCache pipelineCache;
    private final MemoryAllocator VMAInstance;

    private Surface surface;
    private SwapChain swapChain;

    public VulkanContext(Curio curio) {
        super(curio);

        this.instance = new Instance(this);
        this.physicalDevice = PhysicalDevice.pickDevice(this);
        this.device = new Device(this);
        this.VMAInstance = new MemoryAllocator(this);

        this.surface = new Surface(this);
        this.swapChain = new SwapChain(this, SWAPCHAIN_REQUESTED_IMAGES, true);
        this.pipelineCache = new PipelineCache(this);
    }

    @Override
    protected void setupWindowAttributes(Window window) {

    }

    @Override
    protected GraphicsContextLogger createContextLogger() {
        return null;
    }

    @Override
    public Texture createTexture(Bitmap bitmap) {
        return null;
    }

    @Override
    public Viewport createViewport(int width, int height) {
        return new VulkanViewport(width, height);
    }

    public Instance getInstance() {
        return instance;
    }
    public PhysicalDevice getPhysicalDevice() {
        return physicalDevice;
    }
    public Device getDevice() {
        return device;
    }

    public MemoryAllocator getVMAInstance() {
        return VMAInstance;
    }

    public Surface getSurface() {
        return surface;
    }

    public SwapChain getSwapChain() {
        return swapChain;
    }

    public PipelineCache getPipelineCache() {
        return pipelineCache;
    }
}
