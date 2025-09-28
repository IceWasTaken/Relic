package net.ice.relic.core.window;

import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.config.configs.WindowConfig;
import net.ice.relic.core.interfaces.Initializable;
import net.ice.relic.core.interfaces.Updatable;
import org.joml.Vector2i;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public abstract class Window implements Initializable, Updatable {

    protected int width;
    protected int height;
    protected long monitor = NULL;
    protected long windowHandle = NULL;
    protected long NWCursorHandle;
    protected long NECursorHandle;
    protected long AllCursorHandle;
    protected long standardCursorHandle;
    protected long horizontalCursorHandle;
    protected long verticalCursorHandle;
    protected boolean initialized = false;

    protected String title;
    protected final WindowConfig config;
    protected final RelicApplication application;

    protected abstract void backendInit();
    public abstract void resize(int width, int height);

    protected Window(RelicApplication application) {
        this.application = application;
        this.config = application.getConfig().getWindowConfig();

        if(!glfwInit()) {
            throw new RuntimeException("GLFW: Failed to initialize.");
        }
    }

    @Override
    public void init() {

        this.width = config.getWidth();
        this.height = config.getHeight();
        this.title = config.getTitle();
        this.monitor = glfwGetPrimaryMonitor();

        glfwDefaultWindowHints();

        this.standardCursorHandle = glfwCreateStandardCursor(GLFW_CURSOR);
        this.horizontalCursorHandle = glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR);
        this.verticalCursorHandle = glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR);
        this.NWCursorHandle = glfwCreateStandardCursor(GLFW_RESIZE_NWSE_CURSOR);
        this.NECursorHandle = glfwCreateStandardCursor(GLFW_RESIZE_NESW_CURSOR);
        this.AllCursorHandle = glfwCreateStandardCursor(GLFW_RESIZE_ALL_CURSOR);

        backendInit();
    }

    public boolean shouldClose() {
        if(!initialized) {
            return false;
        }

        return glfwWindowShouldClose(windowHandle);
    }

    public void destroy() {
        if(!initialized) {
            return;
        }

        glfwDestroyWindow(windowHandle);
    }

    public void resize(Vector2i size) {
        resize(size.x, size.y);
    }

    public void setCursorShapeStandard() {
        glfwSetCursor(windowHandle, standardCursorHandle);
    }
    public void setCursorShapeHorizontal() {
        glfwSetCursor(windowHandle, horizontalCursorHandle);
    }
    public void setCursorShapeVertical() {
        glfwSetCursor(windowHandle, verticalCursorHandle);
    }
    public void setCursorShapeNWSE() {
        glfwSetCursor(windowHandle, NWCursorHandle);
    }
    public void setCursorShapeNESW() {
        glfwSetCursor(windowHandle, NECursorHandle);
    }
    public void setCursorShapeAll() {
        glfwSetCursor(windowHandle, AllCursorHandle);
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    public Vector2i getSize() {
        return new Vector2i(width, height);
    }

    public long getMonitor() {
        return monitor;
    }









}
