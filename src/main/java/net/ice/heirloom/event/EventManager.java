package net.ice.heirloom.event;

import org.tinylog.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {

    private static final Map<Class<?>, List<Listener>> listeners = new HashMap<>();

    public static void addListener(Class<?> listener) {
        for (Method method : listener.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(EventListener.class)) {
                continue;
            }

            if (!Modifier.isStatic(method.getModifiers())) {
                Logger.error("[EventManager] Listener method {} must be static.", method.getName());
            }

            if (method.getParameterCount() != 1 || !Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                Logger.error("[EventManager] Listener method {} is invalid.", method.getName());
                continue;
            }

            Class<?> eventType = method.getParameterTypes()[0];

            listeners.computeIfAbsent(eventType, k -> {
                ArrayList<Listener> arrayList = new ArrayList<>();
                arrayList.add(new Listener(null, method));
                Logger.info("[EventManager] Added new listener: {}", method.getName());
                return arrayList;

            });

        }
    }

    public static void addListener(Object listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(EventListener.class)) {
                continue;
            }

            if (method.getParameterCount() != 1 || !Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                Logger.error("[EventManager] Listener method {} is invalid.", method.getName());
                continue;
            }

            Class<?> eventType = method.getParameterTypes()[0];
            listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(new Listener(listener, method));
        }
    }

//    public void removeListener(Event event, EventListener listener) {
//        List<Listener> eventListeners = listeners.get(event);
//
//        if(eventListeners != null) {
//            eventListeners.remove(listener);
//        }
//
//        eventListeners.remove(listener);
//    }

    public static Event execute(Event event) {
        List<Listener> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (Listener listener : eventListeners) {
                try {
                    listener.method.invoke(listener.obj, event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return event;
    }

    public void destroy() {
        listeners.clear();
    }

    private record Listener(Object obj, Method method){}
}
