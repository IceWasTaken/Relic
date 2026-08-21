package net.ice.curio.library.opengl;

import net.ice.curio.Curio;
import net.ice.curio.graphics.context.GraphicsContext;
import net.ice.curio.graphics.context.GraphicsContextLogger;
import net.ice.curio.graphics.exception.UnsupportedGraphicsContextException;
import net.ice.curio.graphics.object.Viewport;
import net.ice.curio.graphics.object.resource.Texture;
import net.ice.curio.library.opengl.object.GLViewport;
import net.ice.curio.library.opengl.object.resource.GLTexture;
import net.ice.curio.library.stb.Bitmap;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.tinylog.Logger;

import static org.lwjgl.opengl.GL11.*;

public class OpenGLContext extends GraphicsContext {

    private GLCapabilities capabilities;

    public OpenGLContext(Curio curio) {
        super(curio);
    }

    @Override
    public void init() {
        this.capabilities = GL.createCapabilities();

        checkCapability(capabilities.OpenGL46, "OpenGL backend requires an OpenGL 4.6 capable driver & GPU");
        checkCapability(capabilities.GL_ARB_bindless_texture, "OpenGL backend requires GL_ARB_bindless_texture extension");
        checkCapability(capabilities.GL_ARB_gpu_shader_int64, "OpenGL backend requires GL_ARB_gpu_shader_int64 extension");

        detectMemoryInfoExtension();
    }

    @Override
    public void cleanup() {
        GL.destroy();
        capabilities = null;
    }

    @Override
    public GraphicsContextLogger createContextLogger() {
        return new OpenGLContextLogger();
    }

    @Override
    public Viewport createViewport(int width, int height) {
        return new GLViewport(0, 0, width, height, true);
    }

    @Override
    public Texture createTexture(Bitmap bitmap) {
        return new GLTexture(
                new GLTexture.TextureFormat(
                        GL_TEXTURE_2D,
                        GL_RGBA8,
                        GL_REPEAT,
                        GL_REPEAT,
                        GL_REPEAT,
                        GL_LINEAR_MIPMAP_LINEAR,
                        GL_NEAREST
                ),
                bitmap
        );
    }

    private void checkCapability(boolean capability, String msg) {
        if(!capability) {
            throw new UnsupportedGraphicsContextException("[OpenGLContext] " + msg);
        }
    }

    private void detectMemoryInfoExtension() {
        if(capabilities.GL_NVX_gpu_memory_info) {
            Logger.info("[OpenGLContext] Using GL_NVX_gpu_memory_info extension for graphics memory information.");

            return;
        }
        if(capabilities.GL_ATI_meminfo) {
            Logger.info("[OpenGLContext] Using GL_ATI_meminfo extension for graphics memory information.");

            return;
        }
        Logger.error("[OpenGLContext] Unable to find suitable graphics memory info extension. No graphics memory usage info will be available.");
    }
}

