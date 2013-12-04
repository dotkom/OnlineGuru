package no.ntnu.online.onlineguru.plugin.plugins.flags;

import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.model.network.Network;
import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import no.ntnu.online.onlineguru.utils.Wand;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Håvard Slettvold
 */
public class CommandHandler {

    private Wand wand;
    private FlagsPlugin flagsPlugin;

    private final Flag controlFlag = Flag.f;

    Pattern flagChangePattern = Pattern.compile(
            "flags" +                               // Command
            "(?:\\s+([#&!+](?!0\\b)[^,\\s]+))?" +   // Matches a channel (optional argument)
            "\\s+([^\\s]+)" +                       // Matches a nickname
            "(?:\\s+((?:[+-]\\w+)+))?$"             // Flags to change
    );
    Pattern superuserPattern = Pattern.compile(
            "(?:superuser|su)" +            // Command
            "\\s+(?:(add)|(rem|remove))" +  // Action
            "\\s+(\\w+)"                    // Nick
    );

    public CommandHandler(FlagsPlugin flagsPlugin) {
        this.flagsPlugin = flagsPlugin;
    }

    public void setWand(Wand wand) {
        this.wand = wand;
    }

    public void handleCommand(PrivMsgEvent e) {
        // Using bots nick as trigger for these commands in public, in case there are multiple bots.
        String message = e.getMessage();
        if (e.isChannelMessage()) {
            if (!message.startsWith(wand.getMyNick(e.getNetwork()))) {
                return;
            }
        }

        String sender = e.getSender(); // sender is the one who sent the message.
        String target = e.getTarget();

        Matcher matcher = flagChangePattern.matcher(message);

        if (matcher.find()) {
            String matchedChannel;
            // Make sure we use all options to find a channel to use
            // If there was none from the command..
            if ((matchedChannel = matcher.group(1)) == null) {
                // .. try to use the channel it was triggered in.
                if (e.isChannelMessage()) {
                    matchedChannel = e.getChannel();
                }
            }

            // Verify that sender has access to change flags in the channel in question.
            if (!flagsPlugin.getFlags(e.getNetwork(), matchedChannel, sender).contains(controlFlag)) {
                wand.sendMessageToTarget(e.getNetwork(), target, "[flags] You do not have permission to use this command.");
            }
            else {
                String matchedNick = matcher.group(2);
                String matchedFlags = matcher.group(3);

                handleFlagsCommand(e.getNetwork(), target, matchedChannel, matchedNick, matchedFlags);
            }
        }

        matcher = superuserPattern.matcher(message);

        if (matcher.find()) {
            // Verify that sender has access to change flags in the channel in question.
            if (!flagsPlugin.isSuperuser(e.getNetwork(), sender)) {
                wand.sendMessageToTarget(e.getNetwork(), target, "[flags] You do not have permission to use this command.");
            }
            else {
                String matchedNick = matcher.group(1);
                boolean matchedAction = matcher.group(2) != null;

                handleSuperuserCommand(e.getNetwork(), target, matchedNick, matchedAction);
            }
        }
    }

    private void handleFlagsCommand(Network network, String target, String channel, String nick, String flags) {
        // If we got no usable channel, respond with error
        if (channel == null) {
            wand.sendMessageToTarget(network, target, "[flags] Unable to find a usable channel.");
        }
        // Check if the requested username exists.
        else if (!flagsPlugin.isUser(network, nick)) {
            wand.sendMessageToTarget(network, target, String.format("[flags] No username associated with nick '%s'.", nick));
        }
        // Flags shouldn't be set for superusers
        else {
            if (flagsPlugin.isSuperuser(network, nick)) {
                wand.sendMessageToTarget(network, target, String.format("[flags] %s is a superuser on %s.", nick, network.getServerAlias()));
            }
            else {
                // Fetch nick's flags
                Set<Flag> currentFlags = flagsPlugin.getFlags(network, channel, nick);
                if (flags == null) {
                    if (currentFlags.isEmpty()) {
                        wand.sendMessageToTarget(network, target, String.format("[flags] No flags for %s in %s.", nick, channel));
                    }
                    else {
                        wand.sendMessageToTarget(network, target, String.format("[flags] Flags for %s on channel %s are %s.", nick, channel, flagsPlugin.serializeFlags(currentFlags)));
                    }
                }
                else if (flags.matches("(?:[+-]\\w+)+")) {
                    Set<Flag> updatedFlags = flagsPlugin.updateFlags(currentFlags, flags);
                    flagsPlugin.saveFlags(network, channel, nick, updatedFlags);

                    if (updatedFlags.equals(currentFlags)) {
                        wand.sendMessageToTarget(network, target, String.format("[flags] Flags for %s unchanged.", nick));
                    }
                    else if (updatedFlags.size() > 0) {
                        wand.sendMessageToTarget(network, target, String.format("[flags] Flags for %s updated to %s.", nick, flagsPlugin.serializeFlags(updatedFlags)));
                    }
                    else {
                        wand.sendMessageToTarget(network, target, String.format("[flags] No more flags for %s in %s.", nick, channel));
                    }
                }
                else {
                    wand.sendMessageToTarget(network, target, String.format("[flags] Invalid syntax '%s'", flags));
                }
            }
        }
    }

    private void handleSuperuserCommand(Network network, String target, String nick, boolean add) {
        // Check if the requested username exists.
        if (!flagsPlugin.isUser(network, nick)) {
            wand.sendMessageToTarget(network, target, String.format("[flags] No username associated with nick '%s'.", nick));
        }
        else {
            if (add) {
                if (flagsPlugin.isSuperuser(network, nick)) {
                    wand.sendMessageToTarget(network, target, String.format("[flags] %s is already a superuser on %s.", nick, network.getServerAlias()));
                }
                else {
                    flagsPlugin.addSuperuser(network, nick);
                    wand.sendMessageToTarget(network, target, String.format("[flags] %s added as superuser on %s.", nick, network.getServerAlias()));
                }
            }
            else {
                if (flagsPlugin.isSuperuser(network, nick)) {
                    flagsPlugin.removeSuperuser(network, nick);
                    wand.sendMessageToTarget(network, target, String.format("[flags] Removed superuser status for %s on %s.", nick, network.getServerAlias()));
                }
                else {
                    wand.sendMessageToTarget(network, target, String.format("[flags] %s is not a superuser on %s.", nick, network.getServerAlias()));
                }
            }
        }
    }
}
