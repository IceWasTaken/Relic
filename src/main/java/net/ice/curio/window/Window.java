package net.ice.curio.window;

import net.ice.curio.config.RendererConfig;
import net.ice.curio.library.glfw.GLFWWindow;
import net.ice.curio.window.backend.opengl.GLWindow;
import net.ice.curio.window.backend.vulkan.VulkanWindow;
import net.ice.heirloom.Lifecycle;
import org.joml.Vector2i;

public abstract class Window implements Lifecycle {

    protected final GLFWWindow window;

    protected Window() {
        this.window = new GLFWWindow();
    }

    public static Window getBackend() {
        return switch (RendererConfig.getBackendType()) {
            case OPENGL -> new GLWindow();
            case VULKAN -> new VulkanWindow();
        };
    }

    @Override
    public void update(float deltaTime) {
        window.update();
    }

    public void resize(Vector2i size) {
        window.resize(size.x, size.y);
    }
    public void resize(int x, int y) {
        window.resize(x, y);
    }

    public boolean shouldClose() {
        return window.shouldWindowClose();
    }

    public int getWidth() {
        return window.getWidth();
    }
    public int getHeight() {
        return window.getHeight();
    }
    public Vector2i getSize() {
        return window.getSize();
    }

    public GLFWWindow getWindow() {
        return window;
    }

}
