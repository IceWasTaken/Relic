package net.ice.curio.library.glfw.events;

import net.ice.curio.library.glfw.GLFWWindow;
import net.ice.heirloom.event.Event;
import org.joml.Vector2i;

public class WindowResizeEvent extends Event {

    private final Vector2i size;
    private final GLFWWindow window;

    public WindowResizeEvent(Vector2i size, GLFWWindow window) {
        this.size = size;
        this.window = window;
    }

    public Vector2i getSize() {
        return size;
    }

    public GLFWWindow getWindow() {
        return window;
    }

}
