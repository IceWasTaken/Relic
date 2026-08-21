package net.ice.curio.library.vulkan.object;

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
