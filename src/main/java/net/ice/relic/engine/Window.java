package net.ice.relic.engine;

import net.ice.relic.engine.common.Clock;
import net.ice.relic.engine.common.Input;
import net.ice.relic.engine.opengl.scene.Scene;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private final long windowHandle;
    private final RelicApplication relic;
    private final Clock clock;
    private final WindowOptions options;

    public Window(WindowOptions options, RelicApplication relic) {

        this.options = options;

        this.relic = relic;

        this.clock = new Clock();

        if(!glfwInit()) {
            throw new RuntimeException("Could not initialize GLFW.");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_SRGB_CAPABLE, GLFW_TRUE);
        this.windowHandle = glfwCreateWindow(options.width, options.height, options.title, NULL, NULL);

        if(windowHandle == NULL) {
            throw new RuntimeException("Failed to initialize window.");
        }

        glfwMakeContextCurrent(windowHandle);
        glfwSwapInterval(options.vsync() ? GLFW_TRUE : GLFW_FALSE);
        glfwShowWindow(windowHandle);

        glfwSetKeyCallback(windowHandle, (windowHandle, key, scancode, action, mods) -> {
            if (key >= 0 && key <= GLFW_KEY_LAST) {
                if (action == GLFW_PRESS) {
                    Input.keysDown.add(key);
                } else if (action == GLFW_RELEASE) {
                    Input.keysDown.remove(key);
                }
            }
        });
        GL.createCapabilities();

        glEnable(GL_FRAMEBUFFER_SRGB);

        clock.timerInit();
    }

    public Clock getClock() {
        return clock;
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public RelicApplication getRelic() {
        return relic;
    }

    public WindowOptions getOptions() {
        return options;
    }

    public record WindowOptions(int width, int height, String title, boolean vsync) {
        public WindowOptions changeOptions(int width, int height, String title, boolean vsync) {
            return new WindowOptions(width, height, title, vsync);
        }
    }
}
