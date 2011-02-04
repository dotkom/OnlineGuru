package no.fictive.irclib.event.container.command;

import no.fictive.irclib.control.IRCEventPacket;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;

public class KillEvent extends Event {
	
	private String server;
	private String killed;
	private String reason;
	
	public KillEvent(IRCEventPacket packet, Network network) {
		super(network, packet.getRawline(), EventType.KILL);
		server = packet.getServer();
	}
	
	public String getServer() {
		return server;
	}
	
	public String getKilled() {
		return killed;
	}
	
	public String reason() {
		return reason;
	}
}
