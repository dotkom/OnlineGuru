package no.fictive.irclib.event.container.command;

import no.fictive.irclib.control.IRCEventPacket;
import no.fictive.irclib.event.container.Event;
import no.fictive.irclib.event.model.EventType;
import no.fictive.irclib.model.network.Network;

public class PrivMsgEvent extends Event {
	
	private String target;
	private String sender;
	private String senderIdent;
	private String senderHostname;
	private String message;
	
	public PrivMsgEvent(IRCEventPacket packet, Network network) {
		super(network, packet.getRawline(), EventType.PRIVMSG);
		target 	= packet.getParameter(0);
		sender 			= packet.getNick();
		senderHostname 	= packet.getHostname();
		senderIdent 	= packet.getIdent();
		message 		= packet.getParameter(1);
	}
	
	public boolean isChannelMessage() {
		return !target.equals(getNetwork().getProfile().getNickname());
	}
	
	public boolean isPrivateMessage() {
		return target.equals(getNetwork().getProfile().getNickname());
	}

	public String getTarget() {
		return target;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getHostname() {
		return senderHostname;
	}
	
	public String getIdent() {
		return senderIdent;
	}
	
	public String getMessage() {
		return message;
	}
}
