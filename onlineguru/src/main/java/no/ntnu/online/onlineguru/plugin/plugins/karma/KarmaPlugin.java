package no.ntnu.online.onlineguru.plugin.plugins.karma;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.channel.Channel;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.plugins.karma.model.NetworkList;
import no.ntnu.online.onlineguru.utils.JSONStorage;
import no.ntnu.online.onlineguru.utils.Wand;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Håvard Slettvold
 */
public class KarmaPlugin implements Plugin {

    private Wand wand;

    private final String database_folder = "database/";
    private final String database_file = database_folder + "karma.json";

    private NetworkList networkList;

    private final String karmaTrigger = "!karma";
    private Pattern karmaPattern = Pattern.compile("(?:^|\\s)(\\S+)(\\+\\+|--)(?:\\s|$)");

    public KarmaPlugin() {
        networkList = (NetworkList) JSONStorage.load(database_file, NetworkList.class);
        if (networkList == null) {
            networkList = new NetworkList();
        }
    }

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
                // Save
                save_data();
                // Send reply
                if (reply != null) {
                    wand.sendMessageToTarget(pme.getNetwork(), pme.getTarget(), "[karma] "+reply);
                }
                break;
        }
    }

    private boolean save_data() {
        return JSONStorage.save(database_file, networkList);
    }

    protected String handlePrivmsg(PrivMsgEvent e) {
        String message = e.getMessage();
        if (message.startsWith(karmaTrigger)) {
            message = message.substring(karmaTrigger.length());
            if (message.isEmpty()) {
                int karma = networkList.getKarma(e.getNetwork(), e.getChannel(), e.getSender());
                return e.getSender() +"'s karma is "+ karma +".";
            }
            else {
                message = message.trim();
                Channel chan = e.getNetwork().getChannel(e.getChannel());
                if (chan.isOnChannel(message)) {
                    int karma = networkList.getKarma(e.getNetwork(), e.getChannel(), message);
                    return message +"'s karma is "+ karma +".";
                }
            }
        }
        else {
            Matcher matcher = karmaPattern.matcher(message);

            while (matcher.find()) {
                String nick = matcher.group(1);
                if (nick != null) {
                    if (nick.equals(e.getSender())) {
                        int karma = networkList.decreaseKarma(e.getNetwork(), e.getChannel(), e.getSender(), 5);
                        return nick +"'s karma has been reduced to "+ karma +" for trying to cheat.";
                    }
                    Channel chan = e.getNetwork().getChannel(e.getChannel());
                    if (chan.isOnChannel(nick)) {
                        String karmaDirection = matcher.group(2);
                        if (karmaDirection.equals("++")) {
                            networkList.increaseKarma(e.getNetwork(), e.getChannel(), nick);
                        }
                        else if (karmaDirection.equals("--")) {
                            networkList.decreaseKarma(e.getNetwork(), e.getChannel(), nick);
                        }
                    }
                }
            }
        }
        return null;
    }

}
