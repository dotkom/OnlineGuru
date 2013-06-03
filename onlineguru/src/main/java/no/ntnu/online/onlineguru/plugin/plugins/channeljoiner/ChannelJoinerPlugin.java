package no.ntnu.online.onlineguru.plugin.plugins.channeljoiner;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.flags.FlagsPlugin;
import no.ntnu.online.onlineguru.plugin.plugins.flags.model.Flag;
import no.ntnu.online.onlineguru.utils.Wand;

/**
 * @author Håvard Slettvold
 */
public class ChannelJoinerPlugin implements PluginWithDependencies {

    private Wand wand;
    private FlagsPlugin flagsPlugin;
    private Flag controlFlag = Flag.a;
    private String[] dependencies = new String[]{"FlagsPlugin",};

    public String getDescription() {
        return "Joins channels on command.";
    }

    public void incomingEvent(Event e) {
        if (e.getEventType() == EventType.PRIVMSG) {
            PrivMsgEvent pme = (PrivMsgEvent) e;
            if (pme.isPrivateMessage()) {
                String[] parts = pme.getMessage().split("\\s");
                if (parts.length == 2 && parts[0].equalsIgnoreCase("join")) {
                    String channel = parts[1];
                    if (flagsPlugin.getFlags(pme.getNetwork(), channel, pme.getSender()).contains(controlFlag)) {
                        wand.join(e.getNetwork(), channel);
                    }
                    else {
                        wand.sendMessageToTarget(e.getNetwork(), pme.getSender(), "You have insufficient access to use this command. +a required.");
                    }
                }
                if (parts.length == 2 && parts[0].equalsIgnoreCase("part")) {
                    String channel = parts[1];
                    if (flagsPlugin.getFlags(pme.getNetwork(), channel, pme.getSender()).contains(controlFlag)) {
                        wand.part(e.getNetwork(), channel);
                    }
                    else {
                        wand.sendMessageToTarget(e.getNetwork(), pme.getSender(), "You have insufficient access to use this command. +a required.");
                    }
                }
            }
        }
    }

    public void addEventDistributor(EventDistributor eventDistributor) {
        eventDistributor.addListener(this, EventType.PRIVMSG);
    }

    public void addWand(Wand wand) {
        this.wand = wand;
    }

    public String[] getDependencies() {
        return dependencies;
    }

    public void loadDependency(Plugin plugin) {
        if (plugin instanceof FlagsPlugin) {
            flagsPlugin = (FlagsPlugin) plugin;
        }
    }
}
