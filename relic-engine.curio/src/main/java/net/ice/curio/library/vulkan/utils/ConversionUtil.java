package net.ice.curio.library.vulkan.utils;

import net.ice.curio.graphics.enums.BufferUsage;

import java.util.EnumSet;

import static org.lwjgl.vulkan.VK10.*;

public class ConversionUtil {

    public static int bufferUsageToVK(EnumSet<BufferUsage> usages) {
        int flags = 0;

        if(usages.contains(BufferUsage.VERTEX)) {
            flags |= VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;
        }
        if(usages.contains(BufferUsage.INDEX)) {
            flags |= VK_BUFFER_USAGE_INDEX_BUFFER_BIT;
        }
        if(usages.contains(BufferUsage.UNIFORM)) {
            flags |= VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT;
        }
        if(usages.contains(BufferUsage.STORAGE)) {
            flags |= VK_BUFFER_USAGE_STORAGE_BUFFER_BIT;
        }
        if(usages.contains(BufferUsage.TRANSFER_SOURCE)) {
            flags |= VK_BUFFER_USAGE_TRANSFER_SRC_BIT;
        }
        if(usages.contains(BufferUsage.TRANSFER_DESTINATION)) {
            flags |= VK_BUFFER_USAGE_TRANSFER_DST_BIT;
        }

        return flags;
    }
}
