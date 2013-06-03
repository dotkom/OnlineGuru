package no.ntnu.online.onlineguru.plugin.plugins.urlhandler;

import java.util.Scanner;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.fictive.irclib.event.model.EventType;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.Wand;

public class URLHandlerPlugin implements Plugin {

    private final int shorteningThreshhold = 42;


	private Wand wand;
	
	public URLHandlerPlugin() {
		
	}

	public void addEventDistributor(EventDistributor eventDistributor) {
		eventDistributor.addListener(this, EventType.PRIVMSG);
	}

	public void addWand(Wand wand) {
		this.wand = wand;
	}

	public String[] getDependencies() {
		return null;
	}

	public String getDescription() {
		return "Returns the title from an URL. Also supplies a bit.ly shortened url, if the length is more than "+shorteningThreshhold+".";
	}

	public void incomingEvent(Event e) {
		PrivMsgEvent pme = (PrivMsgEvent)e;
		
		if(pme.isChannelMessage()) {
			String channel = pme.getTarget();
			
			Scanner scanner = new Scanner(pme.getMessage());
			String word;
			
			while(scanner.hasNext()) {
				word = scanner.next();
				if(word.startsWith("http") || word.startsWith("www")) {
					if(word.startsWith("www")) {
						word = "http://" + word;
					}
					if( word.startsWith("http://open.spotify.com/track/") ||
                        word.startsWith("http://open.spotify.com/artist/") ||
                        word.startsWith("http://open.spotify.com/album/")) {
						    return;
					}
					new Entry(wand, pme, word);
				}
			}
		}
	}

}
