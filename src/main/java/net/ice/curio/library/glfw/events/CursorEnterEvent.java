package net.ice.curio.library.glfw.events;

import net.ice.heirloom.event.Event;

public class CursorEnterEvent extends Event {

    private final long window;
    private final boolean entered;

    public CursorEnterEvent(long window, boolean entered) {
        this.window = window;
        this.entered = entered;
    }

    public boolean isEntered() {
        return entered;
    }
}
