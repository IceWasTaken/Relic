package net.ice.relic.engine;

import net.ice.relic.engine.common.Clock;
import net.ice.relic.engine.common.Input;
import org.tinylog.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private long windowHandle;
    private int width, height;
    private Relic relic;
    public String title;
    private final Clock clock;

    public Window(int width, int height, String title, Relic relic) {

        this.width = width;
        this.height = height;
        this.title = title;

        this.clock = new Clock();

        if(!glfwInit()) {
            throw new RuntimeException("Could not initialize GLFW.");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_SRGB_CAPABLE, GLFW_TRUE);
        this.windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);

        if(windowHandle == NULL) {
            throw new RuntimeException("Failed to initialize window.");
        }

        glfwMakeContextCurrent(windowHandle);
        glfwSwapInterval(GLFW_TRUE);
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

        createCapabilities();

        glEnable(GL_FRAMEBUFFER_SRGB);

        relic.initShaders();
        relic.init(this);

        clock.timerInit();

        relic.loop();

        relic.close();
    }

    //should i have used lombok?
    //yes
    //is it too late?
    //no
    //will i?
    //no

    public Clock getClock() {
        return clock;
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public Relic getRelic() {
        return relic;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
