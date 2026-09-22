package net.ice.curio.window;

import net.ice.curio.Curio;
import net.ice.curio.config.RendererConfig;
import net.ice.curio.library.glfw.GLFWWindow;
import net.ice.curio.library.sdl.video.SDLWindow;
import net.ice.curio.window.backend.opengl.GLWindow;
import net.ice.curio.window.backend.vulkan.VulkanWindow;
import net.ice.curio.window.enums.WindowAttribute;

public abstract class Window {

    protected Curio curio;
    protected static Window INSTANCE;

    public abstract int getWidth();
    public abstract int getHeight();
    public abstract boolean shouldClose();
    public abstract boolean shouldResize();

    public abstract void attribute(WindowAttribute attribute, int value);
    public void attribute(WindowAttribute attribute, boolean value) {
        this.attribute(attribute, value ? 1 : 0);
    }

    public abstract void update(float deltaTime);

    protected Window(Curio curio) {
        this.curio = curio;
    }

    public static Window getWindowContext(Curio curio) {
        return switch (RendererConfig.getWindowBackend()) {
	        case GLFW -> new GLFWWindow(curio);
	        case SDL -> new SDLWindow(curio);
        };
    }







}
