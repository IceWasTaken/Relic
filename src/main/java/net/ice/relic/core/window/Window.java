package net.ice.relic.core.window;

import net.ice.relic.application.RelicApplication;
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

    protected final RelicApplication application;

    public abstract void resize(int width, int height);

    protected Window(RelicApplication application) {
        this.application = application;
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

    public long getMonitor() {
        return monitor;
    }









}
