package net.ice.curio.graphics.context;

import net.ice.curio.Curio;
import net.ice.curio.config.RendererConfig;
import net.ice.curio.graphics.object.Viewport;
import net.ice.curio.graphics.object.resource.Texture;
import net.ice.curio.library.opengl.OpenGLContext;
import net.ice.curio.library.stb.Bitmap;
import net.ice.curio.library.vulkan.VulkanContext;
import net.ice.curio.window.Window;
import org.joml.Vector2i;

public abstract class GraphicsContext {

    protected Curio curio;
    protected GraphicsContextLogger graphicsContextLogger;

    protected static GraphicsContext INSTANCE;

    public abstract void init();

    protected GraphicsContext(Curio curio) {
        this.curio = curio;
    }

    public abstract Viewport createViewport(int width, int height);
    public Viewport createViewport(Vector2i size) {
        return createViewport(size.x, size.y);
    }

    public abstract void setupWindowAttributes(Window window);

    protected abstract GraphicsContextLogger createContextLogger();

    public abstract Texture createTexture(Bitmap bitmap);

    public static GraphicsContext getGraphicsContext(Curio curio) {

        if (INSTANCE == null) {
            INSTANCE = switch(RendererConfig.getBackendType()) {
                case OPENGL -> new OpenGLContext(curio);
                case VULKAN -> new VulkanContext(curio);
            };
        }
        return INSTANCE;
    }

    public Curio getCurio() {
        return curio;
    }


}