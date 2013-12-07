package no.ntnu.online.onlineguru.plugin.plugins.karma;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.Wand;

/**
 * @author Håvard Slettvold
 */
public class KarmaPlugin implements Plugin {

    private Wand wand;

    @Override
    public String getDescription() {
        return "Each nick in a channel can have a karma associated with it to show the persons trust level.";
    }

    @Override
    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    @Override
    public void addWand(Wand wand) {
        this.wand = wand;
    }

    @Override
    public void incomingEvent(Event e) {
        switch (e.getEventType()) {
            case PRIVMSG:
                PrivMsgEvent pme = (PrivMsgEvent) e;
                String reply = handlePrivmsg(pme);
                if (reply != null) {
                    wand.sendMessageToTarget(pme.getNetwork(), pme.getTarget(), reply);
                }
                break;
        }
    }

    private String handlePrivmsg(PrivMsgEvent e) {
        return null;
    }
}
