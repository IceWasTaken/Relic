package net.ice.heirloom.event;

public interface EventContext<Event extends Enum<Event>> {

    void addListener(Event event, EventListener listener);

    void removeListener(Event event, EventListener listener);

    void execute(Event event);
}

