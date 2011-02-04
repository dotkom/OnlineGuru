package no.fictive.irclib.event.container.RPL;

import java.util.ArrayList;
import no.fictive.irclib.control.IRCEventPacket;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;


public class RPL_NAMREPLY extends Event {

	private String channel;
	private ArrayList<String> nicks = new ArrayList<String>();
	
	public RPL_NAMREPLY(IRCEventPacket packet, Network network) {
		super(network, packet.getRawline(), EventType.RPL_NAMREPLY);
		channel = packet.getParameter(2);
		for(String nick : packet.getParameter(3).split("\\s")) {
			nicks.add(nick);
		}
	}
	
	public String getChannel() {
		return channel;
	}
	
	public ArrayList<String> getNicks() {
		return nicks;
	}
}
