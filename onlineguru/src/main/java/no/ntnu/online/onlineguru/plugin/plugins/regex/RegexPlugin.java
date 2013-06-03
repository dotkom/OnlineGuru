package no.ntnu.online.onlineguru.plugin.plugins.regex;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.channel.Channel;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import no.ntnu.online.onlineguru.plugin.plugins.help.HelpPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.history.HistoryPlugin;
import no.ntnu.online.onlineguru.utils.Wand;
import no.ntnu.online.onlineguru.service.services.history.History;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * The idea for this plugin belongs to Roy Sindre Norangshol. Completely rewritten by Håvard Slettvold.
 *
 * @author Håvard Slettvold
 */
public class RegexPlugin implements PluginWithDependencies {
    static Logger logger = Logger.getLogger(RegexPlugin.class);

    private History history;
    private static final String DESCRIPTION = "Plugin implements sed for your first match in the channel history.";
    private Wand wand;

    private final Pattern SED_PATTERN = Pattern.compile(
            "^s(.)" +                       // First group contains a single character, which is the separator.
            "((?:(?!\\1).|\\\\\\1)+?)" +    // Matches any character that isn't group 1, or an escaped group 1, needs to me 1 or more occurrences.
            "\\1" +                         // Separator.
            "((?:(?!\\1).|\\\\\\1)*?)" +    // Matches any character that isn't group 1, or an escaped group 1, can be empty.
            "\\1" +                         // Separator.
            "(" +                           // Flags, group 4
            "(?:[igv]" +                     // Matches ignore case (i) or replace all (g)
            "|" +                           // Or
            "(?:(?!\\d\\D+?\\d)\\d)+" +     // One or more digits that aren't followed by one or more nondigits and then another digit.
            ")*" +                          // Can be empty
            ")$"
    );
    // The whole pattern with no comments or java escapes:
    // ^s(.)((?:(?!\1).|\\\1)+?)\1((?:(?!\1).|\\\1)*?)\1((?:[igv]|(?:(?!\d\D+?\d)\d)+)*)$

    public RegexPlugin() {
        // Need empty constructor.
    }

    public RegexPlugin(Wand wand, History history) {
        this.wand = wand;
        this.history = history;
    }

    /*
     * Inherited from PluginWithDependencies
     */

    public String[] getDependencies() {
        return new String[]{"HistoryPlugin", "HelpPlugin"};
    }

    public void loadDependency(Plugin plugin) {
        if (plugin instanceof HistoryPlugin) {
            history = ((HistoryPlugin) plugin).getHistory();
        }
        if (plugin instanceof HelpPlugin) {
            HelpPlugin help = (HelpPlugin) plugin;
            help.addHelp(
                    "sed",
                    Flag.ANYONE,
                    "s/<regex>/<replace text>/igv\\d+ - This trigger closely resembles sed from unix operating systems.",
                    "Flags: [i: ignore case] [g: global search, replace all matches] [v: verbose] [\\d+: a positive integer. Replace only Nth word, or with 'g' flag, replace from N and out.]"
            );
        }
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
                handlePrivMsgEvent((PrivMsgEvent) e);
            }
        }
    }

    private void handlePrivMsgEvent(PrivMsgEvent e) {
        if (e.isChannelMessage()) {
            // At least filter out some unnecessary checking.
            if (e.getMessage().startsWith("s")) {
                String reply = handleSed(e);
                if (reply != null) {
                    wand.sendMessageToTarget(e.getNetwork(), e.getTarget(), "[sed] "+reply);
                }
            }
        }

    }

    protected String handleSed(PrivMsgEvent e) {
        Matcher matcher = SED_PATTERN.matcher(e.getMessage());

        if (!matcher.find()) {
            return null;
        }

        String separator = matcher.group(1);
        String replacement = matcher.group(3);
        String flags = matcher.group(4);
        boolean replaceAll = false;
        boolean verbose = false;
        String occurrence = "";
        Pattern regex = null;

        try {
            int patternFlags = 0;
            String ignoreCaseFlag = "";
            for (char flag : flags.toCharArray()) {
                switch (flag) {
                    case 'i':
                        // Need to use inline flags because this regex will be used in String.replaceAll()
                        ignoreCaseFlag = "(?i)";
                        break;
                    case 'g':
                        replaceAll = true;
                        break;
                    case 'v':
                        verbose = true;
                        break;
                    default:
                        occurrence += flag;
                }
            }
            regex = Pattern.compile(ignoreCaseFlag + matcher.group(2));
        } catch (PatternSyntaxException pse) {
            String[] error = pse.getMessage().split("\n");
            for (String part : error) {
                logger.debug(part);
            }
        }

        if (regex == null) {
            return verbose ? "The Regular Expression pattern could not be compiled." : null;
        }
        else {
            String lastMatchingMessage = getLastMatchingLineFromHistory(e, regex);

            if (lastMatchingMessage.isEmpty()) {
                return verbose ? "Found no match to your search." : null;
            }
            else {
                String fixedMessage;
                try {
                    // Doing the actual replacement.
                    if (!occurrence.isEmpty()) {
                        Matcher searches = regex.matcher(lastMatchingMessage);
                        StringBuffer sb = new StringBuffer();
                        int runs = 0;
                        int occurrences = Integer.parseInt(occurrence);

                        while (searches.find()) {
                            runs++;
                            if (runs >= occurrences) {
                                searches.appendReplacement(sb, replacement);
                                if (!replaceAll) {
                                    break;
                                }
                            }
                        }
                        searches.appendTail(sb);
                        fixedMessage = sb.toString();
                    }
                    else if (replaceAll) {
                        fixedMessage = lastMatchingMessage.replaceAll(regex.pattern(), replacement);
                    }
                    else {
                        fixedMessage = lastMatchingMessage.replaceFirst(regex.pattern(), replacement);
                    }
                } catch (IndexOutOfBoundsException ex) {
                    logger.error("Supplied a replacement group without defining the approriate amount of groups.", ex.getCause());
                    return verbose ? ex.getMessage() + ". Define the matching group." : null;
                }

                if (fixedMessage.length() > 400) {
                    return verbose ? "ERROR: Replaced pattern was longer than 400 characters." : null;
                }
                else {
                    return String.format("<%s> %s", e.getSender(), fixedMessage);
                }
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
