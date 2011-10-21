package no.ntnu.online.onlineguru.plugin.plugins.lmgtfy;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.SimpleIO;
import no.ntnu.online.onlineguru.utils.Wand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rosaloves.bitlyj.Url;
import org.apache.log4j.Logger;

import static com.rosaloves.bitlyj.Bitly.*;


/**
 * Created by IntelliJ IDEA.
 * Author: Nina Margrethe Smørsgård
 * GitHub: https://github.com/NinaMargrethe/
 * Date: 10/17/11
 */
public class LmgtfyPlugin implements Plugin {

    private final String DESCRIPTION = "Lmgtfy plugin";
    private final String LMGTFYTRIGGER = "!lmgtfy";
    private final String settings_folder = "settings/";
	private final String settings_file = settings_folder + "lmgtfy.conf";

    private Wand wand;
    private String bitlyUsername;
    private String bitlyApiKey;
    static Logger logger = Logger.getLogger(LmgtfyPlugin.class);


    public LmgtfyPlugin() {
        initiate();
    }

    private void initiate() {
        try {
            bitlyUsername = SimpleIO.loadConfig(settings_file).get("username");
            bitlyApiKey = SimpleIO.loadConfig(settings_file).get("apikey");
        } catch (IOException e) {
            logger.error(e);
        }
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
        String sublink = "";
        for (String term : message) {
            term = term.trim();
            if (term.length() > 1)
                sublink += term + "+";
        }
        link += sublink;
        link = link.substring(0, link.length() - 1);
        if (sublink.length() >= 42) {
            Url url = as(bitlyUsername, bitlyApiKey).call(shorten(link));
            link = url.getShortUrl();
        }
        return link;
    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    public void addWand(Wand wand) {
        this.wand = wand;
    }

}
