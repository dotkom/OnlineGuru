package no.ntnu.online.onlineguru.plugin.plugins.regex;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.channel.Channel;
import no.fictive.irclib.model.nick.Nick;
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
    private final Pattern FETCH_REGEX = Pattern.compile("^(troll|s)\\/([^/]+)/([^/]+)\\/");

    public RegexPlugin() {
    }

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
                    else if (privMsgEvent.getMessage().startsWith(TRIGGER_TROLL) && new Nick(privMsgEvent.getSender()).isOnChannel(privMsgEvent.getChannel()))
                        sendFixedMessage(handleMessage(TRIGGER_TROLL, privMsgEvent), privMsgEvent);
                }
            }
        }
    }

    private void sendFixedMessage(String fixedMessage, PrivMsgEvent privMsgEvent) {
        if (fixedMessage != null)
            wand.sendMessageToTarget(privMsgEvent.getNetwork(), privMsgEvent.getTarget(), fixedMessage);
    }


    protected String handleMessage(String triggerUsed, PrivMsgEvent seenRequestEvent) {
        //String message = privMsgEvent.getMessage().substring(triggerUsed.length()-1, privMsgEvent.getMessage().length());
        String message = seenRequestEvent.getMessage();
        String fixed;


        List<PrivMsgEvent> lastLines = history.getLastChannelEvents(new Channel(seenRequestEvent.getChannel()));
        for (PrivMsgEvent line : lastLines) {

            if (isSkippingMessageIfSameAsHistoryMessage(message, line))
                continue;
            else if (isSkippingMessageWhenNormalTriggerAndNotMyMessage(triggerUsed, seenRequestEvent, line)) // skips message if normal trigger is used, but message in history is not his.
                continue;

            String lastHistoryMessage = new String(line.getMessage());
            fixed = new String(lastHistoryMessage); // resets it every time

            Matcher matcher = FETCH_REGEX.matcher(message);
            if (matcher.matches()) {
                String from = matcher.group(2);
                String to = matcher.group(3);

                if (!isMatchingTrigger(lastHistoryMessage)) {
                    fixed = lastHistoryMessage.replaceAll(from, to);
                }
            }
            if (!lastHistoryMessage.equals(fixed))
                return String.format("<%s> %s", line.getSender(), fixed);
        }


        return null;
    }

    private boolean isSkippingMessageIfSameAsHistoryMessage(String message, PrivMsgEvent line) {
        return line.getMessage().equalsIgnoreCase(message);
    }

    private boolean isMatchingTrigger(String message) {
        Matcher matcher = FETCH_REGEX.matcher(message);
        return matcher.matches();
    }

    private boolean isSkippingMessageWhenNormalTriggerAndNotMyMessage(String triggerUsed, PrivMsgEvent privMsgEvent, PrivMsgEvent line) {
        return TRIGGER.equalsIgnoreCase(triggerUsed) && !line.getSender().equalsIgnoreCase(privMsgEvent.getSender());
    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    public void addWand(Wand wand) {
        this.wand = wand;
    }
}
