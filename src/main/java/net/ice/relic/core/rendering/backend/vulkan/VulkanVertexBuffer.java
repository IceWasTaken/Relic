package net.ice.relic.core.rendering.backend.vulkan;

//public class VulkanVertexBuffer implements VertexBuffer {
//
//    private final long handle;
//
//    //private final
//
//    public VulkanVertexBuffer(long size, int vmaUsage, int vmaFlags, int reqFlags) {
//        try(MemoryStack stack = MemoryStack.stackPop()) {
//            VkBufferCreateInfo bufferInfo = VkBufferCreateInfo.calloc(stack)
//                    .sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
//                    .size(size)
//                    .usage(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT)
//                    .sharingMode(VK_SHARING_MODE_EXCLUSIVE);
//
//
//            PointerBuffer alloc = stack.callocPointer(1);
//            LongBuffer longBuffer = stack.mallocLong(1);
//            handle = longBuffer.get(0);
//
//            if((VK_BUFFER_USAGE_VERTEX_BUFFER_BIT & VK_BUFFER_USAGE_SHADER_DEVICE_ADDRESS_BIT) > 0) {
//
//            }
//        }
//    }
//
//    @Override
//    public void bind() {
//
//    }
//
//    @Override
//    public void unbind() {
//
//    }
//
//    @Override
//    public void bufferData() {
//
//    }
//
//    @Override
//    public void delete() {
//        vkDestroyBuffer(null, handle, null);
//    }
//}
