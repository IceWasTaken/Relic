package net.ice.curio.library.glfw.events;

import net.ice.heirloom.event.Event;

public class KeyEvent extends Event {

    private final long window;
    private final int key;
    private final int scancode;
    private final int action;
    private final int mods;

    public KeyEvent(long window, int key, int scancode, int action, int mods) {
        this.window = window;
        this.key = key;
        this.scancode = scancode;
        this.action = action;
        this.mods = mods;
    }

    public long getWindow() {
        return window;
    }

    public int getKey() {
        return key;
    }

    public int getScancode() {
        return scancode;
    }

    public int getAction() {
        return action;
    }

    public int getMods() {
        return mods;
    }
}
