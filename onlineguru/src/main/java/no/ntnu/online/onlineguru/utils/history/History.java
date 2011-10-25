package no.ntnu.online.onlineguru.utils.history;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.model.network.Network;
import no.fictive.irclib.model.nick.Nick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class History {
    private Map<Nick, List<Event>> history;
    private int max_events_in_history_for_each_nick = 10;

    public History() {
        this.history = new HashMap<Nick, List<Event>>();
    }

    public void appendHistory(Nick nick, Event event) {
        if (history.containsKey(nick)) {
            List<Event> eventsForNick = history.get(nick);

            eventsForNick.add(0, event);
            if (eventsForNick.size() >= 5)
                eventsForNick.remove(5);
        } else {
            history.put(nick, new ArrayList<Event>());
        }
    }

    public void nickChangeHistory(Nick oldNick, Nick newNick) {
        if (history.containsKey(oldNick)) {
            history.put(newNick, history.get(oldNick));
        } else {
            history.put(newNick, new ArrayList<Event>());
        }
    }

    public List<Event> getLastEvents(Nick nick) {
        if (history.containsKey(nick))
            return history.get(nick);
        else
            return new ArrayList<Event>();
    }


}
