package net.ice.curio.library.opengl.object.resource;

import net.ice.curio.graphics.object.resource.Texture;
import net.ice.curio.library.opengl.wrapper.enums.texture.ImageFormat;
import net.ice.curio.library.opengl.wrapper.enums.texture.TextureType;
import net.ice.curio.library.opengl.wrapper.enums.texture.parameter.FilteringParameter;
import net.ice.curio.library.opengl.wrapper.enums.texture.parameter.WrapParameter;
import net.ice.curio.library.stb.Bitmap;

import java.nio.ByteBuffer;

import static net.ice.curio.library.opengl.wrapper.enums.texture.TextureType.TEXTURE_2D;
import static net.ice.curio.library.opengl.wrapper.enums.texture.parameter.FilteringParameter.*;
import static net.ice.curio.library.opengl.wrapper.enums.texture.parameter.WrapParameter.REPEAT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_MODE;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class GLTexture extends Texture {

    protected final int textureHandle;

    private final TextureType textureType;

    public static final GLTexture.TextureBuilder defaultTextureSettings = new GLTexture.TextureBuilder()
            .textureType(TEXTURE_2D)
            .imageFormat(ImageFormat.RGBA8)
            .minificationFilter(NEAREST_MIPMAP_LINEAR)
            .magnificationFiler(NEAREST);

    protected GLTexture(int textureHandle, TextureType textureType, Bitmap image) {
        super(image);
        this.textureHandle = textureHandle;
        this.textureType = textureType;
    }

    protected GLTexture(GLTexture texture) {
        super(texture.getImage());
        this.textureHandle = texture.textureHandle;
        this.textureType = texture.textureType;
    }

    public void bind() {
        glBindTexture(textureType.getGLEnum(), textureHandle);
    }

    public void unbind() {
        glBindTexture(textureType.getGLEnum(), 0);
    }

    @Override
    public void cleanup() {
        glDeleteTextures(textureHandle);
    }

    public int getTextureHandle() {
        return textureHandle;
    }

    @Override
    public long getHandle() {
        return textureHandle;
    }

    public static class TextureBuilder {
        private TextureType textureType = TEXTURE_2D;
        private ImageFormat imageFormat = ImageFormat.RGBA8;
        private WrapParameter wrapParameterS = REPEAT;
        private WrapParameter wrapParameterT = REPEAT;
        private WrapParameter wrapParameterR = REPEAT;
        private FilteringParameter minificationFilter = NEAREST_MIPMAP_LINEAR;
        private FilteringParameter magnificationFiler = LINEAR;

        public TextureBuilder textureType(TextureType textureType) {
            this.textureType = textureType;
            return this;
        }

        public TextureBuilder imageFormat(ImageFormat imageFormat) {
            this.imageFormat = imageFormat;
            return this;
        }

        public TextureBuilder wrapParameterS(WrapParameter wrapParameter) {
            this.wrapParameterS = wrapParameter;
            return this;
        }

        public TextureBuilder wrapParameterT(WrapParameter wrapParameter) {
            this.wrapParameterT = wrapParameter;
            return this;
        }

        public TextureBuilder wrapParameterR(WrapParameter wrapParameter) {
            this.wrapParameterR = wrapParameter;
            return this;
        }

        public TextureBuilder minificationFilter(FilteringParameter wrapParameter) {
            this.minificationFilter = wrapParameter;
            return this;
        }

        public TextureBuilder magnificationFiler(FilteringParameter wrapParameter) {
            this.magnificationFiler = wrapParameter;
            return this;
        }

        public GLTexture buildWithData(Bitmap bitmap) {
            int textureHandle = glGenTextures();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            glBindTexture(textureType.getGLEnum(), textureHandle);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexParameteri(textureType.getGLEnum(), GL_TEXTURE_MIN_FILTER, minificationFilter.getGLEnum());
            glTexParameteri(textureType.getGLEnum(), GL_TEXTURE_MAG_FILTER, magnificationFiler.getGLEnum());
            glTexParameteri(textureType.getGLEnum(), GL_TEXTURE_WRAP_S, wrapParameterS.getGLEnum());
            glTexParameteri(textureType.getGLEnum(), GL_TEXTURE_WRAP_T, wrapParameterT.getGLEnum());
            glTexParameteri(textureType.getGLEnum(), GL_TEXTURE_WRAP_R, wrapParameterR.getGLEnum());
            glTexImage2D(textureType.getGLEnum(), 0, imageFormat.getGLEnum(), width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, bitmap.getData());

            return new GLTexture(textureHandle, textureType, bitmap);
        }

        public GLTexture buildWithDataAndMipmaps(Bitmap bitmap) {
            int textureHandle = glGenTextures();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glBindTexture(textureType.getGLEnum(), textureHandle);
            glTexParameteri(textureType.getGLEnum(), GL_TEXTURE_MIN_FILTER, minificationFilter.getGLEnum());
            glTexParameteri(textureType.getGLEnum(), GL_TEXTURE_MAG_FILTER, magnificationFiler.getGLEnum());
            glTexParameteri(textureType.getGLEnum(), GL_TEXTURE_WRAP_S, wrapParameterS.getGLEnum());
            glTexParameteri(textureType.getGLEnum(), GL_TEXTURE_WRAP_T, wrapParameterT.getGLEnum());
            glTexParameteri(textureType.getGLEnum(), GL_TEXTURE_WRAP_R, wrapParameterR.getGLEnum());
            glTexImage2D(textureType.getGLEnum(), 0, imageFormat.getGLEnum(), width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, bitmap.getData());
            glGenerateMipmap(textureType.getGLEnum());

            return new GLTexture(textureHandle, textureType, bitmap);
        }

        public GLTexture buildWithoutData(int width, int height) {
            int textureHandle = glGenTextures();

            glBindTexture(textureType.getGLEnum(), textureHandle);
            glTexParameteri(textureType.getGLEnum(), GL_TEXTURE_MIN_FILTER, minificationFilter.getGLEnum());
            glTexParameteri(textureType.getGLEnum(), GL_TEXTURE_MAG_FILTER, magnificationFiler.getGLEnum());
            glTexParameteri(textureType.getGLEnum(), GL_TEXTURE_WRAP_S, wrapParameterS.getGLEnum());
            glTexParameteri(textureType.getGLEnum(), GL_TEXTURE_WRAP_T, wrapParameterT.getGLEnum());
            glTexParameteri(textureType.getGLEnum(), GL_TEXTURE_WRAP_R, wrapParameterR.getGLEnum());
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_NONE);
            glTexImage2D(textureType.getGLEnum(), 0, imageFormat.getGLEnum(), width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);

            return new GLTexture(textureHandle, textureType, null);
        }
    }
}
