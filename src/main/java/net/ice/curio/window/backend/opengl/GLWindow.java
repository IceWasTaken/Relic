package net.ice.curio.window.backend.opengl;

import net.ice.curio.library.glfw.enums.GLFWWindowHint;
import net.ice.curio.library.glfw.enums.GLFWWindowHintValues;
import net.ice.curio.library.glfw.events.WindowHintEvent;
import net.ice.curio.window.Window;
import net.ice.heirloom.event.EventListener;

public class GLWindow extends Window {

    public GLWindow() {
    }

    @Override
    public void init() {
        window.init();
        window.makeContextCurrent();
        window.enableVSync();
    }

    @EventListener
    public void setupWindowHints(WindowHintEvent event) {
        event.windowHint(GLFWWindowHint.VISIBLE, true);
        event.windowHint(GLFWWindowHint.CONTEXT_VERSION_MAJOR, 4);
        event.windowHint(GLFWWindowHint.CONTEXT_VERSION_MINOR, 6);
        event.windowHint(GLFWWindowHint.OPENGL_PROFILE, GLFWWindowHintValues.OPENGL_CORE_PROFILE);
        event.windowHint(GLFWWindowHint.OPENGL_DEBUG_CONTEXT, true);
    }
}
