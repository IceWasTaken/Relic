package net.ice.curio.library.glfw.events;

import net.ice.curio.library.glfw.GLFWWindow;
import net.ice.curio.library.glfw.enums.GLFWWindowHint;
import net.ice.curio.library.glfw.enums.GLFWWindowHintValues;
import net.ice.heirloom.event.Event;
import net.ice.heirloom.event.EventRegistry;

public record WindowHintEvent(GLFWWindow window) implements Event {

    public void windowHint(GLFWWindowHint hint, boolean value) {
        window.windowHint(hint, value);
    }

    public void windowHint(GLFWWindowHint hint, GLFWWindowHintValues value) {
        window.windowHint(hint, value);
    }

    public void windowHint(GLFWWindowHint hint, int value) {
        window.windowHint(hint, value);
    }

    @Override
    public void register(EventRegistry registry) {

    }
}
