package net.ice.relic.core.rendering.backend.opengl.model.texture;

import net.ice.relic.common.asset.image.Image;
import org.tinylog.Logger;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.ARBBindlessTexture.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_image_free;

public class GLTexture {

    private final int textureID;
    private final long bindlessHandle;

    public GLTexture(String path) {
        this(new Image(path));
    }

    public GLTexture(Image image) {
        this(image.getPath(), image.getWidth(), image.getHeight(), image.getData());
    }

    public GLTexture(String path, int width, int height, ByteBuffer data) {
        this.textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        glGenerateMipmap(GL_TEXTURE_2D);

        stbi_image_free(data);

        this.bindlessHandle = glGetTextureHandleARB(textureID);
        if(bindlessHandle == 0L) {
            throw new RuntimeException("Failed to obtain bindless handle.");
        }

        glMakeTextureHandleResidentARB(bindlessHandle);

        if(!glIsTextureHandleResidentARB(bindlessHandle)) {
            Logger.error("Texture handle not resident: {}", path);
        }
    }

    public GLTexture(int width, int height, ByteBuffer imageData) {
        this.textureID = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, textureID);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData);
        glGenerateMipmap(GL_TEXTURE_2D);

        this.bindlessHandle = glGetTextureHandleARB(textureID);
        if (bindlessHandle == 0L) {
            throw new RuntimeException("Failed to obtain bindless texture handle.");
        }

        glMakeTextureHandleResidentARB(bindlessHandle);

        if (!glIsTextureHandleResidentARB(bindlessHandle)) {
            Logger.error("Texture handle not resident: {}", "internal resource");
        }
    }


    public GLTexture(int width, int height) {
        this.textureID = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        this.bindlessHandle = glGetTextureHandleARB(textureID);
        if (bindlessHandle == 0L) {
            throw new RuntimeException("Failed to get bindless handle for texture.");
        }

        glMakeTextureHandleResidentARB(bindlessHandle);
    }

    public long getBindlessHandle() {
        return bindlessHandle;
    }

    public void cleanup() {
        if (bindlessHandle != 0L) {
            glMakeTextureHandleNonResidentARB(bindlessHandle);
        }
        glDeleteTextures(textureID);
    }

    public int getTextureID() {
        return textureID;
    }

    public boolean isResident() {
        return bindlessHandle != 0L;
    }
}