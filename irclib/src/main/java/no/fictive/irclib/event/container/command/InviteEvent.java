package no.fictive.irclib.event.container.command;

import no.fictive.irclib.control.IRCEventPacket;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;

public class InviteEvent extends Event {
	
	private String sender;
	private String senderIdent;
	private String senderHostname;
	private String channel;
	
	public InviteEvent(IRCEventPacket packet, Network network) {
		super(network, packet.getRawline(), EventType.INVITE);
		channel 		= packet.getParameter(1);
		sender 			= packet.getNick();
		senderIdent 	= packet.getIdent();
		senderHostname 	= packet.getHostname();
	}
	
	public String getSenderIdent() {
		return senderIdent;
	}
	
	public String getSenderHostname() {
		return senderHostname;
	}

	public String getSender() {
		return sender;
	}
	
	public String getChannel() {
		return channel;
	}
}
