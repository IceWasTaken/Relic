package net.ice.curio.library.glfw;

import imgui.ImGui;
import imgui.ImGuiIO;
import net.ice.curio.config.RendererConfig;
import net.ice.curio.config.enums.BackendType;
import net.ice.curio.library.glfw.enums.GLFWInitHint;
import net.ice.curio.library.glfw.enums.GLFWPlatform;
import net.ice.curio.library.glfw.enums.GLFWWindowHint;
import net.ice.curio.library.glfw.enums.GLFWWindowHintValues;
import net.ice.curio.library.glfw.events.*;
import net.ice.heirloom.Lifecycle;
import net.ice.heirloom.event.EventManager;
import org.joml.Vector2i;
import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkInstance;
import org.tinylog.Logger;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static net.ice.curio.library.glfw.enums.GLFWInitHint.PLATFORM;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;

public final class GLFWWindow implements Lifecycle {

    private int width = 1280;
    private int height = 720;
    private boolean resized = false;

    private long monitor;
    private long windowHandle;

    private final GLFWWindowProperties windowProperties;

    private GLFWKeyCallback keyCallback;
    private GLFWMouseButtonCallback mouseButtonCallback;
    private GLFWScrollCallback scrollCallback;
    private GLFWCursorPosCallback mousePosCallback;
    private GLFWCharCallback charCallback;

    public GLFWWindow() {
        this.windowProperties = new GLFWWindowProperties();

        initHint(windowProperties.getGlfwPlatform());


        EventManager.execute(new InitHintEvent());

        if(!glfwInit()) {
            throw new RuntimeException("GLFW: Failed to initialize GLFW.");
        }

        this.monitor = glfwGetPrimaryMonitor();

        glfwDefaultWindowHints();
        glfwWindowHintString(GLFW_WAYLAND_APP_ID, "net.ice.curio.library.glfw.GLFWWindow");
        EventManager.execute(new WindowHintEvent(this));

        this.windowHandle = glfwCreateWindow(width, height, windowProperties.getTitle(), 0, 0);

        if(windowHandle == 0) {
            glfwTerminate();
            throw new RuntimeException("GLFW: Failed to create window.");
        }

        IntBuffer widthBuffer = MemoryUtil.memCallocInt(1);
        IntBuffer heightBuffer = MemoryUtil.memCallocInt(1);
        glfwGetMonitorPos(monitor, widthBuffer, heightBuffer);
        int w = glfwGetVideoMode(monitor).width() / 2 + widthBuffer.get() - width / 2;
        int h = (glfwGetVideoMode(monitor).height() / 2) + heightBuffer.get() - height / 2;
        setWindowPosition(w, h);

        setupCallbacks();
    }

    @Override
    public void update() {
        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }

    @Override
    public void cleanup() {
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);


        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }



    private void setupCallbacks() {
        glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> resize(width, height));
        glfwSetErrorCallback((int errorCode, long msgPtr) -> Logger.error("GLFW Error: [{}], [{}]", errorCode, MemoryUtil.memUTF8(msgPtr)));

        glfwSetKeyCallback(windowHandle, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                EventManager.execute(new KeyEvent(window, key, scancode, action, mods));
            }
        });

        glfwSetScrollCallback(windowHandle, scrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                EventManager.execute(new ScrollEvent(window, xoffset, yoffset));
            }
        });

        glfwSetCursorPosCallback(windowHandle, mousePosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                EventManager.execute(new CursorEvent(window, xpos, ypos));
            }
        });

        glfwSetMouseButtonCallback(windowHandle, mouseButtonCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                EventManager.execute(new MouseButtonEvent(window, button, action, mods));
            }
        });

        glfwSetCursorEnterCallback(windowHandle, (window, entered) -> EventManager.execute(new CursorEnterEvent(window, entered)));

        glfwSetCharCallback(windowHandle, (handle, c) -> {
            ImGuiIO io = ImGui.getIO();
            if (!io.getWantCaptureKeyboard()) {
                return;
            }
            io.addInputCharacter(c);
        });
    }

    public void setWindowPosition(int x, int y) {
        if(windowProperties.getGlfwPlatform() == GLFWPlatform.WAYLAND) {
            Logger.error("GLFW: Tried to set window position on Wayland backend.");
            return;
        }

        glfwSetWindowPos(windowHandle, x, y);
    }

    public void makeContextCurrent() {
        glfwMakeContextCurrent(windowHandle);
    }

    public void createWindowSurface(VkInstance vkInstance, LongBuffer handleBuffer) {
        if(RendererConfig.getBackendType() == BackendType.VULKAN) {
            glfwCreateWindowSurface(vkInstance, windowHandle, null, handleBuffer);
        }
    }

    public void enableVSync() {
        glfwSwapInterval(1);
    }
    public void disableVSync() {
        glfwSwapInterval(0);
    }

    public void initHint(GLFWInitHint initHint, boolean value) {
        glfwInitHint(initHint.getGlfwEnum(), value ? 1 : 0);
    }
    public void initHint(GLFWPlatform platform) {
        glfwInitHint(PLATFORM.getGlfwEnum(), platform.getGLFWEnum());
    }

    public void windowHint(GLFWWindowHint hint, boolean value) {
        glfwWindowHint(hint.getGLFWEnum(), value ? 1 : 0);
    }
    public void windowHint(GLFWWindowHint hint, int value) {
        glfwWindowHint(hint.getGLFWEnum(), value);
    }
    public void windowHint(GLFWWindowHint hint, GLFWWindowHintValues value) {
        glfwWindowHint(hint.getGLFWEnum(), value.getGlfwEnum());
    }

    public void setWidth(int width) {
        this.resize(width, height);
    }
    public void setHeight(int height) {
        this.resize(width, height);
    }
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        this.resized = true;
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public Vector2i getSize() {
        return new Vector2i(width, height);
    }

    public boolean isResized() {
        if(resized) {
            this.resized = false;
            return true;
        }
        return false;
    }

    public boolean shouldWindowClose() {
        return glfwWindowShouldClose(windowHandle);
    }

}
