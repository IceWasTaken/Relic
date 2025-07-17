package net.ice.relic.engine.opengl.model.texture;

import org.lwjgl.system.MemoryStack;
import org.tinylog.Logger;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.ARBBindlessTexture.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private final int textureID;
    private final long bindlessHandle;
    private final String texturePath;
    private boolean isResident;

    public Texture(String texturePath) {
        this.texturePath = texturePath;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuf = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            IntBuffer channelsBuf = stack.mallocInt(1);

            ByteBuffer imageData = stbi_load(texturePath, widthBuf, heightBuf, channelsBuf, 4);
            if (imageData == null) {
                Logger.error("Failed to load texture: {} - {}", texturePath, stbi_failure_reason());
                throw new RuntimeException("Failed to load texture: " + texturePath);
            }

            int width = widthBuf.get(0);
            int height = heightBuf.get(0);

            this.textureID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureID);

            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData);
            glGenerateMipmap(GL_TEXTURE_2D);

            stbi_image_free(imageData);
            this.bindlessHandle = glGetTextureHandleARB(textureID);
            if (bindlessHandle == 0L) {
                throw new RuntimeException("Failed to obtain bindless texture handle.");
            }

            glMakeTextureHandleResidentARB(bindlessHandle);
            this.isResident = true;
        }
        if (!glIsTextureHandleResidentARB(bindlessHandle)) {
            Logger.error("Texture handle not resident: {}", texturePath);
        }
    }

    public Texture(int width, int height) {
        this.texturePath = "__internal__";
        this.textureID = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        this.bindlessHandle = glGetTextureHandleARB(textureID);
        if (bindlessHandle == 0L) {
            throw new RuntimeException("Failed to get bindless handle for post-process texture.");
        }

        glMakeTextureHandleResidentARB(bindlessHandle);
        this.isResident = true;
    }

    public long getBindlessHandle() {
        return bindlessHandle;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void cleanup() {
        if (isResident && bindlessHandle != 0L) {
            glMakeTextureHandleNonResidentARB(bindlessHandle);
        }
        glDeleteTextures(textureID);
        isResident = false;
    }

    public int getTextureID() {
        return textureID;
    }

    public boolean isResident() {
        return isResident;
    }
}