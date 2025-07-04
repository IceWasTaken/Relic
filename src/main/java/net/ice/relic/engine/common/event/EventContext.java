package net.ice.relic.engine.common.event;

public interface EventContext<Event extends Enum<Event>> {

    void addListener(Event event, EventListener listener);

    void removeListener(Event event, EventListener listener);

    void execute(Event event);
}

