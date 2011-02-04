package no.fictive.irclib.event.container.command;

import no.fictive.irclib.control.IRCEventPacket;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;

public class PingEvent extends Event {
	
	private String server;
	
	public PingEvent(IRCEventPacket packet, Network network) {
		super(network, packet.getRawline(), EventType.PING);
		server = packet.getParameter(0);
	}

	public String getServer() {
		return server;
	}
}
