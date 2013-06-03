package no.ntnu.online.onlineguru.plugin.plugins.die;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.flags.FlagsPlugin;
import no.ntnu.online.onlineguru.utils.Wand;


public class DiePlugin implements PluginWithDependencies {

	private FlagsPlugin flagsPlugin;
	private String[] dependencies = new String[]{"FlagsPlugin",};
    private Wand wand;

    public String getDescription() {
		return "Kill the bot from irc.";
    }

    public void incomingEvent(Event e) {
        if (e.getEventType() == EventType.PRIVMSG) {
            PrivMsgEvent pme = (PrivMsgEvent) e;
            if (pme.isPrivateMessage()) {
                if (pme.getMessage().equalsIgnoreCase("die")) {
                    if (flagsPlugin.isSuperuser(pme.getNetwork(), pme.getSender())) {
                        wand.quit(pme.getNetwork());
                    }
                    else {
                        wand.sendMessageToTarget(pme.getNetwork(), pme.getSender(), "This feature requires superuser status.");
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
		if(plugin instanceof FlagsPlugin) {
			flagsPlugin = (FlagsPlugin)plugin;
		}
	}
}
