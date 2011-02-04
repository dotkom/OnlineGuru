package no.fictive.irclib.event.container.command;

import no.fictive.irclib.control.IRCEventPacket;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;

public class CTCPEvent extends Event {
	
	private String sender;
	private String senderIdent;
	private String senderHostname;
	private String target;
	private String message;
	
	public CTCPEvent(IRCEventPacket packet, Network network) {
		super(network, packet.getRawline(), EventType.CTCP);
		
		sender 			= packet.getNick();
		senderHostname 	= packet.getHostname();
		senderIdent 	= packet.getIdent();
		target 			= packet.getParameter(0);
		message 		= packet.getParameter(1).substring(1, packet.getParameter(1).length() - 1);
	}

	public String getSender() {
		return sender;
	}
	
	public String getSenderIdent() {
		return senderIdent;
	}
	
	public String getSenderHostname() {
		return senderHostname;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getTarget() {
		return target;
	}
}
