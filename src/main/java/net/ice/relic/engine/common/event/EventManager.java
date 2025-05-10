package net.ice.relic.engine.common.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private final List<Object> listeners = new ArrayList<>();

    public void register(Object listener) {
        listeners.add(listener);
    }

    //TODO: Allow multiple events on one listener
    //eh, maybe just abstraction instead
    //i'll decide later (never)
    public void dispatch(Object event) {
        for(Object listener : listeners) {
            Method[] methods = listener.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if(method.isAnnotationPresent(EventListener.class)) {
                    if(method.getParameterCount() == 1 && method.getParameterTypes()[0].isAssignableFrom(event.getClass())) {
                        try {
                            method.invoke(listener, event);
                        } catch (Exception exception) {
                            throw new RuntimeException("Error while firing event.");
                        }
                    }
                }
            }
        }
    }
}
