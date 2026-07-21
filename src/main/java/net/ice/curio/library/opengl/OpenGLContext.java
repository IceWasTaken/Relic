package net.ice.curio.library.opengl;

import net.ice.curio.Curio;
import net.ice.curio.graphics.context.GraphicsContext;
import net.ice.curio.graphics.exception.UnsupportedGraphicsContextException;
import net.ice.curio.graphics.object.Viewport;
import net.ice.curio.graphics.object.resource.Texture;
import net.ice.curio.library.opengl.object.GLViewport;
import net.ice.curio.library.opengl.object.resource.GLTexture;
import net.ice.curio.library.stb.Bitmap;
import net.ice.curio.library.opengl.object.resource.BindlessTexture;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

public class OpenGLContext extends GraphicsContext {

    private GLCapabilities capabilities;

    public OpenGLContext(Curio curio) {
        super(curio);
    }

    @Override
    public void init() {
        this.capabilities = GL.createCapabilities();

        if(!capabilities.OpenGL46) {
            throw new UnsupportedGraphicsContextException("OpenGL backend requires an OpenGL 4.6 capable GPU");
        }
        if(!capabilities.GL_ARB_bindless_texture) {
            throw new UnsupportedGraphicsContextException("OpenGL backend requires GL_ARB_bindless_texture extension");
        }
        if(!capabilities.GL_ARB_gpu_shader_int64) {
            throw new UnsupportedGraphicsContextException("OpenGL backend requires GL_ARB_gpu_shader_int64");
        }
    }

    @Override
    public Viewport createViewport(int width, int height) {
        return new GLViewport(width, height);
    }

    @Override
    public Texture createTexture(Bitmap bitmap) {
        return new BindlessTexture(GLTexture.defaultTextureSettings.buildWithDataAndMipmaps(bitmap));
    }
}

