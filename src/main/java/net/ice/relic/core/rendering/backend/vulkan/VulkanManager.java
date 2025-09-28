package net.ice.relic.core.rendering.backend.vulkan;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.rendering.backend.BackendManager;
import net.ice.relic.core.rendering.backend.vulkan.device.Device;
import net.ice.relic.core.rendering.backend.vulkan.device.DeviceSelector;
import net.ice.relic.core.rendering.backend.vulkan.device.PhysicalDevice;
import net.ice.relic.core.rendering.backend.vulkan.pipeline.PipelineCache;
import net.ice.relic.core.window.backend.vulkan.Surface;

public class VulkanManager extends BackendManager {

    private final Device device;
    private final Surface surface;
    private final SwapChain swapChain;
    private final VulkanInstance vulkanInstance;
    private final PhysicalDevice physicalDevice;
    private final RelicApplication application;
    private final DeviceSelector deviceSelector;
    private final PipelineCache pipelineCache;

    public VulkanManager(RelicApplication relicApplication) {
        super(relicApplication);

        this.application = relicApplication;
        this.vulkanInstance = new VulkanInstance(true, relicApplication.getApplicationVersion(), "String");
        this.deviceSelector = new DeviceSelector(vulkanInstance);
        this.physicalDevice = deviceSelector.selectBestDevice();
        this.device = new Device(physicalDevice);
        this.surface = new Surface();
        this.swapChain = new SwapChain(true, 2);
        this.pipelineCache = new PipelineCache();
    }

    @Override
    public void init() {
        physicalDevice.init();
        device.init();
        surface.init(vulkanInstance, application.getWindow(), device);
        swapChain.init(application.getWindow(), device, surface);
        pipelineCache.init(device);
    }

    public Device getDevice() {
        return device;
    }

    public PhysicalDevice getPhysicalDevice() {
        return physicalDevice;
    }

    public VulkanInstance getVulkanInstance() {
        return vulkanInstance;
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
