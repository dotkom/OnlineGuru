package no.ntnu.online.onlineguru.plugin.plugins.regex;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.channel.Channel;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.history.HistoryPlugin;
import no.ntnu.online.onlineguru.utils.Wand;
import no.ntnu.online.onlineguru.utils.history.History;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roy Sindre Norangshol
 */
public class RegexPlugin implements PluginWithDependencies {
    private History history;
    private static final String DESCRIPTION = "Plugin to do quick regex and pattern matching with tadada";
    private Wand wand;

    private final String TRIGGER = "s/";
    private final String TRIGGER_TROLL = "troll/";
    private final Pattern FETCH_REGEX = Pattern.compile(".*\\/([^/]+)/([^/]+)\\/");

    public RegexPlugin(Wand wand, History history) {
        this.wand = wand;
        this.history = history;
    }

    @Override
    public String[] getDependencies() {
        return new String[]{"HistoryPlugin"};
    }

    public void loadDependency(Plugin plugin) {
        if (plugin instanceof HistoryPlugin)
            history = ((HistoryPlugin) plugin).getHistory();
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public void incomingEvent(Event e) {
        switch (e.getEventType()) {
            case PRIVMSG: {
                PrivMsgEvent privMsgEvent = (PrivMsgEvent) e;
                if (privMsgEvent.isChannelMessage()) {
                    if (privMsgEvent.getMessage().startsWith(TRIGGER))
                        sendFixedMessage(handleMessage(TRIGGER, privMsgEvent), privMsgEvent);
                    else if (privMsgEvent.getMessage().startsWith(TRIGGER_TROLL))
                        sendFixedMessage(handleMessage(TRIGGER_TROLL, privMsgEvent), privMsgEvent);
                }
            }
        }
    }

    private void sendFixedMessage(String fixedMessage, PrivMsgEvent privMsgEvent) {
        if (fixedMessage != null)
            wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getTarget(), fixedMessage);
    }


    protected String handleMessage(String triggerUsed, PrivMsgEvent privMsgEvent) {
        //String message = privMsgEvent.getMessage().substring(triggerUsed.length()-1, privMsgEvent.getMessage().length());
        String message = privMsgEvent.getMessage();
        String fixed;


        List<PrivMsgEvent> lastLines = history.getLastChannelEvents(new Channel(privMsgEvent.getChannel()));
        for (PrivMsgEvent line : lastLines) {
            if (isSkippingMessage(triggerUsed, privMsgEvent, line)) // skips message if normal trigger is used, but message in history is not his.
                continue;

            String last = line.getMessage();
            fixed = new String(last); // resets it every time

            Matcher matcher = FETCH_REGEX.matcher(message);
            if (matcher.matches()) {
                fixed = last.replaceAll(matcher.group(1), matcher.group(2));
            }
            if (!last.equals(fixed))
                return String.format("<%s> %s", line.getSender(), fixed);

        }
        return null;
    }

    private boolean isSkippingMessage(String triggerUsed, PrivMsgEvent privMsgEvent, PrivMsgEvent line) {
        return TRIGGER.equalsIgnoreCase(triggerUsed) && !line.getSender().equalsIgnoreCase(privMsgEvent.getSender());
    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    public void addWand(Wand wand) {
        this.wand = wand;
    }
}
