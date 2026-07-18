package net.ice.curio.library.vulkan.object.properties;

import net.ice.curio.library.vulkan.enums.PhysicalDeviceType;
import net.ice.heirloom.Version;
import org.lwjgl.vulkan.VkPhysicalDeviceLimits;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties2;
import org.lwjgl.vulkan.VkPhysicalDeviceSparseProperties;

import java.nio.ByteBuffer;

public class PhysicalDeviceProperties {

///    typedef struct VkPhysicalDeviceProperties {
///        uint32_t                            apiVersion;
///        uint32_t                            driverVersion;
///        uint32_t                            deviceID;
///        VkPhysicalDeviceType                deviceType;
///        char                                deviceName[VK_MAX_PHYSICAL_DEVICE_NAME_SIZE];
///        uint8_t                             pipelineCacheUUID[VK_UUID_SIZE];
///        VkPhysicalDeviceLimits              limits;
///        VkPhysicalDeviceSparseProperties    sparseProperties;
///    } VkPhysicalDeviceProperties;

    private Version vulkanAPIVersion;
    private int driverVersion;
    private int vendorID;
    private int deviceID;
    private PhysicalDeviceType physicalDeviceType;
    private String deviceName;
    private ByteBuffer pipelineCacheUUID;
    private VkPhysicalDeviceLimits limits;
    private VkPhysicalDeviceSparseProperties sparseProperties;

    private final VkPhysicalDeviceProperties2 deviceProperties;

    public PhysicalDeviceProperties(VkPhysicalDeviceProperties2 deviceProperties) {
        this.deviceProperties = deviceProperties;

        this.vulkanAPIVersion = Version.fromInt(deviceProperties.properties().apiVersion());
        this.driverVersion = deviceProperties.properties().driverVersion();
        this.vendorID = deviceProperties.properties().vendorID();
        this.deviceID = deviceProperties.properties().deviceID();
        this.physicalDeviceType = PhysicalDeviceType.fromVkEnum(deviceProperties.properties().deviceType());
        this.deviceName = deviceProperties.properties().deviceNameString();
        this.pipelineCacheUUID = deviceProperties.properties().pipelineCacheUUID();
        this.limits = deviceProperties.properties().limits();
        this.sparseProperties = deviceProperties.properties().sparseProperties();
    }

    public void cleanup() {
        deviceProperties.free();
    }

    public Version getVulkanAPIVersion() {
        return vulkanAPIVersion;
    }
    public int getDriverVersion() {
        return driverVersion;
    }
    public int getVendorID() {
        return vendorID;
    }
    public int getDeviceID() {
        return deviceID;
    }
    public PhysicalDeviceType getPhysicalDeviceType() {
        return physicalDeviceType;
    }
    public String getDeviceName() {
        return deviceName;
    }
    public ByteBuffer getPipelineCacheUUID() {
        return pipelineCacheUUID;
    }
    public VkPhysicalDeviceLimits getLimits() {
        return limits;
    }
    public VkPhysicalDeviceSparseProperties getSparseProperties() {
        return sparseProperties;
    }
    public VkPhysicalDeviceProperties2 getDeviceProperties() {
        return deviceProperties;
    }

}
