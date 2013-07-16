package no.ntnu.online.onlineguru.plugin.plugins.basiccontrol;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.flags.FlagsPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import no.ntnu.online.onlineguru.utils.Wand;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Håvard Slettvold
 */
public class BasicControlPlugin implements PluginWithDependencies {

    private Wand wand;

    private FlagsPlugin flagsPlugin;

    private Pattern basicPattern;

    @Override
    public String[] getDependencies() {
        return new String[]{"FlagsPlugin", };
    }

    @Override
    public void loadDependency(Plugin plugin) {
        if (plugin instanceof FlagsPlugin) this.flagsPlugin = (FlagsPlugin) plugin;
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
        if (flags.contains(Flag.A)) {
            if (message.startsWith("nick")) {
                wand.sendServerMessage(e.getNetwork(), "NICK "+message.split(" ")[1]);
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
