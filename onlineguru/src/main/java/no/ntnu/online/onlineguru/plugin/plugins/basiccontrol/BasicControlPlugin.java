package no.ntnu.online.onlineguru.plugin.plugins.basiccontrol;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.auth.AuthPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.flags.FlagsPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import no.ntnu.online.onlineguru.plugin.plugins.help.HelpPlugin;
import no.ntnu.online.onlineguru.utils.Wand;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Håvard Slettvold
 */
public class BasicControlPlugin implements PluginWithDependencies {

    private Wand wand;

    private FlagsPlugin flagsPlugin;
    private AuthPlugin authPlugin;

    @Override
    public String[] getDependencies() {
        return new String[]{"FlagsPlugin", "AuthPlugin", "Help", };
    }

    @Override
    public void loadDependency(Plugin plugin) {
        if (plugin instanceof FlagsPlugin) this.flagsPlugin = (FlagsPlugin) plugin;
        if (plugin instanceof AuthPlugin) this.authPlugin = (AuthPlugin) plugin;
        if (plugin instanceof HelpPlugin) {
            HelpPlugin help = (HelpPlugin) plugin;
            help.addHelp("basics-A", Flag.A, "<botnick> [nick [nick]|say <channel> [message]|auth] - Changes nick, says a message or forces auth with services. <> is only required in private.");
        }

    }

    @Override
    public String getDescription() {
        return "Basics used to control the bot.";
    }

    @Override
    public void incomingEvent(Event e) {
        switch (e.getEventType()) {
            case PRIVMSG:
                handlePrivMsgEvent((PrivMsgEvent) e);
        }
    }

    private void handlePrivMsgEvent(PrivMsgEvent e) {
        String message = e.getMessage();

        // Check if this bot was the target for the command.
        if (e.isChannelMessage()) {
            if (!message.startsWith(wand.getMyNick(e.getNetwork()))) {
                return;
            }
            else {
                // Remoce the bots nick from the string if it was there.
                message = message.substring(wand.getMyNick(e.getNetwork()).length()+1);
            }
        }

        Set<Flag> flags = flagsPlugin.getFlags(e.getNetwork(), e.getSender());

        // Requires Flag.A
        if (!flags.contains(Flag.A)) {
             wand.sendMessageToTarget(e.getNetwork(), e.getSender(), "You do not have access to this feature. It requires +A.");
             return;
        }
        else {
            if (message.startsWith("nick")) {
                wand.sendServerMessage(e.getNetwork(), "NICK "+message.split(" ")[1]);
            }
            else if (message.startsWith("auth")) {
                authPlugin.forceAuth(e.getNetwork());
            }
            else if (message.startsWith("say")) {
                if (e.isChannelMessage()) {
                    wand.sendMessageToTarget(e.getNetwork(), e.getTarget(), message.substring("say".length()+1));
                }
                else {
                    String channel = message.split(" ")[1];
                    if (channel.matches("#.*")) {
                        wand.sendMessageToTarget(e.getNetwork(), channel, message.substring(("say " + channel).length()+1));
                    }
                    else {
                        wand.sendMessageToTarget(e.getNetwork(), e.getSender(), "[say] Error: '" + channel + "' is not a valid target. Must be a #channel.");
                    }
                }
            }
        }
    }

    @Override
    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    @Override
    public void addWand(Wand wand) {
        this.wand = wand;
    }
}
