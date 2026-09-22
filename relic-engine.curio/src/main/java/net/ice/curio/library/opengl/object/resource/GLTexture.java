package net.ice.curio.library.opengl.object.resource;

import net.ice.curio.graphics.object.resource.Texture;
import net.ice.curio.library.stb.Bitmap;
import org.tinylog.Logger;

import static org.lwjgl.opengl.ARBBindlessTexture.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL45.*;

public class GLTexture extends Texture {

    private final int textureID;
    private final long textureHandle;

    public GLTexture(GLSampler sampler, Bitmap bitmap) {
        super(bitmap);

        while(glGetError() != GL_NO_ERROR);


        this.textureID = glCreateTextures(GL_TEXTURE_2D);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int levels = (int) Math.floor(log2(Math.max(width, height))) + 1;

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTextureStorage2D(textureID, levels, GL_RGBA8, width, height);
        glTextureSubImage2D(textureID, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, bitmap.getData());
        glGenerateTextureMipmap(textureID);

        bitmap.cleanup();

        this.textureHandle = glGetTextureSamplerHandleARB(textureID, sampler.getHandle());
        if (textureHandle == 0L) {
            Logger.error("[GLTexture]: Failed to get bindless handle for texture.");
            return;
        }

        glMakeTextureHandleResidentARB(textureHandle);

        if(!glIsTextureHandleResidentARB(textureHandle)) {
            Logger.error("[GLTexture]: Texture handle not resident: {}");
        }

        int error;
        if((error = glGetError()) != GL_NO_ERROR) {
            throw new RuntimeException("[GLTexture]: Encountered error while creating texture: " + error);
        }
    }

    public void bind(int pos) {
        glBindTextureUnit(pos, textureID);
    }

    public void unbind(int pos) {
        glBindTextureUnit(pos, 0);
    }

    @Override
    public void cleanup() {
        if (textureHandle != 0L) {
            glMakeTextureHandleNonResidentARB(textureHandle);
        }
        glDeleteTextures(textureID);
    }

    public int getTextureHandle() {
        return textureID;
    }

    @Override
    public long getHandle() {
        return textureHandle;
    }

    public record TextureFormat(
            int type,
            int format,
            int wrapS,
            int wrapT,
            int wrapR,
            int minFilter,
            int magFilter
    ) {}

}
