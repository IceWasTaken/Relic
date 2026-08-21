package net.ice.curio.library.vulkan.enums;

import static org.lwjgl.vulkan.VK10.*;

public enum PhysicalDeviceType {

    OTHER(VK_PHYSICAL_DEVICE_TYPE_OTHER),
    INTEGRATED(VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU),
    DISCRETE(VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU),
    VIRTUAL(VK_PHYSICAL_DEVICE_TYPE_VIRTUAL_GPU),
    CPU(VK_PHYSICAL_DEVICE_TYPE_CPU);

    private final int vkEnum;

    PhysicalDeviceType(int vkEnum) {
        this.vkEnum = vkEnum;
    }

    public static PhysicalDeviceType fromVkEnum(int vkEnum) {
        for(PhysicalDeviceType physicalDeviceType : PhysicalDeviceType.values()) {
            if(vkEnum == physicalDeviceType.vkEnum) {
                return physicalDeviceType;
            }
        }
        return null;
    }

    public int getVkEnum() {
        return vkEnum;
    }
}
