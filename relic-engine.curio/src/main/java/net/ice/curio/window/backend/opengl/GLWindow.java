package net.ice.curio.window.backend.opengl;

import net.ice.curio.Curio;
import net.ice.curio.library.glfw.GLFWWindow;
import net.ice.curio.library.glfw.enums.GLFWWindowHint;
import net.ice.curio.library.glfw.enums.GLFWWindowHintValues;
import net.ice.curio.library.glfw.events.WindowHintEvent;
import net.ice.curio.window.Window;
import net.ice.heirloom.event.EventListener;
import net.ice.heirloom.event.EventManager;

import java.awt.event.WindowEvent;

public class GLWindow extends GLFWWindow {

    public GLWindow(Curio curio) {
        super(curio);
    }


    public void init() {
        makeContextCurrent();
        enableVSync();
    }

    protected void setupInitHints() {

    }

    protected void setupWindowHints() {
        windowHint(GLFWWindowHint.VISIBLE, true);
        windowHint(GLFWWindowHint.OPENGL_PROFILE, GLFWWindowHintValues.OPENGL_CORE_PROFILE);
        windowHint(GLFWWindowHint.SCALE_TO_MONITOR, true);
    }


}
