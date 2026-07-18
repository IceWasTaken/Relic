package net.ice.curio.library.glfw.events;

import net.ice.curio.library.glfw.GLFWWindow;
import net.ice.curio.library.glfw.enums.GLFWWindowHint;
import net.ice.curio.library.glfw.enums.GLFWWindowHintValues;
import net.ice.heirloom.event.Event;

public class WindowHintEvent extends Event {

    private final GLFWWindow window;

    public WindowHintEvent(GLFWWindow window) {
        this.window = window;
    }

    public void windowHint(GLFWWindowHint hint, boolean value) {
        window.windowHint(hint, value);
    }

    public void windowHint(GLFWWindowHint hint, GLFWWindowHintValues value) {
        window.windowHint(hint, value);
    }

    public void windowHint(GLFWWindowHint hint, int value) {
        window.windowHint(hint, value);
    }

    public GLFWWindow getWindow() {
        return window;
    }
}
