package net.ice.curio.library.opengl.object.resource;

import net.ice.curio.graphics.object.resource.Texture;
import net.ice.curio.library.opengl.wrapper.enums.texture.ImageFormat;
import net.ice.curio.library.opengl.wrapper.enums.texture.TextureType;
import net.ice.curio.library.opengl.wrapper.enums.texture.parameter.FilteringParameter;
import net.ice.curio.library.opengl.wrapper.enums.texture.parameter.WrapParameter;
import net.ice.curio.library.stb.Bitmap;
import org.tinylog.Logger;

import java.nio.ByteBuffer;

import static net.ice.curio.library.opengl.wrapper.enums.texture.TextureType.TEXTURE_2D;
import static net.ice.curio.library.opengl.wrapper.enums.texture.parameter.FilteringParameter.*;
import static net.ice.curio.library.opengl.wrapper.enums.texture.parameter.WrapParameter.REPEAT;
import static org.lwjgl.opengl.ARBBindlessTexture.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_MODE;
import static org.lwjgl.opengl.GL45.*;

public class GLTexture extends Texture {

    private final int textureID;
    private final long textureHandle;

    public GLTexture(TextureFormat format, Bitmap bitmap) {
        super(bitmap);

        while(glGetError() != GL_NO_ERROR);

        this.textureID = glCreateTextures(format.type);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int levels = (int) Math.floor(log2(Math.max(width, height))) + 1;



        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTextureParameteri(textureID, GL_TEXTURE_MIN_FILTER, format.minFilter);
        glTextureParameteri(textureID, GL_TEXTURE_MAG_FILTER, format.magFilter);
        glTextureParameteri(textureID, GL_TEXTURE_WRAP_S, format.wrapS);
        glTextureParameteri(textureID, GL_TEXTURE_WRAP_T, format.wrapT);
        glTextureParameteri(textureID, GL_TEXTURE_WRAP_R, format.wrapR);

        glTextureStorage2D(textureID, levels, format.format, width, height);
        glTextureSubImage2D(textureID, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, bitmap.getData());
        glGenerateTextureMipmap(textureID);

        bitmap.cleanup();

        this.textureHandle = glGetTextureHandleARB(textureID);
        if (textureHandle == 0L) {
            Logger.error("[GLTexture]: Failed to get bindless handle for texture.");
            return;
        }

        glMakeTextureHandleResidentARB(textureHandle);

        if(!glIsTextureHandleResidentARB(textureHandle)) {
            Logger.error("[GLTexture]: Texture handle not resident: {}");
        }

        if(glGetError() != GL_NO_ERROR) {
            throw new RuntimeException("[GLTexture]: Encountered error while creating texture");
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

    public static double log2(double x) {
        return Math.log(x) / Math.log(2);
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
