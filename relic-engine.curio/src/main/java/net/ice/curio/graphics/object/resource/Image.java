package net.ice.curio.graphics.object.resource;

import net.ice.curio.graphics.enums.image.ImageFormat;
import net.ice.curio.graphics.enums.image.ImageType;
import net.ice.curio.graphics.enums.image.ImageUsage;
import net.ice.heirloom.Lifecycle;

import java.util.EnumSet;

public abstract class Image implements Lifecycle {

    protected ImageInfo imageInfo;

    protected Image(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }

    public static class ImageInfo {

        public int width;
        public int height;

        public int mipmapLevels;
        public int sampleCount;
        public int layers;

        public ImageFormat imageFormat;
        public EnumSet<ImageUsage> imageUsage;
        public ImageType imageType;

        public ImageInfo() {
            this.imageFormat = ImageFormat.RGBA8;
            this.mipmapLevels = 1;
            this.sampleCount = 1;
            this.layers = 1;
        }

        public ImageInfo mipmapLevels(int mipmapLevels) {
            this.mipmapLevels = mipmapLevels;
            return this;
        }

        public ImageInfo sampleCount(int sampleCount) {
            this.sampleCount = sampleCount;
            return this;
        }

        public ImageInfo usage(EnumSet<ImageUsage> usage) {
            this.imageUsage = usage;
            return this;
        }

        public ImageInfo layers(int layers) {
            this.layers = layers;
            return this;
        }
        public ImageInfo type(ImageType type) {
            this.imageType = type;
            return this;
        }
        public ImageInfo format(ImageFormat format) {
            this.imageFormat = format;
            return this;
        }
    }


}
