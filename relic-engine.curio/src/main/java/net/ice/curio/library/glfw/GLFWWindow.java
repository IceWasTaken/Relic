package net.ice.curio.library.glfw;

import imgui.ImGui;
import imgui.ImGuiIO;
import net.ice.curio.Curio;
import net.ice.curio.input.event.CursorEnterEvent;
import net.ice.curio.input.event.CursorEvent;
import net.ice.curio.input.event.KeyEvent;
import net.ice.curio.input.event.MouseButtonEvent;
import net.ice.curio.library.glfw.enums.GLFWInitHint;
import net.ice.curio.library.glfw.enums.GLFWPlatform;
import net.ice.curio.library.glfw.enums.GLFWWindowHint;
import net.ice.curio.library.glfw.enums.GLFWWindowHintValues;
import net.ice.curio.library.glfw.events.*;
import net.ice.curio.window.Window;
import net.ice.curio.window.enums.WindowAttribute;
import net.ice.heirloom.event.EventManager;
import org.joml.Vector2i;
import org.lwjgl.glfw.*;
import org.lwjgl.system.MemoryUtil;
import org.tinylog.Logger;

import java.nio.IntBuffer;

import static net.ice.curio.library.glfw.enums.GLFWInitHint.PLATFORM;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.memFree;

public class GLFWWindow extends Window {

    protected int width = 1280;
    protected int height = 720;

    protected boolean resized = false;

    protected long monitor;
    protected long windowHandle;

    protected final GLFWWindowProperties windowProperties;

    protected GLFWErrorCallback errorCallback;
    protected GLFWFramebufferSizeCallback framebufferSizeCallback;
    protected GLFWKeyCallback keyCallback;
    protected GLFWMouseButtonCallback mouseButtonCallback;
    protected GLFWScrollCallback scrollCallback;
    protected GLFWCursorPosCallback mousePosCallback;
    protected GLFWCursorEnterCallback cursorEnterCallback;
    protected GLFWCharCallback charCallback;

    public GLFWWindow(Curio curio) {
	    super(curio);

		this.windowProperties = new GLFWWindowProperties();

        initHint(windowProperties.getGlfwPlatform());

        if(!glfwInit()) {
            throw new RuntimeException("GLFW: Failed to initialize GLFW.");
        }

        glfwSetErrorCallback(errorCallback = new GLFWErrorCallback() {
            @Override
            public void invoke(int error, long description) {
                Logger.error("GLFW Error: [{}], [{}]", error, MemoryUtil.memUTF8(description));
            }
        });

        this.monitor = glfwGetPrimaryMonitor();

        glfwDefaultWindowHints();

        switch (windowProperties.getGlfwPlatform()) {
            case WAYLAND -> glfwWindowHintString(GLFW_WAYLAND_APP_ID, "net.ice.curio.library.glfw.GLFWWindow");
            case X11 -> {
                glfwWindowHintString(GLFW_X11_CLASS_NAME, "net.ice.curio.library.glfw.GLFWWindow");
                glfwWindowHintString(GLFW_X11_INSTANCE_NAME, "net.ice.curio.library.glfw.GLFWWindow");
            }
        }

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

        memFree(widthBuffer);
        memFree(heightBuffer);

        glfwPollEvents();

        widthBuffer = MemoryUtil.memCallocInt(1);
        heightBuffer = MemoryUtil.memCallocInt(1);
        glfwGetFramebufferSize(windowHandle, widthBuffer, heightBuffer);
        resize(widthBuffer.get(0), heightBuffer.get(0));
        memFree(widthBuffer);
        memFree(heightBuffer);


        setupCallbacks();
    }

    @Override
    public void update(float deltaTime) {
        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }

    public void cleanup() {
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);

        glfwTerminate();
    }

    public void refreshName() {
        glfwSetWindowTitle(windowHandle, windowProperties.getTitle());
    }

    private void setupCallbacks() {
        glfwSetFramebufferSizeCallback(windowHandle, framebufferSizeCallback = new  GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                resize(width, height);
            }
        });

        glfwSetKeyCallback(windowHandle, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                EventManager.execute(new KeyEvent(GLFWKey.toKey(key), GLFWKey.toAction(action)));
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
                EventManager.execute(new CursorEvent(xpos, ypos));
            }
        });

        glfwSetMouseButtonCallback(windowHandle, mouseButtonCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                EventManager.execute(new MouseButtonEvent(GLFWKey.toMouseButton(button), GLFWKey.toAction(action)));
            }
        });

        glfwSetCursorEnterCallback(windowHandle, cursorEnterCallback = new GLFWCursorEnterCallback() {
            @Override
            public void invoke(long window, boolean entered) {
                EventManager.execute(new CursorEnterEvent(entered));
            }
        });

        glfwSetCharCallback(windowHandle, charCallback = new GLFWCharCallback() {
            @Override
            public void invoke(long window, int c) {
                ImGuiIO io = ImGui.getIO();
                if (!io.getWantCaptureKeyboard()) {
                    return;
                }
                io.addInputCharacter(c);
            }
        });
    }

    public void setWindowPosition(int x, int y) {
        if(windowProperties.getGlfwPlatform() == GLFWPlatform.WAYLAND) {
            Logger.error("[GLFWWindow]: Tried to set window position on Wayland backend.");
            return;
        }

        glfwSetWindowPos(windowHandle, x, y);
    }

    public void makeContextCurrent() {
        glfwMakeContextCurrent(windowHandle);
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

    @Override
    public void attribute(WindowAttribute attribute, int value) {
        glfwWindowHint(getAttribute(attribute), value);
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

    public Vector2i getWindowSize() {
        IntBuffer widthBuffer = MemoryUtil.memAllocInt(1);
        IntBuffer heightBuffer = MemoryUtil.memAllocInt(1);
        glfwGetWindowSize(windowHandle, widthBuffer, heightBuffer);
        Vector2i size = new Vector2i(widthBuffer.get(0), heightBuffer.get(0));
        memFree(widthBuffer);
        memFree(heightBuffer);
        return size;
    }

    public Vector2i getFramebufferSize() {
        IntBuffer widthBuffer = MemoryUtil.memAllocInt(1);
        IntBuffer heightBuffer = MemoryUtil.memAllocInt(1);
        glfwGetFramebufferSize(windowHandle, widthBuffer, heightBuffer);
        Vector2i size = new Vector2i(widthBuffer.get(0), heightBuffer.get(0));
        memFree(widthBuffer);
        memFree(heightBuffer);
        return size;
    }

    @Override
    public boolean shouldResize() {
        if(resized) {
            this.resized = false;
            return true;
        }
        return false;
    }

    private static int getAttribute(WindowAttribute attribute) {
        return switch (attribute) {
			case CONTEXT_VERSION_MAJOR -> GLFW_CONTEXT_VERSION_MAJOR;
	        case CONTEXT_VERSION_MINOR -> GLFW_CONTEXT_VERSION_MINOR;
	        case CONTEXT_PROFILE -> GLFW_OPENGL_PROFILE;
			case CONTEXT_DEBUG -> GLFW_CONTEXT_DEBUG;
        };
    }


    @Override
    public boolean shouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public GLFWWindowProperties getWindowProperties() {
        return windowProperties;
    }
}
