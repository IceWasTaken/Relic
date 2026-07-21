package net.ice.curio.library.glfw.events;

import net.ice.heirloom.event.Event;
import net.ice.heirloom.event.EventRegistry;

public class CursorEvent implements Event {

    private final long window;
    private final double xpos;
    private final double ypos;

    public CursorEvent(long window, double xpos, double ypos) {
        this.window = window;
        this.xpos = xpos;
        this.ypos = ypos;
    }

    public long getWindow() {
        return window;
    }

    public double getXpos() {
        return xpos;
    }

    public double getYpos() {
        return ypos;
    }

    @Override
    public void register(EventRegistry registry) {

    }
}
