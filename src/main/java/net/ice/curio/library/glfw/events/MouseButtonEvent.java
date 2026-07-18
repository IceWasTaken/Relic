package net.ice.curio.library.glfw.events;

import net.ice.heirloom.event.Event;
import net.ice.heirloom.event.EventRegistry;

public class MouseButtonEvent implements Event {

    private final long window;
    private final int button;
    private final int action;
    private final int mods;

    public MouseButtonEvent(long window, int button, int action, int mods) {
        this.window = window;
        this.button = button;
        this.action = action;
        this.mods = mods;
    }

    public long getWindow() {
        return window;
    }

    public int getButton() {
        return button;
    }

    public int getAction() {
        return action;
    }

    public int getMods() {
        return mods;
    }

    @Override
    public void register(EventRegistry registry) {

    }
}
