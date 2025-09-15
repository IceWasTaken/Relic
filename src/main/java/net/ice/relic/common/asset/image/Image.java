package net.ice.relic.common.asset.image;

import net.ice.relic.common.asset.Asset;
import org.lwjgl.system.MemoryStack;
import org.tinylog.Logger;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;

public class Image implements Asset {

    protected final int height;
    protected final int width;

    protected final String path;
    protected final ByteBuffer data;

    public Image(String path) {
        this.path = path;

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelBuffer = stack.mallocInt(1);

            this.data = stbi_load(path, widthBuffer, heightBuffer, channelBuffer, 4);
            this.width = widthBuffer.get(0);
            this.height = heightBuffer.get(0);

            if(data == null) {
                Logger.debug("Failed to load texture: {} - {}", path, stbi_failure_reason());
            }
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getPath() {
        return path;
    }

    @Override
    public void cleanup() {
        stbi_image_free(data);
    }

    public ByteBuffer getData() {
        return data;
    }
}
