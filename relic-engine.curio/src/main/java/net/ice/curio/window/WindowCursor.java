package net.ice.curio.window;

import static org.lwjgl.glfw.GLFW.*;

//more of a cursor manager, but i don't care to change it
public class WindowCursor {

    protected long NWCursorHandle;
    protected long NECursorHandle;
    protected long AllCursorHandle;
    protected long standardCursorHandle;
    protected long horizontalCursorHandle;
    protected long verticalCursorHandle;

    private final long windowHandle;

    public WindowCursor(long windowHandle) {
        this.windowHandle = windowHandle;

        this.standardCursorHandle = glfwCreateStandardCursor(GLFW_CURSOR);
        this.horizontalCursorHandle = glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR);
        this.verticalCursorHandle = glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR);
        this.NWCursorHandle = glfwCreateStandardCursor(GLFW_RESIZE_NWSE_CURSOR);
        this.NECursorHandle = glfwCreateStandardCursor(GLFW_RESIZE_NESW_CURSOR);
        this.AllCursorHandle = glfwCreateStandardCursor(GLFW_RESIZE_ALL_CURSOR);
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
}
