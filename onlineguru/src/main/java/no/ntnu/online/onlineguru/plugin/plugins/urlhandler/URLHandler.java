package no.ntnu.online.onlineguru.plugin.plugins.urlhandler;

import java.util.Scanner;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.WandRepository;

public class URLHandler implements Plugin {
	
	private WandRepository wandRepository;
	
	public URLHandler() {
		
	}

	public void addEventDistributor(EventDistributor eventDistributor) {
		eventDistributor.addListener(this, EventType.PRIVMSG);
	}

	public void addWand(WandRepository wandRepository) {
		this.wandRepository = wandRepository;
	}

	public String[] getDependencies() {
		return null;
	}

	public String getDescription() {
		return "Returns information about an URL.";
	}

	public void incomingEvent(Event e) {
		PrivMsgEvent cme = (PrivMsgEvent)e;
		
		if(cme.isChannelMessage()) {
			String channel = cme.getTarget();
			
			Scanner scanner = new Scanner(cme.getMessage());
			String message;
			
			while(scanner.hasNext()) {
				message = scanner.next();
				if(message.startsWith("http") || message.startsWith("www")) {
					if(message.startsWith("www")) {
						message = "http://" + message;
					}
					if(message.startsWith("http://open.spotify.com/")) {
						return;
					}
					new Entry(wandRepository, cme.getNetwork(), message, channel);
				}
			}
		}
	}

	public void loadDependency(Plugin plugin) {
		//Not needed
	}
}
