package no.fictive.irclib.event.container.command;

import no.fictive.irclib.control.IRCEventPacket;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;

public class ErrorEvent extends Event {
	
	private String message;
	
	public ErrorEvent(IRCEventPacket packet, Network network) {
		super(network, packet.getRawline(), EventType.ERROR);
		message = packet.getParameter(0);
	}
	
	public String getMessage() {
		return message;
	}
}
