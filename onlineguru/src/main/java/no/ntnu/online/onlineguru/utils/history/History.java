package no.ntnu.online.onlineguru.utils.history;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.NickEvent;
import no.fictive.irclib.model.nick.Nick;

import java.util.*;


public class History {
    private Map<Nick, List<Event>> history;
    public static final int MAX_EVENTS_IN_HISTORY_PER_NICK = 5;

    public History() {
        this.history = new HashMap<Nick, List<Event>>();
    }

    public void appendHistory(Nick nick, Event event) {
        if (history.containsKey(nick)) {
            List<Event> eventsForNick = history.get(nick);

            eventsForNick.add(0, event);
            if (eventsForNick.size() > MAX_EVENTS_IN_HISTORY_PER_NICK)
                eventsForNick.remove(MAX_EVENTS_IN_HISTORY_PER_NICK);
        } else {
            history.put(nick, new ArrayList<Event>(Arrays.asList(new Event[]{event})));
        }
    }

    public void nickChangeHistory(NickEvent nickEvent) {
        if (history.containsKey(new Nick(nickEvent.getOldNick()))) {
            history.put(new Nick(nickEvent.getNewNick()), history.get(new Nick(nickEvent.getOldNick())));
            history.remove(new Nick(nickEvent.getOldNick()));
        } else {
            history.put(new Nick(nickEvent.getNewNick()), new ArrayList<Event>(Arrays.asList(new Event[]{ nickEvent })));
        }
        appendHistory(new Nick(nickEvent.getNewNick()), nickEvent);
    }

    public List<Event> getLastEvents(Nick nick) {
        if (history.containsKey(nick))
            return history.get(nick);
        else
            return new ArrayList<Event>();
    }


}
