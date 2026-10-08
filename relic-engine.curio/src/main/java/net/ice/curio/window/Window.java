package net.ice.curio.window;

import net.ice.curio.Curio;
import net.ice.curio.config.RendererConfig;
import net.ice.curio.library.glfw.GLFWWindow;
import net.ice.curio.library.sdl.video.SDLWindow;
import net.ice.curio.window.enums.WindowAttribute;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.vulkan.VkInstance;

import java.nio.LongBuffer;

public abstract class Window {

    protected Curio curio;
    protected static Window INSTANCE;

    public abstract void createContext();

    public abstract boolean shouldClose();

    public abstract boolean shouldResizeWindow();
    public abstract boolean shouldResizeFramebuffer();

    public abstract Vector2i getWindowSize();
    public abstract Vector2i getFramebufferSize();

    public abstract Vector2f getWindowScale();

    public abstract void attribute(WindowAttribute attribute, int value);
    public void attribute(WindowAttribute attribute, boolean value) {
        this.attribute(attribute, value ? 1 : 0);
    }

    public abstract void createSurface(VkInstance instance, LongBuffer buffer);
    public abstract void update(float deltaTime);

    protected Window(Curio curio) {
        this.curio = curio;
    }

    public static Window getWindowContext(Curio curio) {
        return switch (RendererConfig.getWindowBackend()) {
	        case GLFW -> INSTANCE = new GLFWWindow(curio);
	        case SDL -> INSTANCE = new SDLWindow(curio);
        };
    }
}
