package net.ice.relic;

import net.ice.relic.annotations.Rewrite;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.config.configs.WindowConfig;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;
import org.tinylog.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

@Rewrite(reason = "vulkan support")
public class Window {

    private long windowHandle = NULL;

    private int width;
    private int height;
    private String title;
    private long monitor;

    private boolean isInitialized;

    private GLFWFramebufferSizeCallback framebufferSizeCallback;

    private final RelicApplication application;

    public Window(RelicApplication application) {
        this.application = application;

        isInitialized = false;
    }

    public void init() throws RuntimeException {
        WindowConfig config = application.getConfig().getWindowConfig();

        if(!glfwInit()) {
            throw new RuntimeException("Failed to initialize GLFW.");
        }

        this.width = config.getWidth();
        this.height = config.getHeight();
        this.title = config.getTitle();
        this.monitor = glfwGetPrimaryMonitor();
        GLFWVidMode vidMode = glfwGetVideoMode(monitor);

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_SAMPLES, 4);
        glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_SAMPLES, 4);

        this.windowHandle = glfwCreateWindow(width, height, title, config.isFullscreen() ? monitor : NULL, NULL);

        if(this.windowHandle == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create GLFW window.");
        }
        glfwSetWindowPos(windowHandle, vidMode.width() / 3, vidMode.height() / 5);
        glfwMakeContextCurrent(windowHandle);

        if(config.isVsync()) {
            glfwSwapInterval(1);
        }

        glfwSetFramebufferSizeCallback(windowHandle, new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                setSize(width, height);
                application.getCurrentScene().getMatrix().updateProjMatrix(width, height);
                application.getRenderer().resize();
            }
        });

        glfwSetErrorCallback((int errorCode, long msgPtr) ->
                Logger.error("Error code [{}], msg [{}]", errorCode, MemoryUtil.memUTF8(msgPtr))
        );

        this.isInitialized = true;
    }

    public void update() {
        if(!isInitialized) {
            throw new IllegalStateException("Window has not been initialized yet.");
        }

        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }

    public void destroy() {
        if(!isInitialized) {
            return;
        }

        framebufferSizeCallback.free();
        glfwDestroyWindow(windowHandle);
    }

    public boolean shouldClose() {
        if(!isInitialized) {
            return false;
        }

        return glfwWindowShouldClose(windowHandle);
    }


    public void refreshSize() {
        glViewport(0, 0, width, height);
    }

    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public Vector2i getWindowSize() {
        return new Vector2i(width, height);
    }

    //default setters / getters

    public long getWindowHandle() {
        return windowHandle;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public long getMainMonitor() {
        return monitor;
    }

    public String getCurrentTitle() {
        return title;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
