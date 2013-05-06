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
import org.apache.log4j.Logger;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Ideen til denne pluginen tilhører Roy Sindre Norangshol. Fullstendig skrevet om av Håvard Slettvold.
 *
 * @author Håvard Slettvold
 */
public class RegexPlugin implements PluginWithDependencies {
    static Logger logger = Logger.getLogger(RegexPlugin.class);

    private History history;
    private static final String DESCRIPTION = "Plugin implements sed for you first match in the channel history.";
    private Wand wand;

    private final Pattern SED_PATTERN = Pattern.compile("^s(.)((?:(?!\\1).)+?)\\1((?:(?!\\1).)*?)\\1((?:[ig]|\\d+)*)$");

    public RegexPlugin() {
        // Need empty constructor.
    }

    public RegexPlugin(Wand wand, History history) {
        System.out.println("this never happens");
            this.wand = wand;
        this.history = history;
    }

    /*
     * Inherited from PluginWithDependencies
     */

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

    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    public void addWand(Wand wand) {
        this.wand = wand;
    }

    public void incomingEvent(Event e) {
        switch (e.getEventType()) {
            case PRIVMSG: {
                handlePrivMsgEvent((PrivMsgEvent)e);
            }
        }
    }

    private void handlePrivMsgEvent(PrivMsgEvent e) {
        if (e.isChannelMessage()) {
            Matcher matcher = SED_PATTERN.matcher(e.getMessage());

            if (matcher.find()) {
                handleSed(e, matcher);
            }
        }

    }

    private void handleSed(PrivMsgEvent e, Matcher matcher) {
        String separator = matcher.group(1);
        String replacement = matcher.group(3);
        String flags = matcher.group(4);

        Pattern regex = null;

        try {
            regex = Pattern.compile(matcher.group(2));
        } catch (PatternSyntaxException pse) {
            String[] error = pse.getMessage().split("\n");
            for (String part : error) {
                logger.debug(part);
            }
        }

        if (regex == null) {
            wand.sendMessageToTarget(e.getNetwork(), e.getTarget(), "[sed] The Regular Expression pattern could not be compiled.");
        }
        else {
            String lastMatchingMessage = getLastMatchingLineFromHistory(e, regex);

            if (lastMatchingMessage.isEmpty()) {
                wand.sendMessageToTarget(e.getNetwork(), e.getTarget(), "[sed] Found no match to your search.");
            }
            else {
                // Doing the actual replacement.
                String fixedMessage = lastMatchingMessage.replaceAll(matcher.group(2), replacement);

                wand.sendMessageToTarget(e.getNetwork(), e.getTarget(), String.format("[sed] <%s> %s", e.getSender(), fixedMessage));
            }
        }
    }

    private String getLastMatchingLineFromHistory(PrivMsgEvent e, Pattern regex) {
        List<PrivMsgEvent> lastLines = history.getLastChannelEvents(new Channel(e.getChannel()));

        String currentMessage;
        Matcher commandMatcher;
        Matcher replacementMatcher;

        for (PrivMsgEvent currentEvent : lastLines) {
            // Skip if this message doesn't belong to the nick that used sed.
            if (!e.getSender().equals(currentEvent.getSender())) {
                continue;
            }

            currentMessage = new String(currentEvent.getMessage());
            commandMatcher = SED_PATTERN.matcher(currentMessage);

            // If it matches the sed command, we will not replace in that message.
            if (commandMatcher.find()) {
                continue;
            }

            replacementMatcher = regex.matcher(currentMessage);

            if (replacementMatcher.find()) {
                return currentMessage;
            }
        }

        return "";
    }

}
