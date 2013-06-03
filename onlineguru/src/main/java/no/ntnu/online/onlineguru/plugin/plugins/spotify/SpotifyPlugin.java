package no.ntnu.online.onlineguru.plugin.plugins.spotify;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.event.container.command.PrivMsgEvent;
import no.ntnu.online.onlineguru.plugin.control.EventDistributor;
import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.utils.Wand;

public class SpotifyPlugin implements Plugin {
	
	private Wand wand;
	private Pattern linkPattern = Pattern.compile("((http://)?open.spotify.com/(album|artist|track)/([^\\s]+))|(spotify:(album|artist|track):[^\\s]+)");
	private Matcher linkMatcher;
	private Types linkCase;
	
	public String getDescription() {
		return "Returns information about a track, artist or album from the SpotifyPlugin lookup API.";
	}
    /*
        http://open.spotify.com/track/1x6ACsKV4UdWS2FMuPFUiT
                        spotify:track:1x6ACsKV4UdWS2FMuPFUiT
    */
	public void incomingEvent(Event e) {
		String spotifyURI = "";
		if (e.getEventType() == EventType.PRIVMSG) {
			PrivMsgEvent pme = (PrivMsgEvent)e;
			String message = pme.getMessage();
			linkMatcher = linkPattern.matcher(message);
			
			while (linkMatcher.find()) {
				if (linkMatcher.group(1) != null) {
					spotifyURI = "spotify:"+linkMatcher.group(3)+":"+linkMatcher.group(4);
					linkCase = Types.valueOf(linkMatcher.group(3).toUpperCase());
				}
				else if (linkMatcher.group(5) != null) {
					spotifyURI = linkMatcher.group(5);
					linkCase = Types.valueOf(linkMatcher.group(6).toUpperCase());
				}
				
				if (!spotifyURI.isEmpty()) {
					new FetchURI("http://ws.spotify.com/lookup/1/?uri="+spotifyURI, wand, pme.getNetwork(), pme.getTarget(), linkCase);
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

    public Pattern getLinkPattern() {
        return linkPattern;
    }

}