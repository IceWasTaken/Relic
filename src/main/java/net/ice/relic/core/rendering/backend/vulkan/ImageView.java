package net.ice.relic.core.rendering.backend.vulkan;

import net.ice.relic.core.rendering.backend.vulkan.device.Device;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkImageViewCreateInfo;

import java.nio.LongBuffer;

import static net.ice.relic.core.rendering.backend.vulkan.VulkanUtil.checkVulkan;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_VIEW_TYPE_2D;
import static org.lwjgl.vulkan.VK10.vkCreateImageView;

public class ImageView {

    private int aspectMask;
    private int mipLevels;
    private long vkImage;
    private long vkImageView;

    public ImageView(){}

    public ImageView init(Device device, long vkImage, Data data) {
        this.aspectMask = data.aspectMask;
        this.mipLevels = data.mipLevels;
        this.vkImage = vkImage;
        try (var stack = MemoryStack.stackPush()) {
            LongBuffer lp = stack.mallocLong(1);
            VkImageViewCreateInfo viewCreateInfo = VkImageViewCreateInfo.calloc(stack)
                    .sType$Default()
                    .image(vkImage)
                    .viewType(data.viewType)
                    .format(data.format)
                    .subresourceRange(it -> it
                            .aspectMask(aspectMask)
                            .baseMipLevel(0)
                            .levelCount(mipLevels)
                            .baseArrayLayer(data.baseArrayLayer)
                            .layerCount(data.layerCount));

            checkVulkan(vkCreateImageView(device.getDevice(), viewCreateInfo, null, lp),
                    "Failed to create image view");
            vkImageView = lp.get(0);
        }
        return this;
    }

    public long getVkImage() {
        return vkImage;
    }

    public long getVkImageView() {
        return vkImageView;
    }

    public static class Data {
        private int aspectMask;
        private int baseArrayLayer;
        private int format;
        private int layerCount;
        private int mipLevels;
        private int viewType;

        public Data() {
            this.baseArrayLayer = 0;
            this.layerCount = 1;
            this.mipLevels = 1;
            this.viewType = VK_IMAGE_VIEW_TYPE_2D;
        }

        public ImageView.Data aspectMask(int aspectMask) {
            this.aspectMask = aspectMask;
            return this;
        }

        public ImageView.Data baseArrayLayer(int baseArrayLayer) {
            this.baseArrayLayer = baseArrayLayer;
            return this;
        }

        public ImageView.Data format(int format) {
            this.format = format;
            return this;
        }

        public ImageView.Data layerCount(int layerCount) {
            this.layerCount = layerCount;
            return this;
        }

        public ImageView.Data mipLevels(int mipLevels) {
            this.mipLevels = mipLevels;
            return this;
        }

        public ImageView.Data viewType(int viewType) {
            this.viewType = viewType;
            return this;
        }
    }
}
