package net.ice.curio.library.vulkan;

import net.ice.curio.Curio;
import net.ice.curio.graphics.context.GraphicsContext;
import net.ice.curio.graphics.object.Viewport;
import net.ice.curio.graphics.object.resource.Texture;
import net.ice.curio.library.stb.Bitmap;
import net.ice.curio.library.vulkan.object.VulkanViewport;
import net.ice.curio.library.vulkan.object.context.Device;
import net.ice.curio.library.vulkan.object.context.PhysicalDevice;
import net.ice.curio.library.vulkan.object.context.VulkanInstance;
import net.ice.curio.window.backend.vulkan.Surface;
import net.ice.curio.window.backend.vulkan.VulkanWindow;

public class VulkanContext extends GraphicsContext {

    private final VulkanInstance instance;
    private final PhysicalDevice physicalDevice;
    private final Device device;

    private Surface surface;

    public VulkanContext(Curio curio) {
        super(curio);

        this.instance = new VulkanInstance(curio.getApplicationProperties());
        this.physicalDevice = instance.createPhysicalDevice(null);
        this.device = new Device(physicalDevice);
        this.surface = new Surface(this, (VulkanWindow) curio.getWindow());
    }

    @Override
    public Texture createTexture(Bitmap bitmap) {
        return null;
    }

    @Override
    public Viewport createViewport(int width, int height) {
        return new VulkanViewport(width, height);
    }

    public VulkanInstance getInstance() {
        return instance;
    }
    public PhysicalDevice getPhysicalDevice() {
        return physicalDevice;
    }

    public Device getDevice() {
        return device;
    }
}
