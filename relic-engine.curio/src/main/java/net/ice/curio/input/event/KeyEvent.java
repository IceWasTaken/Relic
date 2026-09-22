package net.ice.curio.input.event;

import net.ice.curio.input.enums.Key;
import net.ice.curio.input.enums.Action;
import net.ice.heirloom.event.Event;
import net.ice.heirloom.event.EventRegistry;

public record KeyEvent(Key key, Action action) implements Event {

    @Override
    public void register(EventRegistry registry) {
    }
}
