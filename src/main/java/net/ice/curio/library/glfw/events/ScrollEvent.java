package net.ice.curio.library.glfw.events;

import net.ice.heirloom.event.Event;

public class ScrollEvent extends Event {

    private final long window;
    private final double xoffset;
    private final double yoffset;

    public ScrollEvent(long window, double xoffset, double yoffset) {
        this.window = window;
        this.xoffset = xoffset;
        this.yoffset = yoffset;
    }

    public long getWindow() {
        return window;
    }

    public double getXoffset() {
        return xoffset;
    }

    public double getYoffset() {
        return yoffset;
    }
}
