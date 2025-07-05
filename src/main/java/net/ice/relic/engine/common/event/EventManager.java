package net.ice.relic.engine.common.event;

import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager<Event extends Enum<Event>> implements EventContext<Event> {

    private final Map<Event, List<EventListener>> listeners;

    public EventManager() {
        listeners = new HashMap<>();
    }

    @Override
    public void addListener(Event event, EventListener listener) {
        if(!listeners.containsKey(event)) {
            listeners.put(event, new ArrayList<>());
        }

        listeners.get(event).add(listener);
    }

    @Override
    public void removeListener(Event event, EventListener listener) {
        List<EventListener> eventListeners = listeners.get(event);

        if(eventListeners != null) {
            eventListeners.remove(listener);
        }

        eventListeners.remove(listener);
    }

    @Override
    public void execute(Event event) {
        List<EventListener> eventListeners = listeners.get(event);

        if(eventListeners == null) {
            return;
        }

        for(EventListener listener : eventListeners) {
            try {
                listener.execute();
            } catch(Exception exception) {
                Logger.error(exception, "Error while executing event listener.");
            }
        }
    }

    public void destroy() {
        listeners.clear();
    }
}
