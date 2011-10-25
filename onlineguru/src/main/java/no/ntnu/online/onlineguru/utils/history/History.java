package no.ntnu.online.onlineguru.utils.history;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.NickEvent;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.model.channel.Channel;
import no.fictive.irclib.model.nick.Nick;

import java.util.*;


public class History {
    private Map<Nick, List<Event>> history;
    private HashMap<Channel, List<PrivMsgEvent>> channelHistory;
    public static final int MAX_EVENTS_IN_HISTORY_PER_NICK = 5;
    public static final int MAX_EVENTS_IN_CHANNEL_HISOTRY = 15;


    public History() {
        this.history = new HashMap<Nick, List<Event>>();
        this.channelHistory = new HashMap<Channel, List<PrivMsgEvent>>();
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

    public void appendChannelHistory(Channel channel, PrivMsgEvent event) {
        if (channelHistory.containsKey(channel)) {
            List<PrivMsgEvent> eventsForChannel = channelHistory.get(channel);

            eventsForChannel.add(0, event);
            if (eventsForChannel.size() > MAX_EVENTS_IN_CHANNEL_HISOTRY)
                eventsForChannel.remove(MAX_EVENTS_IN_CHANNEL_HISOTRY);
        } else {
            channelHistory.put(channel, new ArrayList<PrivMsgEvent>(Arrays.asList(new PrivMsgEvent[]{ event })));
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

    public List<PrivMsgEvent> getLastChannelEvents(Channel channel) {
        if (channelHistory.containsKey(channel))
            return channelHistory.get(channel);
        else
            return new ArrayList<PrivMsgEvent>();
    }


}
