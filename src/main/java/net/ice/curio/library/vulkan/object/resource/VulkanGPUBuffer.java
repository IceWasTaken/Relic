package net.ice.curio.library.vulkan.object.resource;

import net.ice.curio.graphics.enums.BufferFlags;
import net.ice.curio.graphics.enums.BufferUsage;
import net.ice.curio.graphics.object.resource.GPUBuffer;
import net.ice.curio.library.vulkan.utils.ConversionUtil;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkBufferCreateInfo;

import java.nio.ByteBuffer;
import java.util.EnumSet;

import static org.lwjgl.vulkan.VK10.VK_SHARING_MODE_EXCLUSIVE;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO;

//public final class VulkanGPUBuffer extends GPUBuffer {
//
//    private VulkanGPUBuffer(long size, EnumSet<BufferFlags> usage) {
//        super(size, usage);
//
//        try(MemoryStack stack = MemoryStack.stackPush()) {
//            VkBufferCreateInfo vkBufferCreateInfo = VkBufferCreateInfo.calloc(stack)
//                    .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
//                    .size(size)
//                    //.usage(ConversionUtil.bufferUsageToVK(usage))
//                    .sharingMode(VK_SHARING_MODE_EXCLUSIVE);
//
////            VmaAllocationCreateInfo allocationCreateInfo = VmaAllocationCreateInfo.calloc(stack)
////                    .usage()
//
//
//        }
//    }
//
////    @Override
////    public void upload(ByteBuffer data, long offset) {
////
////    }
//
//    @Override
//    public ByteBuffer map() {
//        return null;
//    }
//
//    @Override
//    public void unmap() {
//
//    }
//
//    @Override
//    public void cleanup() {
//
//    }
//
//    public static class VulkanGPUBufferBuilder {
//
//
//
//    }
//}
