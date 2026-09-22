package net.ice.curio.input.event;

import net.ice.curio.input.enums.Action;
import net.ice.curio.input.enums.MouseButton;
import net.ice.heirloom.event.Event;
import net.ice.heirloom.event.EventRegistry;

public record MouseButtonEvent(MouseButton button, Action action) implements Event {

    @Override
    public void register(EventRegistry registry) {

    }
}
