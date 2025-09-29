package net.ice.relic.common.asset.image;

import org.lwjgl.system.MemoryStack;
import org.tinylog.Logger;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;

public class Image {

    protected final int height;
    protected final int width;

    protected final ByteBuffer data;

    public Image(ByteBuffer buffer) {
        try(MemoryStack stack = MemoryStack.stackPush()) {

            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelBuffer = stack.mallocInt(1);

            this.data = stbi_load_from_memory(buffer, widthBuffer, heightBuffer, channelBuffer, 4);
            this.width = widthBuffer.get(0);
            this.height = heightBuffer.get(0);

            if(data == null) {
                Logger.error("Failed to load texture: {}",  stbi_failure_reason());
            }
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void cleanup() {
        stbi_image_free(data);
    }

    public ByteBuffer getData() {
        return data;
    }


}
