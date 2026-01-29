package com.audtream.desktop.model.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventBus {
    public static EventBus instance;
    private Map<Class<?>, List<Consumer>> listeners = new HashMap<>();

    private EventBus() {}

    public static EventBus getInstance() {
        if (instance == null) instance = new EventBus();
        return instance;
    }

    public <T> void subscribe(Class<T> eventType, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public <T> void publish(T event) {
        Class<?> eventClass = event.getClass();
        List<Consumer> eventListeners = listeners.get(eventClass);
        if (eventListeners != null) {
            eventListeners.forEach(listener -> listener.accept(event));
        }
    }
}
