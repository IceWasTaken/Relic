package net.ice.curio.input.event;

import net.ice.heirloom.event.Event;
import net.ice.heirloom.event.EventRegistry;

public record CursorEvent(double xpos, double ypos) implements Event {

    @Override
    public void register(EventRegistry registry) {

    }
}
