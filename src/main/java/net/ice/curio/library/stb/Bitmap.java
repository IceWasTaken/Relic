package net.ice.curio.library.stb;

import net.ice.heirloom.Lifecycle;
import net.ice.heirloom.io.resource.Resource;
import org.lwjgl.system.MemoryStack;
import org.tinylog.Logger;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;

public class Bitmap implements Lifecycle {

    private final int width;
    private final int height;
    private final int channels;

    private final ByteBuffer data;

    private final Resource resource;

    public Bitmap(Resource resource) {
        this.resource = resource;

        try(MemoryStack stack = MemoryStack.stackPush()) {
            Logger.info("[Bitmap] Loading bitmap: {}", resource.getAsPath());

            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelBuffer = stack.mallocInt(1);

            this.data = stbi_load_from_memory(resource.load(), widthBuffer, heightBuffer, channelBuffer, 4);
            this.width = widthBuffer.get(0);
            this.height = heightBuffer.get(0);
            this.channels = channelBuffer.get(0);

            if(data == null) {
                Logger.error("Failed to load image: {}",  stbi_failure_reason());
            }
        }
    }

    public Bitmap(int width, int height, int channels, ByteBuffer data) {
        this.width = width;
        this.height = height;
        this.channels = channels;
        this.data = data;
        this.resource = Resource.EMPTY;
    }

    @Override
    public void cleanup() {
        stbi_image_free(data);
    }

    public Resource getResource() {
        return resource;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ByteBuffer getData() {
        return data;
    }

    public int getChannels() {
        return channels;
    }
}
