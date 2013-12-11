package no.ntnu.online.onlineguru.plugin.control;

import java.util.ArrayList;
import java.util.HashMap;

import no.fictive.irclib.event.container.*;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.model.Plugin;

public class EventDistributor {

    private HashMap<EventType, ArrayList<Plugin>> listCollection = new HashMap<EventType, ArrayList<Plugin>>();


    public void addListener(Plugin plugin, EventType eventType) {
        if(!listCollection.containsKey(eventType)) {
            listCollection.put(eventType, new ArrayList<Plugin>());
        }

        listCollection.get(eventType).add(plugin);
    }

    public void removeListener(Plugin plugin, EventType eventType) {
        if(!listCollection.containsKey(eventType)) return;

        listCollection.get(eventType).remove(plugin);
    }

    public synchronized void handleEvent(Event event) {
        if(!listCollection.containsKey(event.getEventType())) return;

        fireEvent(event, listCollection.get(event.getEventType()));

    }

    synchronized void fireEvent(Event e, ArrayList<Plugin> listeners) {
        if (!listeners.isEmpty()) {
            for(Plugin plugin : listeners) {
                plugin.incomingEvent(e);
            }
        }
    }
}