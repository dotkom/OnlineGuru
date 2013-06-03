package no.ntnu.online.onlineguru.plugin.plugins.channeljoiner;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;
import no.ntnu.online.onlineguru.plugin.plugins.chanserv.control.ChanServ;
import no.ntnu.online.onlineguru.utils.Wand;

public class ChannelJoinerPlugin implements Plugin, PluginWithDependencies{

	private EventDistributor eventDistributor;
	private Wand wand;
	private ChanServ chanServ;
	private String[] dependencies = new String[] { "ChanServ" };
	
	public String getDescription() {
		return "Joins a channel.";
	}

	public void incomingEvent(Event e) {
		if(e.getEventType() == EventType.PRIVMSG) {
			PrivMsgEvent privmsg = (PrivMsgEvent)e;
			
			if(privmsg.isPrivateMessage()) {
			
				String[] parts = privmsg.getMessage().split("\\s");
				if(parts.length == 2 && parts[0].equalsIgnoreCase("join")) {
					if(this.chanServ.isNickLoggedIn(privmsg.getSender())) {
						String channel = parts[1];
						wand.join(e.getNetwork(), channel);
					} else {
						wand.sendMessageToTarget(e.getNetwork(), privmsg.getSender(), "You are not logged in.");
					}
				}
				if(parts.length == 2 && parts[0].equalsIgnoreCase("part")) {
					if(this.chanServ.isNickLoggedIn(privmsg.getSender())) {
						String channel = parts[1];
						wand.part(e.getNetwork(), channel);
					} else {
						wand.sendMessageToTarget(e.getNetwork(), privmsg.getSender(), "You are not logged in.");
					}
				}
			}
		}
	}

	public void addEventDistributor(EventDistributor eventDistributor) {
		this.eventDistributor = eventDistributor;
		this.eventDistributor.addListener(this, EventType.PRIVMSG);
		this.eventDistributor.addListener(this, EventType.NUMERIC);
	}

	public void addWand(Wand wand) {
		this.wand = wand;
	}
	
	public String[] getDependencies() {
		return dependencies;
	}

	public void loadDependency(Plugin plugin) {
		if(plugin instanceof ChanServ) {
			chanServ = (ChanServ) plugin;
		}
	}
}
