package no.ntnu.online.onlineguru.plugin.plugins.lmgtfy;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.Wand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * Author: Nina Margrethe Smørsgård
 * GitHub: https://github.com/NinaMargrethe/
 * Date: 10/17/11
 */
public class LmgtfyPlugin implements Plugin {

    private final String DESCRIPTION = "Lmgtfy plugin";
    private final String LMGTFYTRIGGER = "!lmgtfy";

    private Wand wand;
    private String sourceUrl;

    private void Lmgtfy() {
        initiate();
    }

    private void initiate() {
        sourceUrl = "";
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public void incomingEvent(Event e) {
        switch (e.getEventType()) {
            case PRIVMSG:
                PrivMsgEvent privMsgEvent = (PrivMsgEvent) e;
                String[] message = privMsgEvent.getMessage().split("\\s+");

                if (isMessageForPlugin(message)) {
                    sendLmgtfyLink(privMsgEvent.getNetwork(), privMsgEvent.getTarget(), new ArrayList<String>(Arrays.asList(message).subList(1, message.length)));
                }
        }

    }

    private boolean isMessageForPlugin(String[] message) {
        return message != null && message.length > 1 && message[0].equalsIgnoreCase(LMGTFYTRIGGER);
    }

    private void sendLmgtfyLink(Network network, String target, ArrayList<String> message) {
        wand.sendMessageToTarget(network, target, generateLmgtfyLink(message));
    }

    public String generateLmgtfyLink(List<String> message) {
        String link = "http://lmgtfy.com/?q=";
        for (String term : message) {
            term = term.trim();
            if (term.length() > 1)
                link += term + "+";
        }
        link = link.substring(0, link.length() - 1);

        return link;
    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    public void addWand(Wand wand) {
        this.wand = wand;
    }

}
