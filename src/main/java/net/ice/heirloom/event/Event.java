package net.ice.heirloom.event;

import net.ice.heirloom.register.autoregister.Registerable;

public interface Event extends Registerable<Event, EventRegistry> {

    @Override
    void register(EventRegistry registry);

}
