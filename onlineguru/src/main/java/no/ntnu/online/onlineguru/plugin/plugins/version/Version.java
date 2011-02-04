package no.ntnu.online.onlineguru.plugin.plugins.version;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.CTCPEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.WandRepository;

public class Version implements Plugin {
	
	private WandRepository wandRepository;

	public String getDescription() {
		return "This plugin answers to the CTCP VERSION call";
	}

	public void incomingEvent(Event e) {
		if (e.getEventType() == EventType.CTCP) {
			CTCPEvent ev = (CTCPEvent)e;
			if (ev.getMessage().equals("VERSION")) {
				wandRepository.sendNoticeToTarget(ev.getNetwork(), ev.getSender(), "OnlineGuru built on Java IRClib by dotkom of Online, student association for Informatics at NTNU, Norway");
			}
		}
	}

	public void addEventDistributor(EventDistributor eventDistributor) {
		eventDistributor.addListener(this, EventType.CTCP);		
	}

	public void addWand(WandRepository wandRepository) {
		this.wandRepository = wandRepository;
	}
	
}
