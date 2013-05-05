package no.ntnu.online.onlineguru.plugin.plugins.help;

import java.util.*;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.flags.FlagsPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import no.ntnu.online.onlineguru.utils.Wand;
import org.apache.log4j.Logger;

/**
 * This plugin manages help for the bot. Help is compiled on bot compile time.
 *
 * @author Håvard Slettvold
 */

public class Help implements PluginWithDependencies {
    static Logger logger = Logger.getLogger(Help.class);

    private Wand wand;
    private FlagsPlugin flagsPlugin;

    private ArrayList<HelpItem> helpItems = new ArrayList<HelpItem>();

    /*
     * Plugin with dependencies methods
     */
    public String getDescription() {
        return "Provides information about OnlineGuru's commands.";
    }

    public void incomingEvent(Event e) {
        if (e.getEventType() == EventType.PRIVMSG) {
            handleMessage(e);
        }
    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    public void addWand(Wand wand) {
        this.wand = wand;
    }

    public String[] getDependencies() {
        return new String[]{"FlagsPlugin",};
    }

    public void loadDependency(Plugin plugin) {
        if (plugin instanceof FlagsPlugin) {
            this.flagsPlugin = (FlagsPlugin) plugin;
        }
    }

    /*
     * Event handling
     */
    private void handleMessage(Event e) {
        PrivMsgEvent pme = (PrivMsgEvent) e;
        String message = pme.getMessage();

        // Process the trigger
        if (message.split(" ")[0].equals("!help") || message.split(" ")[0].equals("!hjelp") || message.split(" ")[0].equals("??")) {
            String sender = pme.getSender();
            Network network = pme.getNetwork();

            Set<Flag> userFlags;

            if (flagsPlugin == null) {
                // If this dependency isn't loaded properly, fallback to show only triggers that anyone can see
                logger.debug("Missing dependency; FlagsPlugin. Showing basic triggers.");
                userFlags = new HashSet<Flag>();
                userFlags.add(Flag.ANYONE);
            }
            // Fetch flags for the user
            else {
                if (pme.isChannelMessage()) {
                    userFlags = flagsPlugin.getFlags(network, pme.getChannel(), sender);
                }
                else {
                    userFlags = flagsPlugin.getFlags(network, sender);
                }
            }

            // Remove the first word
            String helpTrigger = message.replaceAll("^[^\\s]+\\s?", "");

            // If there is no argument other than the help trigger, display all the triggers
            if (helpTrigger.isEmpty()) {

                wand.sendMessageToTarget(network, sender, "Here is a list of available triggers, use !help <trigger> for more info;");

                String output = "";

                for (HelpItem helpItem : helpItems) {
                    if (userFlags.contains(helpItem.getFlagRequired())) {
                        if (!output.isEmpty()) {
                            output += ", ";
                        }
                        output += helpItem.getTrigger();

                        // If length of the current output is more than 100 chars, send it
                        if (output.length() > 100) {
                            wand.sendMessageToTarget(network, sender, output);
                            output = "";
                        }
                    }
                }

                // Send the rest if there's anything
                if (!output.isEmpty()) {
                    wand.sendMessageToTarget(network, sender, output);
                }
            }
            // If there are more words, display help text for the supplied trigger
            else {
                boolean found = false;

                for (HelpItem helpItem : helpItems) {
                    if (helpItem.getTrigger().equals(helpTrigger)) {
                        if (userFlags.contains(helpItem.getFlagRequired())) {
                            for (String helpText : helpItem.getHelpText()) {
                                wand.sendMessageToTarget(network, sender, helpText);
                            }
                        }
                        else {
                            wand.sendMessageToTarget(network, sender, "You do not have the flag required to use that command.");
                        }

                        found = true;
                    }
                }

                // If no matching and allowed trigger
                if (!found) {
                    wand.sendMessageToTarget(network, sender, String.format("No help item for '%s'", helpTrigger));
                }
            }
        }
    }

	/*
     * Public methods for Help
	 */

    /**
     * This method creates help entries based on callback from plugins.
     * Current limits are 5 lines of help text and 200 characters per line of help text.
     *
     * @param helpTrigger  String trigger for the command.
     * @param flagRequired The {@link Flag} required to use the command, which will also be required to view the help entry.
     * @param helpText     String Variable Argument of help texts.
     */
    public void addHelp(String helpTrigger, Flag flagRequired, String... helpText) {
        int maxHelpTextLength = 200;

        if (helpText.length > 5) {
            logger.error("Help does not accept more than 5 lines of text per trigger. '" + helpTrigger + "'");
        }
        else {
            boolean approved = true;
            for (String text : helpText) {
                if (text.length() > maxHelpTextLength) {
                    logger.error("Help does not accept more than " + maxHelpTextLength + " characters per line of help text. '" + helpTrigger + "'");
                    approved = false;
                }
            }
            if (approved) {
                HelpItem helpItem = new HelpItem(helpTrigger, flagRequired, helpText);

                if (helpItems.contains(helpItem)) {
                    logger.error(String.format("Another help trigger already exists for %s", helpTrigger));
                }
                else {
                    helpItems.add(helpItem);
                }
            }
        }
    }

}
