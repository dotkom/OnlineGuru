package no.ntnu.online.onlineguru.plugin.plugins.lmgtfy;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.URLShortener;
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

    public static final String LMGTFY_BASE = "http://lmgtfy.com/?q=";
    private final String DESCRIPTION = "Lmgtfy plugin";
    private final String LMGTFYTRIGGER = "!lmgtfy";

    private Wand wand;

    public LmgtfyPlugin() {

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

    private void sendLmgtfyLink(final Network network, final String target, final ArrayList<String> message) {
        new Thread(new Runnable() {
            public void run() {
                String link = generateLmgtfyLink(message);

                String sublink = link.substring(LMGTFY_BASE.length(), link.length());
                if (sublink.length() >= 42)
                    link = URLShortener.bitlyfyLink(link);

                wand.sendMessageToTarget(network, target, link);
            }
        }).start();
    }

    public String generateLmgtfyLink(List<String> message) {
        String link = LMGTFY_BASE;

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
