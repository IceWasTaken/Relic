package net.ice.curio.graphics.object.resource;

import net.ice.curio.graphics.context.GraphicsContext;
import net.ice.curio.graphics.enums.image.ImageFormat;
import net.ice.curio.graphics.enums.image.ImageType;
import net.ice.curio.graphics.enums.image.ImageUsage;
import net.ice.heirloom.Lifecycle;

import java.util.EnumSet;

public abstract class Image implements Lifecycle {

    protected final int width;
    protected final int height;
    protected final int mipmapLevels;

    protected final ImageType imageType;
    protected final ImageFormat format;

    public Image(GraphicsContext graphicsContext, ImageInfo imageInfo) {
        this.width = imageInfo.width;
        this.height = imageInfo.height;
        this.mipmapLevels = imageInfo.mipmapLevels;

        this.imageType = imageInfo.imageType;
        this.format = imageInfo.imageFormat;
    }

    public static class ImageInfo {

        private int width;
        private int height;

        private int mipmapLevels;
        private int sampleCount;
        private int layers;

        private ImageFormat imageFormat;
        private EnumSet<ImageUsage> imageUsage;
        private ImageType imageType;

        public ImageInfo() {
            this.imageFormat = ImageFormat.RGBA8;
            this.imageType = ImageType.IMAGE_2D;
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

        public ImageInfo width(int width) {
            this.width = width;
            return this;
        }

        public ImageInfo height(int height) {
            this.height = height;
            return this;
        }

        public int getLayers() {
            return layers;
        }

        public int getMipmapLevels() {
            return mipmapLevels;
        }

        public EnumSet<ImageUsage> getImageUsage() {
            return imageUsage;
        }

        public ImageFormat getImageFormat() {
            return imageFormat;
        }

        public ImageType getImageType() {
            return imageType;
        }

        public int getHeight() {
            return height;
        }

        public int getSampleCount() {
            return sampleCount;
        }

        public int getWidth() {
            return width;
        }
    }


}
