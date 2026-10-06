package net.ice.curio.library.opengl;

import net.ice.curio.Curio;
import net.ice.curio.graphics.context.GraphicsContext;
import net.ice.curio.graphics.context.GraphicsContextLogger;
import net.ice.curio.graphics.exception.UnsupportedGraphicsContextException;
import net.ice.curio.graphics.object.Viewport;
import net.ice.curio.graphics.object.resource.Texture;
import net.ice.curio.library.opengl.object.GLViewport;
import net.ice.curio.library.opengl.object.resource.GLSamplers;
import net.ice.curio.library.opengl.object.resource.GLTexture;
import net.ice.curio.library.stb.Bitmap;
import net.ice.curio.system.SystemInfo;
import net.ice.curio.window.Window;
import net.ice.curio.window.enums.WindowAttribute;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.tinylog.Logger;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.sdl.SDLVideo.SDL_GL_CONTEXT_PROFILE_CORE;

public class OpenGLContext extends GraphicsContext {

    private GLCapabilities capabilities;

    private GLSamplers samplers;

    public OpenGLContext(Curio curio) {
        super(curio);
    }


    @Override
    public void init() {
        this.curio.getWindow().createContext();
        this.capabilities = GL.createCapabilities();

        SystemInfo.logGLInfo();

        checkCapability(capabilities.OpenGL42, "OpenGL backend requires an OpenGL 4.2 capable context");
        checkCapability(capabilities.GL_ARB_gpu_shader_int64, "OpenGL backend requires GL_ARB_gpu_shader_int64 extension");
        checkCapability(capabilities.GL_ARB_bindless_texture, "OpenGL backend requires GL_ARB_bindless_texture extension");
        checkCapability(capabilities.GL_ARB_texture_filter_anisotropic, "OpenGL backend requires GL_ARB_texture_filter_anisotropic extension");
        checkCapability(capabilities.GL_ARB_direct_state_access, "OpenGL backend requires GL_ARB_direct_state_access extension");
        checkCapability(capabilities.GL_ARB_clip_control, "OpenGL backend requires GL_ARB_clip_control extension");
        checkCapability(capabilities.GL_ARB_buffer_storage, "OpenGL backend requires GL_ARB_buffer_storage extension");
        checkCapability(capabilities.GL_ARB_compute_shader, "OpenGL backend requires GL_ARB_compute_shader extension");
        checkCapability(capabilities.GL_ARB_shader_storage_buffer_object, "OpenGL backend requires GL_ARB_shader_storage_buffer extension");
        checkCapability(capabilities.GL_ARB_framebuffer_no_attachments, "OpenGL backend requires GL_ARB_framebuffer_no_attachments extension");
        checkCapability(capabilities.GL_ARB_multi_draw_indirect, "OpenGL backend requires ARB_multi_draw_indirect extension");

        if(!capabilities.GL_KHR_debug) {
            Logger.error("GL_KHR_debug not supported. Logging will lack detailed GL information.");
        }

        detectMemoryInfoExtension();

        this.samplers = new GLSamplers();
    }

    public void cleanup() {
        GL.destroy();
        capabilities = null;
    }

    @Override
    public void setupWindowAttributes(Window window) {
        window.attribute(WindowAttribute.CONTEXT_PROFILE, SDL_GL_CONTEXT_PROFILE_CORE);
        window.attribute(WindowAttribute.CONTEXT_VERSION_MAJOR, 4);
        window.attribute(WindowAttribute.CONTEXT_VERSION_MINOR, 2);
        window.attribute(WindowAttribute.CONTEXT_DEBUG, true);
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
                samplers.SAMPLER_TRILINEAR_FILTERING,
                bitmap
        );
    }

    public void clearErrors() {
        while(glGetError() != GL_NO_ERROR);
    }

    private void checkCapability(boolean capability, String msg) {
        if(!capability) {
            throw new UnsupportedGraphicsContextException("[OpenGLContext] " + msg);
        }
    }

    public GLSamplers getSamplers() {
        return samplers;
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

